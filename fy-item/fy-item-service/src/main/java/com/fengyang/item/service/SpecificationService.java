package com.fengyang.item.service;

import com.fengyang.common.enums.ExceptionEnum;
import com.fengyang.common.exception.FyException;
import com.fengyang.item.mapper.SpecGroupMapper;
import com.fengyang.item.mapper.SpecParamMapper;
import com.fengyang.item.mapper.SpecificationMapper;
import com.fengyang.item.pojo.SpecGroup;
import com.fengyang.item.pojo.SpecParam;
import com.fengyang.item.pojo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper paramMapper;

    public List<SpecGroup> queryGroupByCid(Long cid) {
        // 查询条件
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        // 查询
        List<SpecGroup> list = groupMapper.select(group);
        if(CollectionUtils.isEmpty(list)) {
            // 没查到
            throw new FyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    public List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setSearching(searching);

        List<SpecParam> list = paramMapper.select(param);
        if (CollectionUtils.isEmpty(list)) {
            // 没查到
            throw new FyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }

    public List<SpecGroup> queryListByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> specGroups = queryGroupByCid(cid);
        // 查询当前分类下的参数
        List<SpecParam> specParams = queryParamList(null, cid, null);

        // 先把规格参数变成map，map的key是规格组id，map的值是组下的所有参数
        Map<Long, List<SpecParam>> map = new HashMap<>();
        for (SpecParam specParam : specParams) {
            if (!map.containsKey(specParam.getGroupId())) {
                // 组id在map中不存在
                map.put(specParam.getGroupId(), new ArrayList<>());
            }
            map.get(specParam.getGroupId()).add(specParam);
        }

        // 填充param到group
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }
        return specGroups;
    }


    /*public String querySpecByCid(Long cid) {
        Specification spec = specMapper.selectByPrimaryKey(cid);
        if (spec == null) {
            // 没查到
            throw new FyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return spec.getSpecifications();
    }*/


}
