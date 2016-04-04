package com.shop.controller.api;

import com.shop.annotation.LoginRequired;
import com.shop.annotation.NotNeedLogin;
import com.shop.bean.JsonResponse;
import com.shop.bean.Page;
import com.shop.bean.UserHolder;
import com.shop.bean.vo.GoodsVo;
import com.shop.bean.vo.OrderDetailVo;
import com.shop.bean.vo.OrderVo;
import com.shop.bean.vo.UserVo;
import com.shop.core.alipay.demo.PassUtil;
import com.shop.core.dao.OrderDetailDao;
import com.shop.core.model.Goods;
import com.shop.core.model.OrderDetail;
import com.shop.core.model.OrderForm;
import com.shop.core.model.User;
import com.shop.core.mybatis.ExpressStatusEnum;
import com.shop.core.service.CartService;
import com.shop.core.service.GoodsService;
import com.shop.core.service.OrderService;
import com.shop.core.service.UserService;
import com.shop.core.util.PhotoUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangwei on 16/1/25.
 */

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderDetailDao orderDetailDao;

    @Autowired
    CartService cartService;

    /**
     * 用户资料
     */
    @LoginRequired
    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public JsonResponse getUserById(@PathVariable("uid") int uid) {
        if (!checkprivilege(uid)) {
            return JsonResponse.failed();
        }
        User user = userService.findUserByUid(uid);
        user.setPassword(null);
        return JsonResponse.success(user);
    }

    @LoginRequired
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public JsonResponse updateUser(User user) {
        if (!checkprivilege(user.getId())) {
            return JsonResponse.failed();
        }
        return JsonResponse.success(userService.updateUser(user));
    }

    @LoginRequired
    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResponse saveUser(UserVo userVo, HttpServletRequest request) {
        if (!userVo.getUsername().equals(UserHolder.getInstance().getUser().getUsername())
                && null != userService.findUserByName(userVo.getUsername())) {
            return JsonResponse.failed("已存在相同用户名");
        }
        User user = new User();
        BeanUtils.copyProperties(userVo, user);
        user.setUpdateAt(new Date());
        if (null == userVo.getId()) {
            user.setCreateAt(new Date());
            user.setIsAdmin(false);
            userService.saveUser(user);
        }
        if (!userVo.getFile().isEmpty()) {
            user.setAvatar(PhotoUploadUtil.uploadPhoto(userVo.getFile(), request, user.getId()));
        }
        userService.updateUser(user);
        return JsonResponse.success();
    }

    private Boolean checkprivilege(Integer uid) {
        return uid.equals(UserHolder.getInstance().getUser().getId()) || UserHolder.getInstance().getUser().getIsAdmin();
    }

    /**
     * 用户订单
     */
    @LoginRequired
    @RequestMapping(value = "/order/list", method = RequestMethod.GET)
    public JsonResponse getOrderList(Page page) {
        OrderForm order = new OrderForm();
        order.setUid(UserHolder.getInstance().getUser().getId());
        List<OrderForm> orderList = orderService.listAllOrderByAttr(page.getOffset(), page.getPageSize(), order);
        List<OrderVo> orderVoList = new ArrayList<>();
        //日期处理
        for (OrderForm order1 : orderList) {
            OrderVo orderVo = new OrderVo();
            BeanUtils.copyProperties(order1, orderVo);
            orderVo.setProperties("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order1.getCreateAt()));
            orderVo.setUpdateAt(null);
            orderVo.setCreateAt(null);

            orderVoList.add(orderVo);
        }
        page.setTotalCount(orderService.getTotalCountByAttr(order));
        page.setData(orderVoList);
        return JsonResponse.success(page);
    }

    @LoginRequired
    @RequestMapping(value = "/order", method = RequestMethod.PUT)
    public JsonResponse updateOrder(OrderVo orderVo) {
        OrderForm order = new OrderForm();
        BeanUtils.copyProperties(orderVo, order);
        List<OrderDetail> orderDetails;
        orderDetails = orderVo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setUpdateAt(new Date());
            orderDetailDao.updateOrderDetail(orderDetail);
        }
        orderService.updateOrder(order);
        return JsonResponse.success();
    }

    @LoginRequired
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public JsonResponse saveOrder(OrderVo orderVo) {
        List<Integer> integerList = convertidsString2ID(orderVo.getIds());

        if (null == integerList) {
            return JsonResponse.failed("订单不能为空");
        }

        Boolean result = false;

        PassUtil passUtil = new PassUtil();
        result = passUtil.updatePassInstance(orderVo.getSerialNumber());

        if (!result) {
            return JsonResponse.failed("支付失败");
        }

        OrderForm order = new OrderForm();
        BeanUtils.copyProperties(orderVo, order);
        order.setCreateAt(new Date());
        order.setUpdateAt(new Date());
        order.setSerialNumber(orderVo.getSerialNumber());
        order.setUid(UserHolder.getInstance().getUser().getId());
        order.setExpressStatus(ExpressStatusEnum.BEGIN_PROCESSED);
        orderService.saveOrder(order);

        Double orderPrice = 0.0;

        for (Integer id : integerList) {
            OrderDetail orderDetail = orderDetailDao.findOrderDetailById(id);
            orderDetail.setOrderId(order.getId());
            orderDetail.setUpdateAt(new Date());

            orderDetailDao.updateOrderDetail(orderDetail);
            Goods goods = goodsService.findGoodsById(orderDetail.getGoodsId());
            goods.setSellCount(goods.getSellCount() + orderDetail.getGoodsCount());
            goods.setRemain(goods.getRemain() - orderDetail.getGoodsCount());
            goodsService.updateGoods(goods);

            orderPrice += orderDetail.getTotalPrice();
        }

        order.setOrderPrice(orderPrice);
        orderService.updateOrder(order);
        User user = UserHolder.getInstance().getUser();
        user.setIntegration(user.getIntegration() + order.getOrderPrice());
        userService.updateUser(user);

        return JsonResponse.success();
    }

    @LoginRequired
    @RequestMapping(value = "/order/{oid}", method = RequestMethod.DELETE)
    public JsonResponse deleteOrder(@PathVariable("oid") int oid) {
        orderService.deleteOrderById(oid);
        return JsonResponse.success();
    }

    /**
     * 用户cart
     */
    @LoginRequired
    @RequestMapping(value = "/cart/list", method = RequestMethod.GET)
    public JsonResponse getCartList(@ModelAttribute Page page) {
        int uid = UserHolder.getInstance().getUser().getId();
        List<OrderDetail> orderDetailList = cartService.listUserAllCart(page.getOffset(), page.getPageSize(), uid);
        List<OrderDetailVo> orderDetailVos = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            OrderDetailVo orderDetailVo = new OrderDetailVo();
            BeanUtils.copyProperties(orderDetail, orderDetailVo);
            orderDetailVo.setUpdateAt(null);
            orderDetailVo.setCreateAt(null);
            orderDetailVo.setProperties("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderDetail.getCreateAt()));
            orderDetailVo.setProperties("goodsName", goodsService.findGoodsById(orderDetail.getGoodsId()).getName());
            orderDetailVos.add(orderDetailVo);
        }
        page.setData(orderDetailVos);
        page.setTotalCount(cartService.getTotalCountByUid(uid));
        return JsonResponse.success(page);
    }

    @LoginRequired
    @RequestMapping(value = "/cart", method = RequestMethod.POST)
    public JsonResponse addGoods2Cart(@RequestParam("id") int id, @RequestParam("count") int count) {
        Goods goods = goodsService.findGoodsById(id);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setGoodsId(id);
        orderDetail.setUid(UserHolder.getInstance().getUser().getId());

        OrderDetail oldOrderDetail = cartService.findOrderDetailByAttr(orderDetail);

        orderDetail.setGoodsCount(count);
        orderDetail.setGoodsPrice(goods.getPrice());
        orderDetail.setTotalPrice(goods.getPrice() * count);
        orderDetail.setUpdateAt(new Date());

        if (null == oldOrderDetail) {
            orderDetail.setCreateAt(new Date());
            cartService.saveCart(orderDetail);
            return JsonResponse.success();
        }
        oldOrderDetail.setGoodsCount(oldOrderDetail.getGoodsCount() + orderDetail.getGoodsCount());
        oldOrderDetail.setTotalPrice(oldOrderDetail.getGoodsCount() * oldOrderDetail.getGoodsPrice());
        cartService.updateCart(oldOrderDetail);

        return JsonResponse.success();
    }

    @LoginRequired
    @RequestMapping(value = "/cart/{odid}", method = RequestMethod.DELETE)
    public JsonResponse deleteOrderDetail(@PathVariable("odid") int odid) {
        cartService.deleteCartByODid(odid);
        return JsonResponse.success();
    }

    /**
     * 商品
     */
    @NotNeedLogin
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    public JsonResponse getGoodsList(@ModelAttribute Page page) {
        List<Goods> goodsList = goodsService.listAllGoodsByAttr(page.getOffset(), page.getPageSize(), null);
        List<GoodsVo> goodsVos = new ArrayList<>();
        User user = UserHolder.getInstance().getUser();
        for (Goods goods : goodsList) {
            GoodsVo goodsVo = new GoodsVo();
            BeanUtils.copyProperties(goods, goodsVo);
            goodsVo.setProperties("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(goods.getCreateAt()));
            goodsVo.setCreateAt(null);
            goodsVo.setUpdateAt(null);
            if (null == user || !user.getIsAdmin()) {
                goodsVo.setProviderId(null);
                goodsVo.setProviderName(null);
                goodsVo.setProviderPhone(null);
            }
            goodsVos.add(goodsVo);
        }
        page.setTotalCount(goodsService.getTotalCount());
        page.setData(goodsVos);
        return JsonResponse.success(page);
    }


    private List<Integer> convertidsString2ID(String ids) {
        List<Integer> idList = new ArrayList<>();
        if (StringUtils.isEmpty(ids)) {
            return null;
        }
        String[] chars = ids.split(",");
        for (String c : chars) {
            idList.add(Integer.valueOf(c));
        }
        return idList;
    }

}
