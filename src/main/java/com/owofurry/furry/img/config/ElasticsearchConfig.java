package com.owofurry.furry.img.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;


//@SpringBootConfiguration
public class ElasticsearchConfig {
    @Bean
    public ClientConfiguration elasticsearchClient() {
        // 配置 Elasticsearch 连接参数
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();
    }
}
