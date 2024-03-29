package com.fengyang.item.web;

import com.fengyang.item.pojo.SpecGroup;
import com.fengyang.item.pojo.SpecParam;
import com.fengyang.item.pojo.Specification;
import com.fengyang.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specService;

    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specService.queryGroupByCid(cid));
    }

    /**
     * 查询参数集合
     * @param gid 组id
     * @param cid 分类id
     * @param searching 是否搜索
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamList(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching
        ) {
        return ResponseEntity.ok(specService.queryParamList(gid, cid, searching));
    }

    /**
     * 根据分类id查询规格参数
     * @param cid
     * @return
     */
    /*@GetMapping("groups/{cid}")
    public ResponseEntity<String> querySpecByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specService.querySpecByCid(cid));
    }*/

    /**
     * 根据分类查询规格组及组内参数
     * @param cid
     * @return
     */
    @GetMapping("group")
    public ResponseEntity<List<SpecGroup>> queryListByCid(@RequestParam("cid") Long cid) {
        return ResponseEntity.ok(specService.queryListByCid(cid));
    }
}
