package com.nu.art.cyborg.logcat;

import com.nu.art.belog.consts.LogLevel;
import com.nu.art.core.utils.RegexAnalyzer;

public class LogcatEntry {

	private RegexAnalyzer analyzer = new RegexAnalyzer("[\\d-:\\.]* [\\d-:\\.\\+]* ([VDIWEA]|.*?)/(.*?)(?:/(.*?))? *?[\\(:]");

	private final String logMessage;
	private LogLevel logLevel;
	private String tag;
	private String thread;

	public LogcatEntry(String logMessage) {
		this.logMessage = logMessage;
		setTagAndThread();
	}

	private void setTagAndThread() {
		String[] groups = analyzer.findRegex(0, logMessage, 1, 2, 3);
		if (groups.length == 0)
			return;

		if (groups[2] == null) {
			setLogLevel(groups[0]);
			tag = groups[1];
		} else {
			setLogLevel(groups[0]);
			thread = groups[1];
			tag = groups[2];
		}
	}

	public String getLogMessage() {
		return logMessage;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public String getThread() {
		return thread;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	private void setLogLevel(String logLevel) {
		switch (logLevel) {
			case "V":
			case "Verbose":
				this.logLevel = LogLevel.Verbose;
				break;
			case "D":
			case "Debug":
				this.logLevel = LogLevel.Debug;
				break;
			case "I":
			case "Info":
				this.logLevel = LogLevel.Info;
				break;
			case "W":
			case "Warning":
				this.logLevel = LogLevel.Warning;
				break;
			case "E":
			case "Error":
				this.logLevel = LogLevel.Error;
				break;
		}
	}
}