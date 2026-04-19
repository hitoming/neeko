package com.neeko.map.controller;

import com.neeko.map.common.Result;
import com.neeko.map.entity.SysTag;
import com.neeko.map.mapper.SysTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private SysTagMapper tagMapper;

    /**
     * 获取所有标签
     */
    @GetMapping("/list")
    public Result<List<SysTag>> getAllTags() {
        List<SysTag> tags = tagMapper.selectList(null);
        return Result.success(tags);
    }
}
