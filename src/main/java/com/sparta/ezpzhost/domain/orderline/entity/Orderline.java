package com.sparta.ezpzhost.domain.orderline.entity;

import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.order.entity.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orderline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderline_id")
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int orderPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * Orderline 생성자
     *
     * @param quantity 주문할 상품의 수량
     * @param order    주문
     * @param item     주문할 상품
     */
    private Orderline(int quantity, Order order, Item item) {
        this.quantity = quantity;
        this.orderPrice = quantity * item.getPrice();
        this.order = order;
        this.item = item;
    }

    /**
     * Orderline 정적 팩토리 메서드
     *
     * @param quantity 주문할 상품의 수량
     * @param order    주문
     * @param item     주문할 상품
     * @return Orderline
     */
    public static Orderline of(int quantity, Order order, Item item) {
        return new Orderline(quantity, order, item);
    }


}
