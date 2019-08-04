package com.pagseguro.redis.camel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;

import lombok.NoArgsConstructor;

/**
 * 
 * @author dbatista
 *
 */
@NoArgsConstructor
public class RedisRestApiRouter extends RouteBuilder {

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Resource(name = "redisTemplate")
	private SetOperations<String, String> setOps;
	
	@Value(value = "${endpoint.cluster}")
	String clusterNodeEndpoint;

	@Override
	public void configure() throws Exception {

		onException(Exception.class)
			.handled(true)
			.setBody()
			.simple("${exception.message}");

		restConfiguration("rest-api")
			.component("undertow")
			.host("0.0.0.0")
			.port(9090)
			.enableCORS(true)
				.bindingMode(RestBindingMode.json);

		rest("/idempotency/v1")
			.get("/list/all")
				.id("api-idempotency-get-all-keys")
				.produces("application/json")
			.route()
				.process(exchange -> {
					
					/**
					 * Not recommend to works in Cluster
					 */
					
					 /* final List<RedisFeedModel> response = this.setOps
							  .getOperations()
							  .keys("key_*") 
							  .stream()
							  	.map(this::valueOfKey)
							  	.map(RedisFeedModel::new)
							  .collect(Collectors.toList());*/
					 
					// out.println(redisTemplate);
					
					/*this.setOps.getOperations()
						.opsForCluster()
						.keys(new RedisClusterNode(clusterNodeEndpoint, 6379), "key_*")
						.stream()
						.map(this::valueOfKey)
						.map(RedisFeedModel::new)
						.collect(Collectors.toList())
						.forEach(out::println);*/
					
					List<RedisFeedModel> listOfKeys = new ArrayList<RedisFeedModel>();
					
					this.redisTemplate
						.getConnectionFactory()
							.getClusterConnection()
						.scan(ScanOptions.scanOptions().match("key_*").build())
						.forEachRemaining(entry -> {

							final String key = new String(entry);
							
							final String value = new String(this.redisTemplate
									.getConnectionFactory()
									.getClusterConnection()
									.get(entry));
							
							listOfKeys.add(new RedisFeedModel(key, value));
						});
						
					//
					exchange.getOut().setBody(listOfKeys);
					//
				})
				.endRest()
			.delete("/remove/{key}")
				.description("Deletes the specified key")
				.param()
					.name("key")
				.type(RestParamType.path)
					.description("The key of the item")
					.dataType("String")
					.endParam()
					.responseMessage()
						.code(200)
						.message("Successfully deleted item")
					.endResponseMessage()
				.produces("application/json")
				.route()
				.process(exchange -> {
					
					final String key = exchange
							.getIn()
							.getHeader("key", String.class);
					
					this.setOps
					  .getOperations().delete(key);
					
					exchange.getOut()
						.setBody(simple(String.format(" %s removed", key)));
				})
			.endRest()
			.delete("/remove/all")
				.id("api-idempotency-delete")
				.produces("application/json")
			.route()
				.process(exchange -> {
						
						this.redisTemplate
							.getConnectionFactory()
							.getConnection()
								.flushAll();
						
						exchange
							.getOut()
							.setBody(simple("All keys removed")); 
						
				}).endRest();

	}

}
