package com.pindou.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pindou.common.Result;
import com.pindou.entity.Review;
import com.pindou.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewMapper reviewMapper;

    @GetMapping("/list/{productId}")
    public Result<?> list(@PathVariable Long productId,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {
        Page<Review> p = new Page<>(page, size);
        IPage<Review> result = reviewMapper.selectReviewPage(p, productId);
        return Result.success(result);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Review review, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        review.setUserId(userId);
        reviewMapper.insert(review);
        return Result.success("评价成功", null);
    }
}
