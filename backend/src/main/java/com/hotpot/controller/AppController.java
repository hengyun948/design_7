package com.hotpot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotpot.common.Result;
import com.hotpot.dto.OrderDetailVO;
import com.hotpot.dto.OrderSubmitDTO;
import com.hotpot.entity.*;
import com.hotpot.mapper.CategoryMapper;
import com.hotpot.mapper.DishMapper;
import com.hotpot.mapper.ReviewMapper;
import com.hotpot.mapper.UserMapper;
import com.hotpot.service.OrderService;
import com.hotpot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app")
public class AppController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private JwtUtil jwtUtil;

    // ===== 登录(模拟) =====
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        // 实际应调用微信API用code换openid，此处模拟
        String openid = "mock_openid_" + code;

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname("微信用户");
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);
        }

        String token = jwtUtil.generateToken(user.getId(), "app");
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("nickname", user.getNickname());
        return Result.success(data);
    }

    // ===== 分类列表 =====
    @GetMapping("/category/list")
    public Result<List<Category>> categoryList() {
        List<Category> list = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        return Result.success(list);
    }

    // ===== 菜品列表(仅上架) =====
    @GetMapping("/dish/list")
    public Result<List<Dish>> dishList(@RequestParam(required = false) Long categoryId) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getStatus, 1);
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        wrapper.orderByAsc(Dish::getSortOrder);
        return Result.success(dishMapper.selectList(wrapper));
    }

    // ===== 提交订单 =====
    @PostMapping("/order/submit")
    public Result<?> orderSubmit(@RequestBody OrderSubmitDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentId");
        Orders order = orderService.submitOrder(userId, dto);
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", order.getId());
        data.put("orderNo", order.getOrderNo());
        return Result.success(data);
    }

    // ===== 模拟支付 =====
    @PostMapping("/order/pay")
    public Result<?> orderPay(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentId");
        Long orderId = params.get("orderId");
        orderService.payOrder(orderId, userId);
        return Result.success();
    }

    // ===== 我的订单列表 =====
    @GetMapping("/order/list")
    public Result<?> orderList(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "20") int size,
                               @RequestParam(required = false) Integer status,
                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentId");
        return Result.success(orderService.listByUser(userId, status, page, size));
    }

    // ===== 订单详情 =====
    @GetMapping("/order/detail/{id}")
    public Result<OrderDetailVO> orderDetail(@PathVariable Long id) {
        return Result.success(orderService.getDetail(id));
    }

    // ===== 提交评价 =====
    @PostMapping("/review/submit")
    public Result<?> reviewSubmit(@RequestBody Review review, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentId");
        review.setUserId(userId);
        review.setCreateTime(LocalDateTime.now());
        reviewMapper.insert(review);
        return Result.success();
    }
}
