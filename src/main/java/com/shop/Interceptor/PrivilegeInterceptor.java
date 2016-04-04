package com.shop.Interceptor;

import com.shop.annotation.AdminOnly;
import com.shop.bean.UserHolder;
import com.shop.core.model.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 控制权限
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class PrivilegeInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PrivilegeInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AdminOnly adminOnly = null;
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            adminOnly = method.getMethodAnnotation(AdminOnly.class);

            if (adminOnly == null)
                adminOnly = method.getBeanType().getAnnotation(AdminOnly.class);
        }

        if (adminOnly == null) {
            // 不需要管理员权限，放过检查
            return true;
        }

        User user = UserHolder.getInstance().getUser();
        if (user != null) {
            if (user.getIsAdmin())
                return true;
            else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 将参数值中的“[]”进行转义
     *
     * @param value
     * @return
     */
    public static String escape(String value) {
        if (StringUtils.isNotBlank(value)) {
            return value.replace("[", "\\[").replace("]", "\\]");
        }
        return "";
    }
}
