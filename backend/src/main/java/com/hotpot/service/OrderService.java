package com.hotpot.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotpot.dto.OrderDetailVO;
import com.hotpot.dto.OrderSubmitDTO;
import com.hotpot.entity.OrderItem;
import com.hotpot.entity.Orders;
import com.hotpot.mapper.OrderItemMapper;
import com.hotpot.mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Transactional
    public Orders submitOrder(Long userId, OrderSubmitDTO dto) {
        int totalAmount = 0;
        for (OrderSubmitDTO.OrderItemDTO item : dto.getItems()) {
            totalAmount += item.getDishPrice() * item.getQuantity();
        }

        Orders order = new Orders();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setTableNo(dto.getTableNo());
        order.setRemark(dto.getRemark());
        order.setCreateTime(LocalDateTime.now());
        ordersMapper.insert(order);

        for (OrderSubmitDTO.OrderItemDTO item : dto.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setDishId(item.getDishId());
            orderItem.setDishName(item.getDishName());
            orderItem.setDishPrice(item.getDishPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItemMapper.insert(orderItem);
        }

        return order;
    }

    public void payOrder(Long orderId, Long userId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态异常");
        }
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        ordersMapper.updateById(order);
    }

    public Page<Orders> listByUser(Long userId, Integer status, int page, int size) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        return ordersMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Page<Orders> listAll(Integer status, int page, int size) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        return ordersMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public OrderDetailVO getDetail(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        OrderDetailVO vo = new OrderDetailVO();
        vo.setOrder(order);
        vo.setItems(items);
        return vo;
    }

    public void updateStatus(Long orderId, Integer status) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setStatus(status);
        ordersMapper.updateById(order);
    }
}
