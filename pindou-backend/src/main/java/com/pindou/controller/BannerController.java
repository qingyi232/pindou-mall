package com.pindou.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pindou.common.Result;
import com.pindou.entity.Banner;
import com.pindou.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banner")
public class BannerController {

    @Autowired
    private BannerMapper bannerMapper;

    @GetMapping("/list")
    public Result<?> list() {
        QueryWrapper<Banner> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByAsc("sort_order");
        List<Banner> list = bannerMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/all")
    public Result<?> all() {
        QueryWrapper<Banner> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort_order");
        List<Banner> list = bannerMapper.selectList(wrapper);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Banner banner) {
        banner.setStatus(1);
        bannerMapper.insert(banner);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Banner banner) {
        bannerMapper.updateById(banner);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        bannerMapper.deleteById(id);
        return Result.success();
    }
}
