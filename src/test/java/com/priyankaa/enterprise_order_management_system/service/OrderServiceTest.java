package com.priyankaa.enterprise_order_management_system.service;

import com.priyankaa.enterprise_order_management_system.dto.OrderItemRequest;
import com.priyankaa.enterprise_order_management_system.dto.OrderRequest;
import com.priyankaa.enterprise_order_management_system.dto.OrderResponse;
import com.priyankaa.enterprise_order_management_system.entity.Order;
import com.priyankaa.enterprise_order_management_system.entity.Product;
import com.priyankaa.enterprise_order_management_system.entity.User;
import com.priyankaa.enterprise_order_management_system.enums.OrderStatus;
import com.priyankaa.enterprise_order_management_system.enums.Role;
import com.priyankaa.enterprise_order_management_system.exception.InsufficientStockException;
import com.priyankaa.enterprise_order_management_system.exception.ResourceNotFoundException;
import com.priyankaa.enterprise_order_management_system.repository.OrderRepository;
import com.priyankaa.enterprise_order_management_system.repository.ProductRepository;
import com.priyankaa.enterprise_order_management_system.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Priya");
        user.setEmail("priya@gmail.com");
        user.setRole(Role.CUSTOMER);

        product = new Product(
                "PROD-ELEC-001",
                "Enterprise Server Rack",
                "Enterprise server rack",
                new BigDecimal("1299.99"),
                15
        );
        product.setId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        orderRequest = new OrderRequest();
        orderRequest.setItems(List.of(itemRequest));
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {

        when(userRepository.findByEmail("priya@gmail.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(100L);
                    return order;
                });

        OrderResponse response =
                orderService.createOrder(orderRequest, "priya@gmail.com");

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(new BigDecimal("2599.98"), response.getTotalPrice());

        assertEquals(1, response.getItems().size());
        assertEquals(1L, response.getItems().get(0).getProductId());
        assertEquals(2, response.getItems().get(0).getQuantity());
        assertEquals(
                new BigDecimal("1299.99"),
                response.getItems().get(0).getPrice()
        );

        // 15 - 2 = 13
        assertEquals(13, product.getStockQuantity());

        verify(userRepository).findByEmail("priya@gmail.com");
        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldCalculateTotalForMultipleProducts() {

        Product secondProduct = new Product(
                "PROD-NET-002",
                "Enterprise Router",
                "Enterprise network router",
                new BigDecimal("500.00"),
                10
        );
        secondProduct.setId(2L);

        OrderItemRequest firstItem = new OrderItemRequest();
        firstItem.setProductId(1L);
        firstItem.setQuantity(2);

        OrderItemRequest secondItem = new OrderItemRequest();
        secondItem.setProductId(2L);
        secondItem.setQuantity(3);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(firstItem, secondItem));

        when(userRepository.findByEmail("priya@gmail.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(secondProduct));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(100L);
                    return order;
                });

        OrderResponse response =
                orderService.createOrder(request, "priya@gmail.com");

        // (1299.99 * 2) + (500 * 3)
        // 2599.98 + 1500 = 4099.98
        assertEquals(
                new BigDecimal("4099.98"),
                response.getTotalPrice()
        );

        assertEquals(13, product.getStockQuantity());
        assertEquals(7, secondProduct.getStockQuantity());
        assertEquals(2, response.getItems().size());

        verify(productRepository, times(2))
                .save(any(Product.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> orderService.createOrder(
                                orderRequest,
                                "unknown@gmail.com"
                        )
                );

        assertEquals(
                "User not found with email: unknown@gmail.com",
                exception.getMessage()
        );

        verify(productRepository, never())
                .findById(anyLong());

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductDoesNotExist() {

        when(userRepository.findByEmail("priya@gmail.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> orderService.createOrder(
                                orderRequest,
                                "priya@gmail.com"
                        )
                );

        assertEquals(
                "Product not found with ID: 1",
                exception.getMessage()
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenStockIsInsufficient() {

        product.setStockQuantity(1);

        when(userRepository.findByEmail("priya@gmail.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        InsufficientStockException exception =
                assertThrows(
                        InsufficientStockException.class,
                        () -> orderService.createOrder(
                                orderRequest,
                                "priya@gmail.com"
                        )
                );

        assertEquals(
                "Insufficient stock for product: Enterprise Server Rack",
                exception.getMessage()
        );

        // Stock should remain unchanged.
        assertEquals(1, product.getStockQuantity());

        verify(productRepository, never())
                .save(any(Product.class));

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void getOrderHistory_ShouldReturnOrdersForUser() {

        Order order = createSampleOrder();

        when(userRepository.findByEmail("priya@gmail.com"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByUserId(1L))
                .thenReturn(List.of(order));

        List<OrderResponse> responses =
                orderService.getOrderHistory("priya@gmail.com");

        assertNotNull(responses);
        assertEquals(1, responses.size());

        assertEquals(100L, responses.get(0).getId());
        assertEquals(
                OrderStatus.PENDING,
                responses.get(0).getStatus()
        );

        verify(orderRepository).findByUserId(1L);
    }

    @Test
    void updateOrderStatus_ShouldUpdateSuccessfully() {

        Order order = createSampleOrder();

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response =
                orderService.updateOrderStatus(
                        100L,
                        OrderStatus.CONFIRMED
                );

        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());

        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(orderCaptor.capture());

        assertEquals(
                OrderStatus.CONFIRMED,
                orderCaptor.getValue().getStatus()
        );
    }

    private Order createSampleOrder() {

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(new BigDecimal("2599.98"));

        com.priyankaa.enterprise_order_management_system.entity.OrderItem item =
                new com.priyankaa.enterprise_order_management_system.entity.OrderItem(
                        order,
                        product,
                        2,
                        new BigDecimal("1299.99")
                );

        order.setItems(List.of(item));

        return order;
    }
}