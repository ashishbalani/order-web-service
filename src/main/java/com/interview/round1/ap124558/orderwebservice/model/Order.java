package com.interview.round1.ap124558.orderwebservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {
    private Long orderId;
    private String price;
    private Long parentId;
    private Integer executedQuantity;
    private Integer quantity;
    private String security;

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", price='" + price + '\'' +
                ", parentId=" + parentId +
                ", executedQuantity=" + executedQuantity +
                ", quantity=" + quantity +
                ", security='" + security + '\'' +
                '}';
    }
}