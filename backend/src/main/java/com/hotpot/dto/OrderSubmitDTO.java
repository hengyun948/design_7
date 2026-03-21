package com.hotpot.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderSubmitDTO {
    private String tableNo;
    private String remark;
    private List<OrderItemDTO> items;

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    @Data
    public static class OrderItemDTO {
        private Long dishId;
        private String dishName;
        private Integer dishPrice;
        private Integer quantity;

        public Long getDishId() {
            return dishId;
        }

        public void setDishId(Long dishId) {
            this.dishId = dishId;
        }

        public String getDishName() {
            return dishName;
        }

        public void setDishName(String dishName) {
            this.dishName = dishName;
        }

        public Integer getDishPrice() {
            return dishPrice;
        }

        public void setDishPrice(Integer dishPrice) {
            this.dishPrice = dishPrice;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
