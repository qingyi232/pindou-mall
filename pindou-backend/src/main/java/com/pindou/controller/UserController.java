package com.pindou.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pindou.common.Constants;
import com.pindou.common.Result;
import com.pindou.entity.User;
import com.pindou.mapper.UserMapper;
import com.pindou.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        if (userMapper.selectOne(wrapper) != null) {
            return Result.error("用户名已存在");
        }
        user.setPassword(DigestUtil.md5Hex(user.getPassword()));
        user.setRole(Constants.ROLE_USER);
        user.setStatus(1);
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(user.getUsername());
        }
        userMapper.insert(user);
        return Result.success("注册成功", null);
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        User dbUser = userMapper.selectOne(wrapper);
        if (dbUser == null) {
            return Result.error("用户不存在");
        }
        if (!DigestUtil.md5Hex(user.getPassword()).equals(dbUser.getPassword())) {
            return Result.error("密码错误");
        }
        if (dbUser.getStatus() != 1) {
            return Result.error("账号已被禁用");
        }
        String token = jwtUtils.generateToken(dbUser.getId(), dbUser.getUsername(), dbUser.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", dbUser);
        return Result.success("登录成功", result);
    }

    @GetMapping("/info")
    public Result<?> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        user.setPassword(null);
        user.setRole(null);
        userMapper.updateById(user);
        return Result.success("更新成功", null);
    }

    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (!DigestUtil.md5Hex(oldPassword).equals(user.getPassword())) {
            return Result.error("旧密码错误");
        }
        user.setPassword(DigestUtil.md5Hex(newPassword));
        userMapper.updateById(user);
        return Result.success("密码修改成功", null);
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword) {
        Page<User> p = new Page<>(page, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("username", keyword).or().like("nickname", keyword);
        }
        wrapper.orderByDesc("create_time");
        IPage<User> result = userMapper.selectPage(p, wrapper);
        return Result.success(result);
    }

    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        User user = new User();
        user.setId(id);
        user.setStatus(params.get("status"));
        userMapper.updateById(user);
        return Result.success();
    }
}
