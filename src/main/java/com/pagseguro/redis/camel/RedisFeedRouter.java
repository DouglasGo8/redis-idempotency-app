package com.pagseguro.redis.camel;

import lombok.NoArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.SetOperations;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;


@NoArgsConstructor
public class RedisFeedRouter extends RouteBuilder {


    /**
     *
     */
    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOps;


    private static final Logger logger = LoggerFactory.getLogger(RedisFeedRouter.class);


    @Override
    public void configure() throws Exception {


        from("seda:a")
                //.log("Hi from Seda Component with ${body}")
                .process((exchange) -> {

                    final String key = String.format("key_%s", UUID.randomUUID().toString());
                    final String body = String.format("The Quick Quarkus jumps over lazy Spring at %s",
                            LocalDateTime.now().toString());

                    this.setOps.getOperations()
                            .opsForValue()
                            .set(key, body);

                    //exchange.getIn().setBody(key);
                })
                //.to("seda:b")
                .end();


        /*from("seda:b")
                .process((exchange) -> {
                    final String key = exchange.getIn().getBody(String.class);
                    logger.info(this.setOps.getOperations().opsForValue().get(key));
                });*/

    }
}
