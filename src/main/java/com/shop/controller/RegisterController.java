package com.shop.controller;

import com.shop.annotation.NotNeedLogin;
import com.shop.bean.vo.UserVo;
import com.shop.core.alipay.demo.PassUtil;
import com.shop.core.model.User;
import com.shop.core.service.UserService;
import com.shop.core.util.PhoneNumberUtil;
import com.shop.core.util.PhotoUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhang on 2016/2/20.
 */
@Controller
@NotNeedLogin
@RequestMapping("/register")
public class RegisterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;

    @NotNeedLogin
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String register() {
        return "register";
    }

    @NotNeedLogin
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ModelAndView registerForm(@ModelAttribute UserVo userVo, ModelAndView modelAndView, HttpServletRequest request) {
        if (null == userService.findUserByName(userVo.getUsername())) {
            User user = new User();
            BeanUtils.copyProperties(userVo, user);
            user.setIsAdmin(false);
            user.setIsDelete(false);
            int uid = userService.saveUser(user);

            if (null != userVo.getFile()) {
                user.setAvatar(PhotoUploadUtil.uploadPhoto(userVo.getFile(), request, uid));
            }
            userService.updateUser(user);
            String msg = "您已经成功注册，请登录";
            if (null != user.getPhone() && PhoneNumberUtil.isMobileNum(user.getPhone())) {
                PassUtil passUtil = new PassUtil();
                if (passUtil.addPassInstance(user.getPhone())) {
                    msg = "您已经成功注册,并且i美妆送您一次免单优惠券，详查支付宝，请登录使用！";
                }
            }

            modelAndView.getModelMap().addAttribute("msg", msg);
            modelAndView.setViewName("login");
        } else {
            modelAndView.addObject("user", userVo);
            modelAndView.setViewName("register");
        } return modelAndView;
    }
}
