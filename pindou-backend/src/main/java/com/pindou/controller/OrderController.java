package com.pindou.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pindou.common.Result;
import com.pindou.entity.*;
import com.pindou.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @PostMapping("/create")
    public Result<?> create(@RequestBody Order order, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setStatus(0);

        List<Cart> cartList = cartMapper.selectCartWithProduct(userId);
        List<Cart> checkedItems = cartList.stream().filter(c -> c.getChecked() == 1).toList();

        if (checkedItems.isEmpty()) {
            return Result.error("请选择要结算的商品");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Cart cart : checkedItems) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStock() < cart.getQuantity()) {
                return Result.error("商品「" + cart.getProductName() + "」库存不足");
            }
            total = total.add(cart.getProductPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }

        order.setTotalAmount(total);
        orderMapper.insert(order);

        for (Cart cart : checkedItems) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(cart.getProductId());
            item.setProductName(cart.getProductName());
            item.setProductImage(cart.getProductImage());
            item.setPrice(cart.getProductPrice());
            item.setQuantity(cart.getQuantity());
            orderItemMapper.insert(item);

            Product product = productMapper.selectById(cart.getProductId());
            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales(product.getSales() + cart.getQuantity());
            productMapper.updateById(product);
        }

        QueryWrapper<Cart> delWrapper = new QueryWrapper<>();
        delWrapper.eq("user_id", userId).eq("checked", 1);
        cartMapper.delete(delWrapper);

        return Result.success("下单成功", order);
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) Integer status,
                          HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Order> p = new Page<>(page, size);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        IPage<Order> result = orderMapper.selectPage(p, wrapper);

        for (Order o : result.getRecords()) {
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_id", o.getId());
            o.setOrderItems(orderItemMapper.selectList(itemWrapper));
        }
        return Result.success(result);
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在");
        }
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", id);
        order.setOrderItems(orderItemMapper.selectList(wrapper));
        return Result.success(order);
    }

    @PutMapping("/pay/{id}")
    public Result<?> pay(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("订单状态异常");
        }
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success("支付成功", null);
    }

    @PutMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("只能取消待付款订单");
        }
        order.setStatus(4);
        orderMapper.updateById(order);

        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", id);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            product.setStock(product.getStock() + item.getQuantity());
            product.setSales(product.getSales() - item.getQuantity());
            productMapper.updateById(product);
        }
        return Result.success("取消成功", null);
    }

    @PutMapping("/confirm/{id}")
    public Result<?> confirm(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在");
        }
        order.setStatus(3);
        orderMapper.updateById(order);
        return Result.success("确认收货成功", null);
    }

    @GetMapping("/admin/list")
    public Result<?> adminList(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) Integer status,
                               @RequestParam(required = false) String orderNo) {
        Page<Order> p = new Page<>(page, size);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like("order_no", orderNo);
        }
        wrapper.orderByDesc("create_time");
        IPage<Order> result = orderMapper.selectPage(p, wrapper);
        for (Order o : result.getRecords()) {
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_id", o.getId());
            o.setOrderItems(orderItemMapper.selectList(itemWrapper));
        }
        return Result.success(result);
    }

    @PutMapping("/admin/ship/{id}")
    public Result<?> ship(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        order.setStatus(2);
        orderMapper.updateById(order);
        return Result.success("发货成功", null);
    }

    @GetMapping("/admin/stats")
    public Result<?> stats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalOrders", orderMapper.selectCount(null));
        QueryWrapper<Order> paidWrapper = new QueryWrapper<>();
        paidWrapper.ne("status", 0).ne("status", 4);
        stats.put("paidOrders", orderMapper.selectCount(paidWrapper));
        return Result.success(stats);
    }

    private String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
    }
}
