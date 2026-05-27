package com.pindou.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pindou.common.Result;
import com.pindou.entity.Cart;
import com.pindou.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartMapper cartMapper;

    @GetMapping("/list")
    public Result<?> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Cart> list = cartMapper.selectCartWithProduct(userId);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Cart cart, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cart.setUserId(userId);

        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("product_id", cart.getProductId());
        Cart existCart = cartMapper.selectOne(wrapper);

        if (existCart != null) {
            existCart.setQuantity(existCart.getQuantity() + cart.getQuantity());
            cartMapper.updateById(existCart);
        } else {
            cart.setChecked(1);
            cartMapper.insert(cart);
        }
        return Result.success("添加成功", null);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Cart cart, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Cart dbCart = cartMapper.selectById(cart.getId());
        if (dbCart == null || !dbCart.getUserId().equals(userId)) {
            return Result.error("购物车项不存在");
        }
        cartMapper.updateById(cart);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id).eq("user_id", userId);
        cartMapper.delete(wrapper);
        return Result.success();
    }

    @DeleteMapping("/clear")
    public Result<?> clear(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        cartMapper.delete(wrapper);
        return Result.success();
    }
}
