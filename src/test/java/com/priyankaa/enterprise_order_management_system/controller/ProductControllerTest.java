package com.priyankaa.enterprise_order_management_system.controller;

import com.priyankaa.enterprise_order_management_system.dto.ProductRequest;
import com.priyankaa.enterprise_order_management_system.dto.ProductResponse;
import com.priyankaa.enterprise_order_management_system.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



class ProductControllerTest {

    private MockMvc mockMvc;

    private ProductService productService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        productService = mock(ProductService.class);

        ProductController productController =
                new ProductController(productService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {

        ProductRequest request = new ProductRequest();
        request.setSku("PROD-ELEC-001");
        request.setName("Enterprise Server Rack");
        request.setDescription("High-performance enterprise server rack");
        request.setPrice(new BigDecimal("1299.99"));
        request.setStockQuantity(15);

        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setSku("PROD-ELEC-001");
        response.setName("Enterprise Server Rack");
        response.setDescription("High-performance enterprise server rack");
        response.setPrice(new BigDecimal("1299.99"));
        response.setStockQuantity(15);

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sku").value("PROD-ELEC-001"))
                .andExpect(jsonPath("$.name").value("Enterprise Server Rack"))
                .andExpect(jsonPath("$.price").value(1299.99))
                .andExpect(jsonPath("$.stockQuantity").value(15));

        verify(productService)
                .createProduct(any(ProductRequest.class));
    }

    @Test
    void getAllProducts_ShouldReturnProductList() throws Exception {

        ProductResponse firstProduct = new ProductResponse();
        firstProduct.setId(1L);
        firstProduct.setSku("PROD-ELEC-001");
        firstProduct.setName("Enterprise Server Rack");
        firstProduct.setDescription("Enterprise server rack");
        firstProduct.setPrice(new BigDecimal("1299.99"));
        firstProduct.setStockQuantity(15);

        ProductResponse secondProduct = new ProductResponse();
        secondProduct.setId(2L);
        secondProduct.setSku("PROD-NET-002");
        secondProduct.setName("Enterprise Router");
        secondProduct.setDescription("Enterprise network router");
        secondProduct.setPrice(new BigDecimal("899.99"));
        secondProduct.setStockQuantity(20);

        when(productService.getAllProducts())
                .thenReturn(List.of(firstProduct, secondProduct));

        mockMvc.perform(
                        get("/api/products")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].sku").value("PROD-ELEC-001"))
                .andExpect(jsonPath("$[0].name").value("Enterprise Server Rack"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].sku").value("PROD-NET-002"))
                .andExpect(jsonPath("$[1].name").value("Enterprise Router"));

        verify(productService).getAllProducts();
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList() throws Exception {

        when(productService.getAllProducts())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/products")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService).getAllProducts();
    }

    @Test
    void createProduct_ShouldReturnBadRequest_WhenRequestIsInvalid()
            throws Exception {

        ProductRequest request = new ProductRequest();

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(productService, never())
                .createProduct(any(ProductRequest.class));
    }
}