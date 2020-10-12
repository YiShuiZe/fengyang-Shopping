package com.fengyang.order.utils;

import com.fengyang.common.enums.ExceptionEnum;
import com.fengyang.common.exception.FyException;
import com.fengyang.order.config.PayConfig;
import com.fengyang.order.enums.OrderStatusEnum;
import com.fengyang.order.enums.PayState;
import com.fengyang.order.mapper.OrderMapper;
import com.fengyang.order.mapper.OrderStatusMapper;
import com.fengyang.order.pojo.Order;
import com.fengyang.order.pojo.OrderStatus;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PayHelper {

    @Autowired
    private WXPay wxPay;

    @Autowired
    private PayConfig payConfig;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderStatusMapper statusMapper;

    public String createPayUrl(Long orderId, String description, Long totalPay) {

        //从缓存中取出支付连接
        /*String key = "order:pay:url:" + orderId;
        try {
            String url = redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotBlank(url)) {
                return url;
            }
        } catch (Exception e) {
            log.error("查询缓存付款链接异常，订单号：{}", orderId, e);
        }*/

        try {
            Map<String, String> data = new HashMap<>();
            //商品描述
            data.put("body", description);
            //订单号
            data.put("out_trade_no", orderId.toString());
            //货币（默认就是人民币）
            //data.put("fee_type", "CNY");
            //总金额
            data.put("total_fee", totalPay.toString());
            //调用微信支付的终端ip
            data.put("spbill_create_ip", "127.0.0.1");
            //回调地址
            data.put("notify_url", payConfig.getNotifyUrl());
            //交易类型为扫码支付
            data.put("trade_type", "NATIVE");

            //利用wxPay工具，完成下单
            Map<String, String> result = wxPay.unifiedOrder(data);

            // 判断通信和业务标识
            isSuccess(result);


            //下单成功，获取支付连接
            String url = result.get("code_url");

            //将链接缓存到Redis中，失效时间2小时
            /*try {
                this.redisTemplate.opsForValue().set(key, url, 2, TimeUnit.HOURS);
            } catch (Exception e) {
                log.error("【微信下单】缓存付款链接异常,订单编号：{}", orderId, e);
            }*/
            return url;
        } catch (Exception e) {
            log.error("[微信下单] 创建预交易订单异常", e);
            return null;
        }
    }

    public void isSuccess(Map<String, String> result) {
        // 判断通信标识  ctrl+alt+m 将选中的代码块封装成方法
        String returnCode = result.get("return_code");
        //通信失败
        if (WXPayConstants.FAIL.equals(returnCode)) {
            log.error("【微信下单】与微信通信失败，失败信息：{}", result.get("return_msg"));
            throw new FyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }

        // 判断业务标识
        String resultCode = result.get("result_code");
        //下单失败
        if (WXPayConstants.FAIL.equals(resultCode)) {
            log.error("【微信下单】创建预交易订单失败，错误码：{}，错误信息：{}",
                    result.get("err_code"), result.get("err_code_des"));
            throw new FyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }
    }

    public void isValidSign(Map<String, String> data) {
        try {
            // 重新生成签名
            String sign1 = WXPayUtil.generateSignature(data, payConfig.getKey(), WXPayConstants.SignType.HMACSHA256);
            String sign2 = WXPayUtil.generateSignature(data, payConfig.getKey(), WXPayConstants.SignType.MD5);

            // 和传过来的签名进行比较
            String sign = data.get("sign");
            if (!StringUtils.equals(sign, sign1) && !StringUtils.equals(sign, sign2)) {
                throw new FyException(ExceptionEnum.INVALID_SIGN_ERROR);
            }
        } catch (Exception e) {
            throw new FyException(ExceptionEnum.INVALID_SIGN_ERROR);
        }
    }


    public PayState queryPayState(Long orderId) {
        try {
            // 组织请求参数
            Map<String, String> data = new HashMap<>();
            // 订单号
            data.put("out_trade_no", orderId.toString());
            // 查询状态
            Map<String, String> result = wxPay.orderQuery(data);

            // 校验通信状态
            isSuccess(result);

            // 校验签名
            isValidSign(result);

            // 3 检验金额
            String totalFeeStr = result.get("total_fee");
            String tradeNo = result.get("out_trade_no");
            if (StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(tradeNo)) {
                throw new FyException(ExceptionEnum.INVALID_ORDER_PARAM);
            }
            // 3.1 获取结果中的金额
            Long totalFee = Long.valueOf(totalFeeStr);
            // 3.2 获取订单金额
            Order order = orderMapper.selectByPrimaryKey(orderId);
            if (totalFee != order.getActualPay()) {
                // 金额不符
                throw new FyException(ExceptionEnum.INVALID_ORDER_PARAM);
            }

            /**
             * SUCCESS—支付成功
             *
             * REFUND—转入退款
             *
             * NOTPAY—未支付
             *
             * CLOSED—已关闭
             *
             * REVOKED—已撤销（付款码支付）
             *
             * USERPAYING--用户支付中（付款码支付）
             *
             * PAYERROR--支付失败(其他原因，如银行返回失败)
             */
            String state = result.get("trade_state");
            if (WXPayConstants.SUCCESS.equals(state)) {
                // 支付成功
                // 修改订单状态
                OrderStatus status = new OrderStatus();
                status.setStatus(OrderStatusEnum.PAY_UP.value());
                status.setOrderId(orderId);
                status.setPaymentTime(new Date());
                int count = statusMapper.updateByPrimaryKeySelective(status);
                if (count != 1) {
                    throw new FyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
                }
                // 返回成功
                return PayState.SUCCESS;
            }
            if ("NOTPAY".equals(state) || "USERPAYING".equals(state)) {
                return PayState.NOT_PAY;
            }

            return PayState.FAIL;

        } catch (Exception e) {
            return PayState.NOT_PAY;
        }
    }
}
