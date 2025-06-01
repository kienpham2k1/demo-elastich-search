package org.example.demoelastichsearch.repository;

import org.example.demoelastichsearch.entity.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository  extends ElasticsearchRepository<Product, String> {
}
