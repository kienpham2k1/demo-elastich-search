package org.example.demoelastichsearch.loader;

import org.example.demoelastichsearch.entity.Product;
import org.example.demoelastichsearch.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) {
        // Xoá tất cả nếu cần (cho mục đích test)
        productRepository.deleteAll();

        // Tạo dữ liệu mẫu
        List<Product> products = List.of(
                new Product(UUID.randomUUID().toString(), "iPhone 15", 999.0f),
                new Product(UUID.randomUUID().toString(), "Samsung Galaxy S23", 899.0f),
                new Product(UUID.randomUUID().toString(), "Xiaomi 13", 499.0f)
        );

        // Lưu vào Elasticsearch
        productRepository.saveAll(products);

        System.out.println("Elasticsearch: Đã khởi tạo dữ liệu mẫu!");
    }
}
