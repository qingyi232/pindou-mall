package com.pindou.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pindou.common.Result;
import com.pindou.entity.Product;
import com.pindou.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "12") int size,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) String keyword) {
        Page<Product> p = new Page<>(page, size);
        IPage<Product> result = productMapper.selectProductPage(p, categoryId, keyword, 1);
        return Result.success(result);
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        product.setViewCount(product.getViewCount() + 1);
        productMapper.updateById(product);
        return Result.success(product);
    }

    @GetMapping("/hot")
    public Result<?> hot() {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).eq("is_hot", 1).orderByDesc("sales").last("LIMIT 8");
        List<Product> list = productMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/new")
    public Result<?> newProducts() {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).eq("is_new", 1).orderByDesc("create_time").last("LIMIT 8");
        List<Product> list = productMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/search")
    public Result<?> search(@RequestParam String keyword,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "12") int size) {
        Page<Product> p = new Page<>(page, size);
        IPage<Product> result = productMapper.selectProductPage(p, null, keyword, 1);
        return Result.success(result);
    }

    @GetMapping("/admin/list")
    public Result<?> adminList(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Integer status) {
        Page<Product> p = new Page<>(page, size);
        IPage<Product> result = productMapper.selectProductPage(p, categoryId, keyword, status);
        return Result.success(result);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Product product) {
        product.setStatus(1);
        product.setSales(0);
        product.setViewCount(0);
        productMapper.insert(product);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Product product) {
        productMapper.updateById(product);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        productMapper.deleteById(id);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Product product) {
        Product p = new Product();
        p.setId(id);
        p.setStatus(product.getStatus());
        productMapper.updateById(p);
        return Result.success();
    }
}
