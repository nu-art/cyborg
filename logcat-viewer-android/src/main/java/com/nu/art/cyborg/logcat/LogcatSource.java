package com.nu.art.cyborg.logcat;

import android.support.annotation.NonNull;

import com.nu.art.belog.Logger;
import com.nu.art.belog.consts.LogLevel;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class LogcatSource
	extends Logger
	implements Runnable {

	public interface OnLogUpdatedListener {

		void onLogUpdated(int newItems);
	}

	private OnLogUpdatedListener listener;

	private final Set<String> availableTags = new HashSet<>();
	private final Set<String> availableThreads = new HashSet<>();
	private final List<LogcatEntry> allLogs = new ArrayList<>();
	private final List<LogcatEntry> temp = new ArrayList<>();

	private volatile boolean buffer;
	private LogLevel lastLogLevel = LogLevel.Verbose;
	private String lastTag = null;
	private String lastThread = null;

	private final String name;
	private BufferedReader reader;
	private int scrollPosition;

	public LogcatSource(String name) {
		this.name = name;
	}

	protected abstract BufferedReader createReader()
		throws Exception;

	public String getName() {
		return name;
	}

	protected void setListener(OnLogUpdatedListener listener) {
		this.listener = listener;
	}

	public Collection<LogcatEntry> getFiltered(LogcatViewerFilter filter, int from, int to) {
		temp.clear();
		//		logDebug("getFiltered: " + from + " => " + to);
		for (int i = from; i < to; i++) {
			if (i >= allLogs.size())
				// TODO: this is a bug... need to investigate
				return temp;

			LogcatEntry logcatEntry = allLogs.get(i);
			if (filter.check(logcatEntry))
				temp.add(logcatEntry);
		}
		return temp;
	}

	int getLogEntryCount() {
		return allLogs.size();
	}

	public Collection<LogcatEntry> getFiltered(LogcatViewerFilter filter) {
		return getFiltered(filter, 0, allLogs.size());
	}

	public abstract boolean isAvailable();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		LogcatSource that = (LogcatSource) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public void run() {
		try {
			logInfo("Starting reading source: " + name);
			reader = createReader();
		} catch (Exception e) {
			logError("Failed reading source: " + name, e);
			_dispose();
			return;
		}

		int newItems = 0;
		long lastUpdate = 0;
		buffer = true;

		while (buffer) {
			String finalLine = null;
			try {
				if (reader.ready())
					finalLine = reader.readLine();
			} catch (IOException e) {
				buffer = false;
				continue;
			}

			if ((finalLine == null || newItems >= 1000) && buffer && System.currentTimeMillis() - lastUpdate > 1000) {
				if (newItems > 0) {
					lastUpdate = System.currentTimeMillis();
					listener.onLogUpdated(newItems);
				}
				newItems = 0;
			}

			if (finalLine == null) {
				// in case of dynamic logs... we would wait and collect the new logs
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					logError("Interrupted", e);
				}
				continue;
			}
			LogcatEntry logcatEntry = getLogCatItem(finalLine);
			synchronized (this) {
				newItems++;
				allLogs.add(logcatEntry);
			}
		}

		listener = null;
		_dispose();
	}

	private void _dispose() {
		try {
			logInfo("Disposing source: " + name);
			disposeImpl();
		} catch (Exception e) {
			logError("Error disposing source: " + name, e);
		}
	}

	protected void disposeImpl()
		throws Exception {
		if (reader == null)
			return;

		reader.close();
	}

	final void dispose() {
		buffer = false;
	}

	@NonNull
	private LogcatEntry getLogCatItem(String finalLine) {
		LogcatEntry logcatEntry = new LogcatEntry(finalLine);

		LogLevel logLevel = logcatEntry.getLogLevel();
		if (logLevel != null) {
			lastLogLevel = logLevel;
		} else {
			logcatEntry.setLogLevel(lastLogLevel);
		}

		String tag = logcatEntry.getTag();
		if (tag != null) {
			availableTags.add(tag);
			lastTag = tag;
		} else {
			logcatEntry.setTag(lastTag);
		}

		String thread = logcatEntry.getThread();
		if (thread != null) {
			availableThreads.add(thread);
			lastThread = thread;
		} else {
			logcatEntry.setThread(lastThread);
		}
		return logcatEntry;
	}

	public Collection<String> getAvailableTags() {
		ArrayList<String> tags = new ArrayList<>(availableTags);
		Collections.sort(tags);
		return tags;
	}

	public Collection<String> getAvailableThreads() {
		ArrayList<String> threads = new ArrayList<>(availableThreads);
		Collections.sort(threads);
		return threads;
	}
}
