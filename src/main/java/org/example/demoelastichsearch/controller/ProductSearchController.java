package org.example.demoelastichsearch.controller;

import org.example.demoelastichsearch.entity.Product;
import org.example.demoelastichsearch.service.ProductQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductSearchController {
    @Autowired
    private ProductQueryService productQueryService;

    @GetMapping("/search")
    public List<Product> search(@RequestParam String keyword,
                                @RequestParam Double minPrice,
                                @RequestParam Double maxPrice ) {
        return productQueryService.search(keyword, minPrice, maxPrice);
    }
}
