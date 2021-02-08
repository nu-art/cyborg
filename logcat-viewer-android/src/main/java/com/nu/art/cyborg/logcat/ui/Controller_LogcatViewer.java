package com.nu.art.cyborg.logcat.ui;

import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.utils.DebugFlags;
import com.nu.art.core.utils.DebugFlags.DebugFlag;
import com.nu.art.cyborg.core.CyborgAdapter;
import com.nu.art.cyborg.core.CyborgAdapter.CyborgRecyclerAdapter;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgRecycler;
import com.nu.art.cyborg.core.CyborgStackController;
import com.nu.art.cyborg.core.abs._DebugFlags;
import com.nu.art.cyborg.core.dataModels.DataModel;
import com.nu.art.cyborg.core.dataModels.ListDataModel;
import com.nu.art.cyborg.logcat.LogcatEntry;
import com.nu.art.cyborg.logcat.LogcatSource;
import com.nu.art.cyborg.logcat.LogcatSource.OnLogUpdatedListener;
import com.nu.art.cyborg.logcat.Module_LogcatViewer;
import com.nu.art.cyborg.logcat.R;
import com.nu.art.cyborg.logcat.interfaces.OnLogSourceChangedListener;
import com.nu.art.cyborg.logcat.interfaces.OnMenuItemClickedListener;

import java.util.Collection;

/**
 * Created by TacB0sS on 16/03/2018.
 */

public class Controller_LogcatViewer
	extends CyborgController
	implements OnLogUpdatedListener, OnMenuItemClickedListener, OnLogSourceChangedListener {

	private DebugFlags.DebugFlag[] flags = {
		CyborgStackController.DebugFlag,
		CyborgController.DebugFlag,
		_DebugFlags.Debug_Performance
	};

	private boolean[] state = new boolean[flags.length];

	private LogItemsResolver resolver;
	@SuppressWarnings("unused")
	private Module_LogcatViewer module;
	private CyborgRecycler logcatRecycler;

	public Controller_LogcatViewer() {
		super(R.layout.controller__logcat);
	}

	@Override
	protected void extractMembers() {
		logcatRecycler = getViewById(R.id.RV_LogcatViewer);
	}

	@Override
	protected void onCreate() {
		for (int i = 0; i < flags.length; i++) {
			state[i] = flags[i].isEnabled();
			flags[i].disable();
		}

		CyborgAdapter<LogcatEntry> adapter = new CyborgAdapter<>(this, Renderer_LogcatEntry.class);
		adapter.setResolver(resolver = new LogItemsResolver());
		logcatRecycler.setAdapter(adapter);
		if (module.getActiveSource() == null) {
			LogcatSource[] sources = module.getAvailableSources();
			module.setActiveSourceAndRead(sources[0]);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		module.setLogUpdateListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		module.setLogUpdateListener(null);
	}

	@Override
	protected void onDestroy() {
		for (int i = 0; i < flags.length; i++) {
			flags[i].enable(state[i]);
		}
	}

	@Override
	public void onLogUpdated(final int newItems) {
		postOnUI(new Runnable() {

			@Override
			public void run() {
				int count = resolver.model.getItemsCount();
				Collection<LogcatEntry> filteredLogs = module.getFilteredLogs(count, count + newItems);
				resolver.model.addAll(filteredLogs);
			}
		});
	}

	@Override
	public void scrollToTop() {
		logcatRecycler.scrollToPosition(0);
	}

	@Override
	public void scrollToBottom() {
		logcatRecycler.scrollToPosition(resolver.model.getItemsCount() - 1);
	}

	@Override
	public void renderList() {
		logcatRecycler.invalidateDataModel();
	}

	@Override
	public void onLogcatSourceChanged() {
		resolver.model.clear();
	}

	private class LogItemsResolver
		implements Getter<DataModel<LogcatEntry>> {

		@SuppressWarnings("unchecked")
		private ListDataModel<LogcatEntry> model = new ListDataModel<>(LogcatEntry.class);

		@Override
		public DataModel<LogcatEntry> get() {
			model.clear();
			model.addAll(module.getFilteredLogs());
			return model;
		}
	}
}
