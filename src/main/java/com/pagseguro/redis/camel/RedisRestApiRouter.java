package com.pagseguro.redis.camel;

import lombok.NoArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.data.redis.core.SetOperations;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
public class RedisRestApiRouter extends RouteBuilder {


    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOps;

    @Override
    public void configure() throws Exception {


        restConfiguration("rest-api")
                .component("undertow")
                .host("0.0.0.0")
                .port(9090)
                .enableCORS(true)
                .bindingMode(RestBindingMode.json);

            rest("/idempotency/v1")
                .get("/list")
                    .id("api-idempotency")
                .produces("application/json")
                .route()
                .process(exchange -> {
                    final List<RedisFeedModel> response = this.setOps
                            .getOperations()
                            .keys("key_*")
                            .stream()
                            .map(key -> new RedisFeedModel(key,
                                    this.setOps.getOperations()
                                            .opsForValue().get(key)))
                            .collect(Collectors.toList());
                    exchange.getIn().setBody(response);
                })
                .endRest();
    }
}
