package com.hotpot.dto;

import com.hotpot.entity.OrderItem;
import com.hotpot.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrderDetailVO {
    private Orders order;
    private List<OrderItem> items;

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
