package com.pindou.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pindou.common.Result;
import com.pindou.entity.Category;
import com.pindou.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<?> list() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByAsc("sort_order");
        List<Category> list = categoryMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/all")
    public Result<?> all() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort_order");
        List<Category> list = categoryMapper.selectList(wrapper);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Category category) {
        category.setStatus(1);
        categoryMapper.insert(category);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Category category) {
        categoryMapper.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return Result.success();
    }
}
