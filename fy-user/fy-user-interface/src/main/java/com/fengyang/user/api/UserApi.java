package com.fengyang.user.api;

import com.fengyang.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {

    @GetMapping("/query")
    User queryUserByUsernameAndPassword (
            @RequestParam("username") String username,
            @RequestParam("password") String password
    );
}
