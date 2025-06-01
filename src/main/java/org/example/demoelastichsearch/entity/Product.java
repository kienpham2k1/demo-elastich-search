package org.example.demoelastichsearch.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(indexName = "product")
public class Product {
    @Id
    private String id;
    private String name;
    private double price;
}
