package com.shop.controller;

import com.shop.annotation.NotNeedLogin;
import com.shop.core.service.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by zhangwei on 16/1/25.
 */
@Controller
@NotNeedLogin
@RequestMapping("")
public class IndexController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    GoodsService goodsService;
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView index(ModelAndView view) {

        view.addObject("first",goodsService.findGoodsById(3));
        view.setViewName("index");
        return view;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView index1(ModelAndView view) {
        view.setViewName("index");
        return view;
    }
}
