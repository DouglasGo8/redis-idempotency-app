package com.pagseguro.redis.camel;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.camel.builder.RouteBuilder;

@Data
@NoArgsConstructor
public class TimerRedisRouter extends RouteBuilder {

    /**
     *
     */
    private int periodInSeconds;

    /**
     *
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {

        from(String.format("timer://myTimer?fixedRate=true&period=%ds", periodInSeconds))
                .to("seda:a")
                .end();

    }
}
