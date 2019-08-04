package com.pagseguro.redis.camel;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.data.redis.core.SetOperations;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public class RedisFeedRouter extends RouteBuilder {


    /**
     *
     */
    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOps;


    // private static final Logger logger = LoggerFactory.getLogger(RedisFeedRouter.class);


    @Override
    public void configure() throws Exception {

    	
    	onException(Exception.class)
			.handled(true)
			.setBody()
			.simple("${exception.message}");

        from("seda:a")
                //.log("Hi from Seda Component with ${body}")
                .process((exchange) -> {

                    final String key = String.format("key_%s", UUID.randomUUID().toString());
                    final String body = String.format("The quick Quarkus jumps over the lazy Spring at %s",
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
