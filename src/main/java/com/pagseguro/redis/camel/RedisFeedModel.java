package com.pagseguro.redis.camel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 */
@Data
@AllArgsConstructor
public class RedisFeedModel {
	
	private final String key;
	private final String value;
}
