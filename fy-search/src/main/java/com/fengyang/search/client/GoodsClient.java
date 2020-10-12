package com.fengyang.search.client;

import com.fengyang.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
