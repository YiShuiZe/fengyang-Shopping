package com.fengyang.cart.interceptor;

import com.fengyang.auth.pojo.UserInfo;
import com.fengyang.auth.utils.JwtUtils;
import com.fengyang.cart.config.JwtProperties;
import com.fengyang.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserIntercpetor implements HandlerInterceptor {

    private JwtProperties prop;

    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    public UserIntercpetor(JwtProperties prop) {
        this.prop = prop;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取cookie
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        try {
            // 解析token
            UserInfo userInfo = JwtUtils.getUserInfo(prop.getPublicKey(), token);
            // 传递userInfo
            tl.set(userInfo);
            // 放行
            return true;
        } catch (Exception e) {
            log.error("[购物车服务] 解析用户身份失败.", e);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 最后用完数据，一定要清空
        tl.remove();
    }

    public static UserInfo getUser() {
        return tl.get();
    }
}
