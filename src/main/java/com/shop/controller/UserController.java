package com.shop.controller;

import com.shop.annotation.LoginRequired;
import com.shop.bean.UserHolder;
import com.shop.bean.vo.UserVo;
import com.shop.core.model.User;
import com.shop.core.service.UserService;
import com.shop.core.util.PhotoUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by zhangwei on 16/1/25.
 */
@Controller
@LoginRequired
@RequestMapping("/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String User(ModelMap view) {
        User user = UserHolder.getInstance().getUser();
        view.addAttribute("userVo", user);
        return "user";
    }

    @RequestMapping(value = "/user/edit", method = RequestMethod.GET)
    public String getEditUser(ModelMap view) {
        User user = UserHolder.getInstance().getUser();
        view.addAttribute(user);
        return "edit_user";
    }

    @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
    public String editUser(UserVo userVo, HttpServletRequest request, ModelMap view) {
        User user1 = userService.findUserByName(userVo.getUsername());
        if (null != user1 && !user1.getId().equals(userVo.getId())) {
            view.addAttribute("userVo", userVo);
            return "edit_user";
        }

        User user = new User();
        BeanUtils.copyProperties(userVo, user);
        user.setUpdateAt(new Date());
        user.setIsAdmin(userVo.getAdmin());
        user.setIsDelete(userVo.getDelete());
        if (null == userVo.getId()) {
            user.setCreateAt(new Date());
            user.setIsAdmin(false);
            userService.saveUser(user);
        }
        if (!userVo.getFile().isEmpty()) {
            user.setAvatar(PhotoUploadUtil.uploadPhoto(userVo.getFile(), request, user.getId()));
        }
        userService.updateUser(user);
        view.addAttribute("userVo", userVo);
        return "edit_user";
    }

    /**
     * 我的购物车
     */
    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String cart() {
        return "cart";
    }

    /**
     * 我的订单
     */
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public String order() {
        return "order";
    }
}
