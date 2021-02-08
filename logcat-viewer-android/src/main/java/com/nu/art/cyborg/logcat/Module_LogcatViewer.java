package com.nu.art.cyborg.logcat;

import android.graphics.Color;
import android.os.Handler;

import com.nu.art.belog.BeLogged;
import com.nu.art.belog.LoggerClient;
import com.nu.art.belog.loggers.FileLogger;
import com.nu.art.belog.consts.LogLevel;
import com.nu.art.core.generics.Processor;
import com.nu.art.core.interfaces.Condition;
import com.nu.art.core.tools.ArrayTools;
import com.nu.art.cyborg.core.CyborgModule;
import com.nu.art.cyborg.core.modules.ThreadsModule;
import com.nu.art.cyborg.logcat.LogcatSource.OnLogUpdatedListener;
import com.nu.art.cyborg.logcat.interfaces.OnLogSourceChangedListener;
import com.nu.art.cyborg.logcat.interfaces.OnMenuItemClickedListener;
import com.nu.art.cyborg.logcat.sources.Logcat_ArchivedLogFile;
import com.nu.art.cyborg.logcat.sources.Logcat_LiveSystemLogs;
import com.nu.art.cyborg.logcat.sources.Logcat_LogFile;
import com.nu.art.storage.CustomPreference;
import com.nu.art.storage.FloatPreference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by TacB0sS on 17/03/2018.
 */

public class Module_LogcatViewer
	extends CyborgModule
	implements OnLogUpdatedListener {

	private static final Collection<LogcatEntry> EmptyLogItemsCollection = new ArrayList<>();

	private static final int TextSize_Max = 24;
	private static final int TextSize_Min = 4;

	private CustomPreference<LogcatTheme> themes;
	private FloatPreference textSize;
	private FileLogger fileLogger;
	private Set<LogcatSource> sources = new HashSet<>();

	private LogcatViewerFilter filter = new LogcatViewerFilter();
	private LogcatSource activeSource;
	private Handler handler;
	private OnLogUpdatedListener listener;

	@Override
	protected void init() {
		themes = new CustomPreference<>("themes", LogcatTheme.class, new LogcatTheme());
		textSize = new FloatPreference("text-size", 6f);
		handler = getModule(ThreadsModule.class).getDefaultHandler("log reader");
		LoggerClient[] clients = BeLogged.getInstance().getClients();
		for (LoggerClient client : clients) {
			if (client instanceof FileLogger) {
				fileLogger = (FileLogger) client;
				break;
			}
		}
		sources.add(new Logcat_LiveSystemLogs());
	}

	public LogcatSource[] getAvailableSources() {
		if (fileLogger == null)
			return ArrayTools.asArray(sources, LogcatSource.class);

		File[] allLogFiles = fileLogger.getAllLogFiles();
		for (File logFile : allLogFiles) {
			if (logFile.getName().endsWith("zip")) {
				File tempFile = new File(getApplicationContext().getCacheDir(), logFile.getName().replace(".zip", "-temp.txt"));
				sources.add(new Logcat_ArchivedLogFile(logFile, tempFile));
			} else
				sources.add(new Logcat_LogFile(logFile));
		}

		return ArrayTools.asFilteredArray(LogcatSource.class, sources, new Condition<LogcatSource>() {
			@Override
			public boolean checkCondition(LogcatSource item) {
				return item.isAvailable();
			}
		});
	}

	void addLogcatSource(LogcatSource source) {
		sources.add(source);
	}

	public final void changeTextSize(float textSizeSp) {
		if (textSizeSp < TextSize_Min) {
			return;
		}

		if (textSizeSp > TextSize_Max) {
			return;
		}

		this.textSize.set(textSizeSp);
	}

	public final float getTextSize() {
		return this.textSize.get(false);
	}

	public void setActiveSourceAndRead(LogcatSource source) {
		if (this.activeSource != null && this.activeSource != source)
			this.activeSource.dispose();

		this.activeSource = source;
		activeSource.setListener(this);
		handler.post(this.activeSource);
	}

	public final Collection<LogcatEntry> getFilteredLogs() {
		if (activeSource == null) {
			EmptyLogItemsCollection.clear();
			return EmptyLogItemsCollection;
		}

		return getFilteredLogs(0, activeSource.getLogEntryCount());
	}

	public final Collection<LogcatEntry> getFilteredLogs(int from, int to) {
		if (activeSource == null) {
			EmptyLogItemsCollection.clear();
			return EmptyLogItemsCollection;
		}

		return activeSource.getFiltered(filter, from, to);
	}

	@Override
	public void onLogUpdated(int newItems) {
		if (listener != null)
			listener.onLogUpdated(newItems);
	}

	public void setLogUpdateListener(OnLogUpdatedListener listener) {
		this.listener = listener;
	}

	public LogcatViewerFilter getFilter() {
		return filter;
	}

	public LogcatSource getActiveSource() {
		return activeSource;
	}

	public void replaceSource(LogcatSource item) {
		if (activeSource != null) {
			if (activeSource.equals(item))
				return;

			activeSource.dispose();
		}

		dispatchEvent("Selection completed", OnLogSourceChangedListener.class, new Processor<OnLogSourceChangedListener>() {
			@Override
			public void process(OnLogSourceChangedListener listener) {
				listener.onLogcatSourceChanged();
			}
		});
		setActiveSourceAndRead(item);
	}

	public void onFilterUpdated() {
		dispatchEvent("Filter update", OnMenuItemClickedListener.class, new Processor<OnMenuItemClickedListener>() {
			@Override
			public void process(OnMenuItemClickedListener listener) {
				listener.renderList();
			}
		});
	}

	public int getTextColor(LogLevel logLevel) {
		switch (logLevel) {
			case Verbose:
				return Color.parseColor(themes.get(false).verbose);

			case Debug:
				return Color.parseColor(themes.get(false).debug);

			case Info:
				return Color.parseColor(themes.get(false).info);

			case Warning:
				return Color.parseColor(themes.get(false).warning);

			case Error:
				return Color.parseColor(themes.get(false).error);

			default:
				return Color.parseColor(themes.get(false).verbose);
		}
	}
}
