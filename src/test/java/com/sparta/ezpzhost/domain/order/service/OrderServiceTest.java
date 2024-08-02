package com.sparta.ezpzhost.domain.order.service;

import com.sparta.ezpzhost.common.entity.Timestamped;
import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.cart.entity.Cart;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.item.enums.ItemStatus;
import com.sparta.ezpzhost.domain.item.repository.ItemRepository;
import com.sparta.ezpzhost.domain.order.dto.OrderCondition;
import com.sparta.ezpzhost.domain.order.dto.OrderFindAllResponseDto;
import com.sparta.ezpzhost.domain.order.dto.OrderRequestDto;
import com.sparta.ezpzhost.domain.order.dto.OrderResponseDto;
import com.sparta.ezpzhost.domain.order.entity.Order;
import com.sparta.ezpzhost.domain.order.enums.OrderStatus;
import com.sparta.ezpzhost.domain.order.repository.OrderRepository;
import com.sparta.ezpzhost.domain.orderline.entity.Orderline;
import com.sparta.ezpzhost.domain.orderline.repository.OrderlineRepository;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzhost.domain.popup.enums.PopupStatus;
import com.sparta.ezpzhost.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderlineRepository orderlineRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Order order;
    private Cart cart;
    private Host host;
    private Popup popup;
    private List<Cart> cartList;
    private Item item;
    private Orderline orderline;
    private OrderFindAllResponseDto orderFindAllResponseDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(
                1L,
                "testuser",
                "encodedPassword",
                "Test Name",
                "testuser@example.com",
                "01012345678"
        );
        host = new Host(
                1L,
                "hostUsername",
                "hostPassword",
                "host@example.com",
                "Host Company",
                "123-45-67890"
        );
        popup = new Popup(
                1L,                    // id
                "Popup Name",          // name
                "Popup Description",   // description
                "http://example.com/thumbnail.jpg", // thumbnailUrl
                "thumbnail.jpg",       // thumbnailName
                "123 Popup St.",       // address
                "Manager Name",        // managerName
                "010-1234-5678",       // phoneNumber
                0,                     // likeCount
                10,
                4.5,
                LocalDateTime.now(),   // startDate
                LocalDateTime.now().plusDays(7), // endDate
                PopupStatus.IN_PROGRESS,    // popupStatus
                ApprovalStatus.APPROVED, // approvalStatus
                host,                  // host
                new ArrayList<>()
        );
        item = new Item(
                1L,               // id
                popup,            // popup
                "Item Name",      // name
                "Item Description", // description
                1000,             // price
                2,               // stock
                "http://example.com/image.jpg", // imageUrl
                "image.jpg",      // imageName
                0,                // likeCount
                ItemStatus.SALE   // itemStatus
        );

        cart = Cart.of(2, user, item);
        cartList = new ArrayList<>();
        cartList.add(cart);
        order = new Order(1L, 2000, OrderStatus.ORDER_COMPLETED, user, new ArrayList<>());
        orderline = Orderline.of(2, order, item);
        orderFindAllResponseDto = new OrderFindAllResponseDto(
                1L,                    // orderId
                14000,                 // totalPrice
                OrderStatus.ORDER_COMPLETED.toString(), // orderStatus
                "2024-07-24"           // orderDate
        );
        // Set createdAt using reflection
        setCreatedAt(order, LocalDateTime.now());
    }

    private void setCreatedAt(Order order, LocalDateTime createdAt) {
        try {
            java.lang.reflect.Field field = Timestamped.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(order, createdAt);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test 조건별 주문 목록 조회 - ALL")
    public void findAllOrders_All() {
        OrderRequestDto orderRequestDto = new OrderRequestDto("all",
                1L,
                "ORDER_COMPLETED");
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = List.of(order);
        Page<Order> orderPages = new PageImpl<>(orderList, pageable, orderList.size());

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(orderRepository.findOrdersAllByStatus(any(OrderCondition.class), any(Pageable.class),
                any(Host.class))).thenReturn(orderPages);

        Page<OrderFindAllResponseDto> responsePage = orderService.findAllOrders(orderRequestDto,
                pageable, host);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
    }

    @Test
    @DisplayName("Test 조건별 주문 목록 조회 - BY_ITEM")
    public void findAllOrders_By_Item() {
        OrderRequestDto orderRequestDto = new OrderRequestDto("by_item",
                1L,
                "all");
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = List.of(order);
        Page<Order> orderPages = new PageImpl<>(orderList, pageable, orderList.size());

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(orderRepository.findOrdersAllByStatus(any(OrderCondition.class), any(Pageable.class),
                any(Host.class))).thenReturn(orderPages);

        Page<OrderFindAllResponseDto> responsePage = orderService.findAllOrders(orderRequestDto,
                pageable, host);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
    }

    @Test
    @DisplayName("Test 조건별 주문 목록 조회 - BY_ITEM - 조회할 수 없는 id일 때")
    public void findAllOrders_By_Item_Has_Not_Answer() {
        OrderRequestDto orderRequestDto = new OrderRequestDto("by_item",
                2L,
                "all");
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = List.of(order);
        Page<Order> orderPages = new PageImpl<>(orderList, pageable, orderList.size());

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            orderService.findAllOrders(orderRequestDto, pageable, host);
        });

        assertEquals(ErrorType.ITEM_ACCESS_FORBIDDEN, exception.getErrorType());
    }

    @Test
    @DisplayName("Test 조건별 주문 목록 조회 - BY_STATUS - 결과 있을 때")
    public void findAllOrders_By_Status() {
        OrderRequestDto orderRequestDto = new OrderRequestDto("by_status",
                -1L,
                "order_completed");
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = List.of(order);
        Page<Order> orderPages = new PageImpl<>(orderList, pageable, orderList.size());

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(orderRepository.findOrdersAllByStatus(any(OrderCondition.class), any(Pageable.class),
                any(Host.class))).thenReturn(orderPages);

        Page<OrderFindAllResponseDto> responsePage = orderService.findAllOrders(orderRequestDto,
                pageable, host);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
    }

    @Test
    @DisplayName("Test 조건별 주문 목록 조회 - BY_STATUS - 결과 없을 때")
    public void findAllOrders_By_Status_Has_Not_Answer() {
        OrderRequestDto orderRequestDto = new OrderRequestDto("by_status",
                -1L,
                "delivered");
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = List.of(order);

        when(orderRepository.findOrdersAllByStatus(any(OrderCondition.class), any(Pageable.class),
                any(Host.class))).thenReturn(new PageImpl<>(new ArrayList<>(), pageable, 0));

        CustomException exception = assertThrows(CustomException.class, () -> {
            orderService.findAllOrders(orderRequestDto, pageable, host);
        });

        assertEquals(ErrorType.EMPTY_PAGE_ELEMENTS, exception.getErrorType());
    }

    @Test
    @DisplayName("Test 주문 상세 조회")
    public void findOrder() {
        when(orderRepository.findOrderWithDetails(anyLong(), anyLong())).thenReturn(order);
        when(itemRepository.isItemSoldByHost(anyLong(), anyLong())).thenReturn(true);

        OrderResponseDto responseDto = orderService.findOrder(1L, host);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getOrderId());
        assertEquals(2000, responseDto.getTotalPrice());
    }

    @Test
    @DisplayName("Test 주문 상세 조회 - 주문을 찾을 수 없거나 접근할 수 없는 경우")
    public void findOrder_Not_Found_Or_Access_Denied() {
        when(orderRepository.findOrderWithDetails(anyLong(), anyLong())).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> {
            orderService.findOrder(1L, host);
        });

        assertEquals(ErrorType.ORDER_NOT_FOUND_OR_ACCESS_DENIED, exception.getErrorType());
    }
}
