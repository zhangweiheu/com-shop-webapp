package com.shop.controller;

import com.shop.model.User;
import com.shop.service.UserService;
import com.shop.util.PhotoUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhang on 2016/2/20.
 */
@Controller
@RequestMapping("/register")
public class RegisterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String register() {
        return "register";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ModelAndView registerForm(@ModelAttribute User user, ModelAndView modelAndView, @RequestParam("avatarfile") MultipartFile file, HttpServletRequest request) {
        if (null == userService.findUserByName(user.getUsername())) {

            int uid = userService.saveUser(user);

            if(null != file){
                user.setAvatar(PhotoUploadUtil.uploadPhoto(file,request,uid));
            }
            userService.updateUser(user);
            modelAndView.getModelMap().addAttribute("msg","您已经成功注册，请登录");
            modelAndView.setViewName("login");
        } else {
            modelAndView.addObject("user",user);
            modelAndView.setViewName("register");
        }
        return modelAndView;
    }


}
