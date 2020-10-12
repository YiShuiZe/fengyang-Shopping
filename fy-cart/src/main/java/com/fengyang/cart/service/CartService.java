package com.fengyang.cart.service;

import com.fengyang.auth.pojo.UserInfo;
import com.fengyang.cart.interceptor.UserIntercpetor;
import com.fengyang.cart.pojo.Cart;
import com.fengyang.common.enums.ExceptionEnum;
import com.fengyang.common.exception.FyException;
import com.fengyang.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_RREFIX = "cart:uid:";

    public void addCart(Cart cart) {
        // 获取登录用户
        UserInfo user = UserIntercpetor.getUser();
        // key
        String key = KEY_RREFIX + user.getId();
        // hashKey
        String hashKey = cart.getSkuId().toString();
        // 记录num
        Integer num = cart.getNum();
        // 获取登录用户的所有购物车
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        // 判断当前购物车商品，是否存在
        if (operation.hasKey(hashKey)) {
            // 存在，修改数量
            String json = operation.get(hashKey).toString();
            cart = JsonUtils.toBean(json, Cart.class);
            cart.setNum(cart.getNum() + num);
        }
        // 写回redis
        operation.put(hashKey, JsonUtils.toString(cart));
    }

    public List<Cart> queryCartList() {
        // 获取登录用户
        UserInfo user = UserIntercpetor.getUser();
        // key
        String key = KEY_RREFIX + user.getId();

        if (!redisTemplate.hasKey(key)) {
            // key不存在，返回404
            throw new FyException(ExceptionEnum.CART_NOT_FOUND);
        }

        // 获取登录用户的所有购物车
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        List<Cart> carts = operation.values().stream().map(o -> JsonUtils.toBean(o.toString(), Cart.class))
                .collect(Collectors.toList());
        return carts;
    }

    public void updateNum(Long skuId, Integer num) {
        // 获取登录用户
        UserInfo user = UserIntercpetor.getUser();
        // key
        String key = KEY_RREFIX + user.getId();
        // hashKey
        String hashKey = skuId.toString();

        // 获取操作
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);

        // 判断是否存在
        if (!operations.hasKey(hashKey)) {
             throw new FyException(ExceptionEnum.CART_NOT_FOUND);
        }
        // 查询
        Cart cart = JsonUtils.toBean(operations.get(skuId.toString()).toString(), Cart.class);
        cart.setNum(num);

        // 写会redis
        operations.put(hashKey, JsonUtils.toString(cart));
    }

    public void deleteCart(Long skuId) {
        // 获取登录用户
        UserInfo user = UserIntercpetor.getUser();
        // key
        String key = KEY_RREFIX + user.getId();

        // 删除
        redisTemplate.opsForHash().delete(key, skuId.toString());
    }
}
