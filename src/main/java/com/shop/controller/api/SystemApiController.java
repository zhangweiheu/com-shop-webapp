package com.shop.controller.api;


import com.shop.annotation.AdminOnly;
import com.shop.annotation.LoginRequired;
import com.shop.bean.JsonResponse;
import com.shop.bean.Page;
import com.shop.bean.UserHolder;
import com.shop.bean.vo.GoodsVo;
import com.shop.bean.vo.OrderVo;
import com.shop.bean.vo.UserVo;
import com.shop.core.alipay.demo.PassUtil;
import com.shop.core.dao.OrderDetailDao;
import com.shop.core.model.Goods;
import com.shop.core.model.OrderForm;
import com.shop.core.model.OrderDetail;
import com.shop.core.model.User;
import com.shop.core.service.GoodsService;
import com.shop.core.service.OrderService;
import com.shop.core.service.UserService;
import com.shop.core.util.PhoneNumberUtil;
import com.shop.core.util.PhotoUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhang on 2016/3/8.
 */
@RestController
@LoginRequired
@AdminOnly
@RequestMapping("/api/system")
public class SystemApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemApiController.class);

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderDetailDao orderDetailDao;


    /**
     * 系统用户管理
     */
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public JsonResponse getUserList(Page page) {
        List<User> userList = userService.listAllUser(page.getOffset(), page.getPageSize());
        List<UserVo> userVoList = new ArrayList<>();
        //日期处理
        for (User user : userList) {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            userVo.setProperties("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.getCreateAt()));
            userVo.setUpdateAt(null);
            userVo.setPassword(null);
            userVo.setCreateAt(null);
            userVo.setAdmin(user.getIsAdmin());
            userVo.setDelete(user.getIsDelete());

            userVoList.add(userVo);
        }
        page.setTotalCount(userService.getTotalCount());
        page.setData(userVoList);
        return JsonResponse.success(page);
    }

    @RequestMapping(value = "/user/{uid}", method = RequestMethod.DELETE)
    public JsonResponse deleteUserById(@PathVariable("uid") int uid) {
        userService.deleteUserByUid(uid);
        return JsonResponse.success();
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public JsonResponse updateUser(UserVo userVo, HttpServletRequest request) {
        User user = new User();
        BeanUtils.copyProperties(userVo, user);
        user.setUpdateAt(new Date());
        if (null != userVo.getFile()) {
            user.setAvatar(PhotoUploadUtil.uploadPhoto(userVo.getFile(), request, user.getId()));
        }
        userService.updateUser(user);
        return JsonResponse.success();
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public JsonResponse saveUser(UserVo userVo, HttpServletRequest request) {
        if (null != userService.findUserByName(userVo.getUsername())) {
            return JsonResponse.failed("已存在相同用户名");
        }
        if (null == userVo.getId()) {
            User user = new User();
            BeanUtils.copyProperties(userVo, user);
            user.setCreateAt(new Date());
            user.setUpdateAt(new Date());
            userService.saveUser(user);
            if (null != userVo.getFile()) {
                user.setAvatar(PhotoUploadUtil.uploadPhoto(userVo.getFile(), request, user.getId()));
            }
            userService.updateUser(user);
        } else {
            User user = new User();
            BeanUtils.copyProperties(userVo, user);
            user.setUpdateAt(new Date());
            if (null != userVo.getFile()) {
                user.setAvatar(PhotoUploadUtil.uploadPhoto(userVo.getFile(), request, user.getId()));
            }
            userService.updateUser(user);
        }

        return JsonResponse.success();
    }


    /**
     * 系统订单管理
     */
    @RequestMapping(value = "/order/list", method = RequestMethod.GET)
    public JsonResponse getOrderList(Page page) {
        List<OrderForm> orderList = orderService.listAllOrder(page.getOffset(), page.getPageSize());
        List<OrderVo> orderVoList = new ArrayList<>();
        //日期处理
        for (OrderForm order : orderList) {
            OrderVo orderVo = new OrderVo();
            BeanUtils.copyProperties(order, orderVo);
            orderVo.setProperties("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateAt()));
            orderVo.setUpdateAt(null);
            orderVo.setCreateAt(null);

            orderVoList.add(orderVo);
        }
        page.setTotalCount(orderService.getTotalCount());
        page.setData(orderVoList);
        return JsonResponse.success(page);
    }

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

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public JsonResponse saveOrder(OrderVo orderVo) {
        OrderForm order = new OrderForm();
        BeanUtils.copyProperties(orderVo, order);
        order.setCreateAt(new Date());
        order.setCreateAt(new Date());
        orderService.saveOrder(order);

        List<OrderDetail> orderDetails;
        orderDetails = orderVo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setOrderId(order.getId());
            orderDetail.setUpdateAt(new Date());
            orderDetail.setCreateAt(new Date());
            orderDetailDao.saveOrderDetail(orderDetail);
        }
        return JsonResponse.success();
    }

    @RequestMapping(value = "/order/{oid}", method = RequestMethod.DELETE)
    public JsonResponse deleteOrder(@PathVariable("oid") int oid) {
        orderService.deleteOrderById(oid);
        return JsonResponse.success();
    }



    /**
     * 系统商品管理
     */
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
            if (!user.getIsAdmin()) {
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

    @RequestMapping(value = "/goods", method = RequestMethod.PUT)
    public JsonResponse updateGoods(GoodsVo goodsVo) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsVo, goods);
        goodsService.updateGoods(goods);
        return JsonResponse.success();
    }

    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    public JsonResponse saveGoods(@ModelAttribute GoodsVo goodsVo, HttpServletRequest request) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsVo, goods);
        goods.setUpdateAt(new Date());

        if (null == goodsVo.getId()) {
            goods.setCreateAt(new Date());
            goodsService.saveGoods(goods);
        }

        if (!goodsVo.getFile().isEmpty()) {
            goods.setLinkPhoto(PhotoUploadUtil.uploadPhoto(goodsVo.getFile(), request, goods.getId()));
        }

        goodsService.updateGoods(goods);
        return JsonResponse.success();
    }

    @RequestMapping(value = "/goods/{gid}", method = RequestMethod.DELETE)
    public JsonResponse deleteGoods(@PathVariable("gid") int gid) {
        goodsService.deleteGoodsById(gid);
        return JsonResponse.success();
    }

    @RequestMapping(value = "/pass", method = RequestMethod.POST)
    public JsonResponse addPass(@RequestParam("phoneNumber") String phoneNumber) {
        PassUtil passUtil = new PassUtil();
        if(!PhoneNumberUtil.isMobileNum(phoneNumber)){
            return JsonResponse.failed("手机号不对！");
        }
        if(passUtil.addPassInstance(phoneNumber)){
            return JsonResponse.success();
        }
        return JsonResponse.failed("添加失败");
    }
}
