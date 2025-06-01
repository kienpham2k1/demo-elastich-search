package org.example.demoelastichsearch.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
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
import org.springframework.data.elasticsearch.core.*;
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
        queryBuilder.withAggregation("price", Aggregation.of(a -> a
                        .terms(t -> t
                                .field("price")
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
//        // Phân trang (ví dụ page 0, size 10)
        queryBuilder.withPageable(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "price")));
//        queryBuilder.withSort();

        // Thực hiện search
        NativeQuery query = queryBuilder.build();
        SearchHits<Product> hits = elasticsearchOperations.search(query, Product.class);
        SearchPage<Product> productPage = SearchHitSupport.searchPageFor(hits, queryBuilder.getPageable());
        return hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();
    }

}
