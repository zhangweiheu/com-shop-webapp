package com.shop.controller;

import com.shop.annotation.AdminOnly;
import com.shop.annotation.LoginRequired;
import com.shop.bean.UserHolder;
import com.shop.bean.vo.GoodsVo;
import com.shop.bean.vo.UserVo;
import com.shop.core.model.Goods;
import com.shop.core.model.OrderForm;
import com.shop.core.model.User;
import com.shop.core.service.GoodsService;
import com.shop.core.service.OrderService;
import com.shop.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by zhang on 2016/3/8.
 */
@Controller
@LoginRequired
@AdminOnly
@RequestMapping("/system")
public class SystemController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    UserService userService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    /**
     * 用户
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ModelAndView user() {
        ModelAndView view = new ModelAndView();
        view.setViewName("system_user");
        return view;
    }

    @RequestMapping(value = "/user/edit/{uid}", method = RequestMethod.GET)
    public ModelAndView editUser(@PathVariable("uid") int uid) {
        ModelAndView view = new ModelAndView();
        User user = userService.findUserByUid(uid);
        if (null != user) {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            userVo.setUpdateAt(null);
            userVo.setCreateAt(null);
            if (UserHolder.getInstance().getUser().getIsAdmin()) {
                userVo.setDelete(user.getIsDelete());
                userVo.setAdmin(user.getIsAdmin());
            }
            view.addObject(userVo);
        }
        view.setViewName("edit_user");
        return view;
    }

    /**
     * 订单
     */
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ModelAndView order() {
        ModelAndView view = new ModelAndView();
        view.setViewName("system_order");
        return view;
    }

    @RequestMapping(value = "/order/edit/{oid}", method = RequestMethod.GET)
    public ModelAndView editOrder(@PathVariable("oid") int oid) {
        ModelAndView view = new ModelAndView();
        OrderForm order = orderService.findOrderByOId(oid);

        view.setViewName("edit_order");
        return view;
    }



    /**
     * 商品
     */
    @RequestMapping(value = "/goods", method = RequestMethod.GET)
    public ModelAndView goods() {
        ModelAndView view = new ModelAndView();
        view.setViewName("system_goods");
        return view;
    }

    @RequestMapping(value = "/goods/edit/{gid}", method = RequestMethod.GET)
    public ModelAndView editGoods(@PathVariable("gid") int gid) {
        ModelAndView view = new ModelAndView();
        Goods goods = goodsService.findGoodsById(gid);
        if (null != goods) {
            GoodsVo goodsVo = new GoodsVo();
            BeanUtils.copyProperties(goods, goodsVo);
            goodsVo.setUpdateAt(null);
            goodsVo.setCreateAt(null);
            view.addObject(goodsVo);
        }
        view.setViewName("edit_goods");
        return view;
    }
}
