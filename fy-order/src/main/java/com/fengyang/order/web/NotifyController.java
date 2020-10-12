package com.fengyang.order.web;

import com.fengyang.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("wxpay")
public class NotifyController {

    @Autowired
    private OrderService orderService;

    /**
     * 微信支付成功回调
     * @param result
     * @return
     */
    @GetMapping(value = "notify", produces = "application/xml")
    public Map<String, String> wxPayCallBack(@RequestBody Map<String, String> result) {
        // 处理回调
        orderService.handleNotify(result);

        log.info("[支付回调] 接收微信支付回调， 结果:{}", result);

        // 返回成功
        Map<String, String> msg = new HashMap<>();
        msg.put("return_code", "SUCCESS");
        msg.put("return_msg", "OK");
        return msg;
    }
}
