package com.fengyang.search.pojo;

import com.fengyang.common.vo.PageResult;
import com.fengyang.item.pojo.Brand;
import com.fengyang.item.pojo.Category;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResult extends PageResult<Goods> {

    private List<Category> categories; // 分类待选项

    private List<Brand> brands; // 品牌待选项

    private List<Map<String, Object>> specs; // 规格参数 key及待选项

    public SearchResult() {
    }

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
}

