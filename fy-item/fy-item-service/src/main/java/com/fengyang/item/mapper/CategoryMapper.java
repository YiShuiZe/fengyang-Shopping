package com.fengyang.item.mapper;

import com.fengyang.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category, Long> {
}
