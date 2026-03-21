package com.hotpot.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotpot.common.Result;
import com.hotpot.dto.LoginDTO;
import com.hotpot.dto.OrderDetailVO;
import com.hotpot.entity.*;
import com.hotpot.mapper.*;
import com.hotpot.service.OrderService;
import com.hotpot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;
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

    @Value("${hotpot.upload.path}")
    private String uploadPath;

    // ===== 登录 =====
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO dto) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, dto.getUsername()));
        if (admin == null || !BCrypt.checkpw(dto.getPassword(), admin.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(admin.getId(), "admin");
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("name", admin.getName());
        return Result.success(data);
    }

    // ===== 分类管理 =====
    @GetMapping("/category/list")
    public Result<List<Category>> categoryList() {
        List<Category> list = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        return Result.success(list);
    }

    @PostMapping("/category/save")
    public Result<?> categorySave(@RequestBody Category category) {
        if (category.getId() == null) {
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateById(category);
        }
        return Result.success();
    }

    @DeleteMapping("/category/delete/{id}")
    public Result<?> categoryDelete(@PathVariable Long id) {
        Long count = dishMapper.selectCount(
                new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, id));
        if (count > 0) {
            return Result.error("该分类下还有菜品，无法删除");
        }
        categoryMapper.deleteById(id);
        return Result.success();
    }

    // ===== 菜品管理 =====
    @GetMapping("/dish/list")
    public Result<?> dishList(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "20") int size,
                              @RequestParam(required = false) Long categoryId) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        wrapper.orderByAsc(Dish::getSortOrder);
        Page<Dish> result = dishMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    @PostMapping("/dish/save")
    public Result<?> dishSave(@RequestBody Dish dish) {
        if (dish.getId() == null) {
            dishMapper.insert(dish);
        } else {
            dishMapper.updateById(dish);
        }
        return Result.success();
    }

    @DeleteMapping("/dish/delete/{id}")
    public Result<?> dishDelete(@PathVariable Long id) {
        dishMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/dish/status")
    public Result<?> dishStatus(@RequestParam Long id, @RequestParam Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.updateById(dish);
        return Result.success();
    }

    // ===== 订单管理 =====
    @GetMapping("/order/list")
    public Result<?> orderList(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "20") int size,
                               @RequestParam(required = false) Integer status) {
        return Result.success(orderService.listAll(status, page, size));
    }

    @GetMapping("/order/detail/{id}")
    public Result<OrderDetailVO> orderDetail(@PathVariable Long id) {
        return Result.success(orderService.getDetail(id));
    }

    @PostMapping("/order/status")
    public Result<?> orderStatus(@RequestParam Long orderId, @RequestParam Integer status) {
        orderService.updateStatus(orderId, status);
        return Result.success();
    }

    // ===== 评价管理 =====
    @GetMapping("/review/list")
    public Result<?> reviewList(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "20") int size) {
        Page<Review> result = reviewMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Review>().orderByDesc(Review::getCreateTime));
        return Result.success(result);
    }

    // ===== 文件上传 =====
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file.transferTo(new File(dir, fileName));
        return Result.success("/uploads/" + fileName);
    }
}
