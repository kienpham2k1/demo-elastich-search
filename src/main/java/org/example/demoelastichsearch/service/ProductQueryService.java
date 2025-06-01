package org.example.demoelastichsearch.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import org.example.demoelastichsearch.entity.Product;
import org.example.demoelastichsearch.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductQueryService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public List<Product> search(String keyword, Double minPrice, Double maxPrice) {
        NativeQueryBuilder queryBuilder = NativeQuery.builder();
        queryBuilder.withAggregation("name", Aggregation.of(a -> a
                        .terms(ta -> ta
                                .field("name")
                        )
                )
        );
        // Query theo keyword
        queryBuilder.withQuery(q -> q
                .bool(b -> b
                        .must(m -> m
                                .multiMatch(mm -> mm
                                        .query(keyword)
                                        .fields("name", "brand", "categories")
                                        .fuzziness("AUTO")
                                )
                        )
                )
        );
        queryBuilder.withFilter(q -> q
                .bool(b -> b.should(s -> s
                                .term(t -> t
                                        .field("name")
                                        .value(keyword)
                                        .caseInsensitive(true)
                                )
                        )
                )
        );
        queryBuilder.withFilter(q -> q
                .bool(b -> b
                        .should(m -> m
                                .range(r -> r
                                        .number(n -> n
                                                .field("price")
                                                .gte(minPrice)
                                                .lte(maxPrice)
                                        )
                                )
                        )
                )
        );
        // Phân trang (ví dụ page 0, size 10)
        queryBuilder.withPageable(PageRequest.of(0, 10));
        queryBuilder.withSort(Sort.by(Sort.Direction.DESC, "id"));

        // Thực hiện search
        NativeQuery query = queryBuilder.build();
        SearchHits<Product> hits = elasticsearchOperations.search(query, Product.class);

        return hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();
    }

}
