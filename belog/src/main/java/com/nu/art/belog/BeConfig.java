package com.nu.art.belog;

import com.nu.art.belog.consts.LogLevel;
import com.nu.art.core.tools.ArrayTools;

import java.util.regex.Pattern;

public class BeConfig {

	public Rule[] rules = {};
	public LoggerConfig[] configs = {};

	public BeConfig() {}

	public BeConfig setRules(Rule... rules) {
		this.rules = rules;
		return this;
	}

	public BeConfig setLoggersConfig(LoggerConfig... configs) {
		this.configs = configs;
		return this;
	}

	public final BeConfig merge(BeConfig config) {
		return new BeConfig().setRules(ArrayTools.appendElements(this.rules, config.rules))
		                     .setLoggersConfig(ArrayTools.appendElements(this.configs, config.configs));
	}

	public static class Rule {

		String tag;
		String thread;
		LogLevel minLevel = LogLevel.Verbose;
		LogLevel maxLevel = LogLevel.Assert;

		String[] loggerKeys;

		transient Pattern _tag;
		transient Pattern _thread;

		public Rule setLoggerKeys(String... loggerKeys) {
			this.loggerKeys = loggerKeys;
			return this;
		}

		public Rule setTag(String tag) {
			this.tag = tag;
			return this;
		}

		public Rule setThread(String thread) {
			this.thread = thread;
			return this;
		}

		public Rule setMinLevel(LogLevel minLevel) {
			this.minLevel = minLevel;
			return this;
		}

		public Rule setMaxLevel(LogLevel maxLevel) {
			this.maxLevel = maxLevel;
			return this;
		}
	}

	public static abstract class LoggerConfig {

		public final String type;
		public boolean isDefault;
		public String key;

		protected LoggerConfig(String type) {this.type = type;}

		public LoggerConfig setKey(String key) {
			this.key = key;
			return this;
		}
	}
}
