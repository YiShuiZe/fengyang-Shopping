package com.fengyang.gateway.filters;

import com.fengyang.auth.pojo.UserInfo;
import com.fengyang.auth.utils.JwtUtils;
import com.fengyang.common.utils.CookieUtils;
import com.fengyang.gateway.config.FilterProperties;
import com.fengyang.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties prop;

    @Autowired
    private FilterProperties filterProp;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;//过滤器类型，前置过滤
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;//过滤器顺序
    }

    @Override
    public boolean shouldFilter() {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();

        // 获取请求的url路径
        String path = request.getRequestURI();

        // 如果是商品微服务的Get请求，可以放行
        // String method = request.getMethod();

        // 判断是否放行。放行，则返回false
        return !isAllowPath(path);//是否过滤
    }

    private boolean isAllowPath(String path) {
        for (String allowPath : filterProp.getAllowPaths()) {
            // 判断是否允许
            if (path.startsWith(allowPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();
        // 获取cookie中的token
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());

        try {
            // 解析token
            UserInfo userInfo = JwtUtils.getUserInfo(prop.getPublicKey(), token);
            // TODO 校验权限
        } catch (Exception e) {
            // 解析token失败，未登录，拦截
            ctx.setSendZuulResponse(false);
            // 返回状态码
            ctx.setResponseStatusCode(403);
        }
        return null;
    }
}
