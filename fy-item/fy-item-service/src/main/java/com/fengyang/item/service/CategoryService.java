package com.fengyang.item.service;

import com.fengyang.item.mapper.CategoryMapper;
import com.fengyang.common.enums.ExceptionEnum;
import com.fengyang.common.exception.FyException;
import com.fengyang.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryListByPid(Long pid) {
        // 查询条件，mapper会把对象中的非空属性作为查询条件
        Category itemCategory = new Category();
        itemCategory.setParentId(pid);
        List<Category> list = categoryMapper.select(itemCategory);
        // 判断结果
        if (CollectionUtils.isEmpty(list)) {
            throw new FyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    public List<Category> queryByIds(List<Long> ids) {
        List<Category> list = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)) {
            throw new FyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }
}
