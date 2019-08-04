package com.pagseguro.redis.camel;

import org.apache.camel.builder.RouteBuilder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author dbatista
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
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

    	onException(Exception.class)
		.handled(true)
		.setBody()
			.simple("${exception.message}");
		
		//from("timer://myTimer?fixedRate=true&period=30s")
		//	.to("seda:a")
		//	.end();

		
		  from(String.format("timer://myTimer?fixedRate=true&period=%ds", periodInSeconds))
		  	.to("seda:a")
		  	.end();
		 

	}
}
