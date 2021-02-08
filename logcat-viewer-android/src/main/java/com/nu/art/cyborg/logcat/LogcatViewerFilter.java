package com.nu.art.cyborg.logcat;

import com.nu.art.belog.consts.LogLevel;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LogcatViewerFilter {

	private Set<String> selectedThreads = new HashSet<>();
	private Set<String> selectedTags = new HashSet<>();
	private Set<LogLevel> selectedLogLevels = new HashSet<>();
	private String freeText;
	private boolean regexp;

	public final void unselectAllLogLevels() {
		selectedLogLevels.clear();
	}

	public final void unselectAllThreads() {
		selectedThreads.clear();
	}

	public final void unselectAllTags() {
		selectedTags.clear();
	}

	public final void addSelectedThread(String thread) {
		selectedThreads.add(thread);
	}

	public final void removeSelectedThread(String thread) {
		selectedThreads.remove(thread);
	}

	public final void addSelectedTag(String tag) {
		selectedTags.add(tag);
	}

	public final void removeSelectedTag(String tag) {
		selectedTags.remove(tag);
	}

	public void addSelectedLogLevels(LogLevel... selectedLogLevels) {
		this.selectedLogLevels.addAll(Arrays.asList(selectedLogLevels));
	}

	public void removeSelectedLogLevels(LogLevel... selectedLogLevels) {
		this.selectedLogLevels.removeAll(Arrays.asList(selectedLogLevels));
	}

	public Collection<LogLevel> getSelectedLogLevels() {
		return selectedLogLevels;
	}

	public Collection<String> getSelectedTags() {
		return selectedTags;
	}

	public Collection<String> getSelectedThreads() {
		return selectedThreads;
	}

	public void reset() {
		selectedLogLevels.clear();
		selectedTags.clear();
		selectedThreads.clear();
	}

	@SuppressWarnings( {
		                   "SimplifiableIfStatement",
		                   "RedundantIfStatement"
	                   })
	boolean check(LogcatEntry logcatEntry) {
		if (!selectedLogLevels.isEmpty() && !isSelected(logcatEntry.getLogLevel()))
			return false;

		if (!selectedTags.isEmpty() && !isTagSelected(logcatEntry.getTag()))
			return false;

		if (!selectedThreads.isEmpty() && !isThreadSelected(logcatEntry.getThread()))
			return false;

		if (freeText == null)
			return true;

		if (!regexp)
			return logcatEntry.getLogMessage().contains(freeText);

		// do regexp thingy

		return true;
	}

	public boolean isThreadSelected(String thread) {
		return selectedThreads.contains(thread);
	}

	public boolean isTagSelected(String tag) {
		return selectedTags.contains(tag);
	}

	public boolean isSelected(LogLevel logLevel) {
		return selectedLogLevels.contains(logLevel);
	}

	public void toggleSelection(LogLevel logLevel) {
		if (isSelected(logLevel))
			removeSelectedLogLevels(logLevel);
		else
			addSelectedLogLevels(logLevel);
	}

	public void toggleTagSelection(String tag) {
		if (isTagSelected(tag))
			removeSelectedTag(tag);
		else
			addSelectedTag(tag);
	}

	public void toggleThreadSelection(String thread) {
		if (isThreadSelected(thread))
			removeSelectedThread(thread);
		else
			addSelectedThread(thread);
	}
}