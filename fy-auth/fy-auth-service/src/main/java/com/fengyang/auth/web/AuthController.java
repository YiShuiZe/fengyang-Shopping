package com.fengyang.auth.web;

import com.fengyang.auth.config.JwtProperties;
import com.fengyang.auth.pojo.UserInfo;
import com.fengyang.auth.service.AuthService;
import com.fengyang.auth.utils.JwtUtils;
import com.fengyang.common.enums.ExceptionEnum;
import com.fengyang.common.exception.FyException;
import com.fengyang.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${ly.jwt.cookieName}")
    private String cookieName;

    @Autowired
    private JwtProperties prop;

    /**
     * 登录授权
     * @param password
     * @return
     * @param username
     */
    @PostMapping("login")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletResponse response,
            HttpServletRequest request) {
        // 登录
        String token = authService.login(username, password);
        // TODO 写入cookie
        CookieUtils.newBuilder(response).httpOnly().request(request)
                .build(cookieName, token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 校验用户登录状态  鉴权
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(
            @CookieValue("LY_TOKEN") String token,
            HttpServletResponse response, HttpServletRequest request
    ) {
        try {
            // 解析token
            UserInfo userInfo = JwtUtils.getUserInfo(prop.getPublicKey(), token);

            //刷新token，重新生成token
            String newToken = JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExpire());

            // 写入cookie
            CookieUtils.newBuilder(response).httpOnly().request(request)
                    .build(cookieName, newToken);

            // 已登录，返回用户信息
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            // token已过期，或者token被篡改
            throw new FyException(ExceptionEnum.UNAUTHRIZED);
        }

    }
}
