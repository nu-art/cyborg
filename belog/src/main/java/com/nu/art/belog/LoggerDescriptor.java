package com.nu.art.belog;

import com.nu.art.belog.BeConfig.LoggerConfig;

public class LoggerDescriptor<Config extends LoggerConfig, Logger extends LoggerClient> {

	final String key;
	final Class<Config> configType;
	final Class<Logger> loggerType;

	public LoggerDescriptor(String key, Class<Config> configType, Class<Logger> loggerType) {
		this.key = key;
		this.configType = configType;
		this.loggerType = loggerType;
	}

	public String getKey() {
		return key;
	}

	public Class<Config> getConfigType() {
		return configType;
	}

	protected void validateConfig(Config config)
		throws RuntimeException {
	}
}
