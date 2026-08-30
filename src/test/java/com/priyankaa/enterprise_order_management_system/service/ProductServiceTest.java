package com.priyankaa.enterprise_order_management_system.service;

import com.priyankaa.enterprise_order_management_system.dto.ProductRequest;
import com.priyankaa.enterprise_order_management_system.dto.ProductResponse;
import com.priyankaa.enterprise_order_management_system.entity.Product;
import com.priyankaa.enterprise_order_management_system.repository.ProductRepository;
import com.priyankaa.enterprise_order_management_system.exception.DuplicateResourceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest productRequest;
    private Product product;

    @BeforeEach
    void setUp() {

        productRequest = new ProductRequest();
        productRequest.setSku("PROD-ELEC-001");
        productRequest.setName("Enterprise Server Rack");
        productRequest.setDescription(
                "High-performance 42U network switch server rack."
        );
        productRequest.setPrice(new BigDecimal("1299.99"));
        productRequest.setStockQuantity(15);

        product = new Product(
                "PROD-ELEC-001",
                "Enterprise Server Rack",
                "High-performance 42U network switch server rack.",
                new BigDecimal("1299.99"),
                15
        );

        product.setId(1L);
    }

    @Test
    void createProduct_ShouldCreateSuccessfully() {

        when(productRepository.existsBySku(productRequest.getSku()))
                .thenReturn(false);

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponse response =
                productService.createProduct(productRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("PROD-ELEC-001", response.getSku());
        assertEquals("Enterprise Server Rack", response.getName());
        assertEquals(
                "High-performance 42U network switch server rack.",
                response.getDescription()
        );
        assertEquals(new BigDecimal("1299.99"), response.getPrice());
        assertEquals(15, response.getStockQuantity());

        verify(productRepository)
                .existsBySku("PROD-ELEC-001");

        verify(productRepository)
                .save(any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowException_WhenSkuAlreadyExists() {

        when(productRepository.existsBySku(productRequest.getSku()))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> productService.createProduct(productRequest)
                );

        assertEquals(
                "Product already exists with SKU: PROD-ELEC-001",
                exception.getMessage()
        );

        verify(productRepository)
                .existsBySku("PROD-ELEC-001");

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void getAllProducts_ShouldReturnProductList() {

        Product secondProduct = new Product(
                "PROD-NET-002",
                "Enterprise Router",
                "High-speed enterprise router",
                new BigDecimal("899.99"),
                20
        );

        secondProduct.setId(2L);

        when(productRepository.findAll())
                .thenReturn(List.of(product, secondProduct));

        List<ProductResponse> responses =
                productService.getAllProducts();

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(
                "PROD-ELEC-001",
                responses.get(0).getSku()
        );

        assertEquals(
                "PROD-NET-002",
                responses.get(1).getSku()
        );

        verify(productRepository)
                .findAll();
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProductsExist() {

        when(productRepository.findAll())
                .thenReturn(List.of());

        List<ProductResponse> responses =
                productService.getAllProducts();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(productRepository)
                .findAll();
    }
}