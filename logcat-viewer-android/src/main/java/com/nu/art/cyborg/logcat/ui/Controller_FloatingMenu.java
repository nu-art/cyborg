package com.nu.art.cyborg.logcat.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.nu.art.belog.consts.LogLevel;
import com.nu.art.core.generics.Processor;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.logcat.LogcatSource;
import com.nu.art.cyborg.logcat.Module_LogcatViewer;
import com.nu.art.cyborg.logcat.R;
import com.nu.art.cyborg.logcat.interfaces.OnLogSourceChangedListener;
import com.nu.art.cyborg.logcat.interfaces.OnMenuItemClickedListener;
import com.nu.art.cyborg.logcat.sources.Logcat_ArchivedLogFile;
import com.nu.art.cyborg.logcat.sources.Logcat_LiveSystemLogs;
import com.nu.art.cyborg.logcat.sources.Logcat_LogFile;
import com.nu.art.cyborg.logcat.ui.Controller_SelectionDialog.DialogModel;

/**
 * Created by TacB0sS on 16/03/2018.
 */

@SuppressWarnings("unchecked")
public class Controller_FloatingMenu
	extends CyborgController
	implements OnClickListener, OnLongClickListener, OnMenuItemClickedListener, OnLogSourceChangedListener {

	private Module_LogcatViewer module;
	private Controller_SelectionDialog dialog;
	private View upperBar;
	private View rightBar;

	private Runnable autoHideMenuAction = new Runnable() {
		boolean showToast;

		@Override
		public void run() {
			if (!showToast) {
				toastShort(R.string.Toast_DoubleTapForMenu);
				showToast = true;
			}

			hideButtons();
		}
	};

	@Override
	public void scrollToBottom() {
	}

	@Override
	public void scrollToTop() {
	}

	@Override
	public void renderList() {
		if (dialog == null)
			return;

		dialog.invalidateDataModel();
	}

	private ImageView shareBtn;
	private boolean menuIsVisible;

	private int[] viewIds = {
		R.id.iv_FilterTag,
		R.id.iv_FilterThread,
		R.id.iv_FilterLogLevel,
		R.id.iv_SelectSource,
		R.id.iv_Share,

		R.id.iv_ScrollToTop,
		R.id.iv_ScrollToBottom,

		R.id.iv_IncreaseFontSize,
		R.id.iv_DecreaseFontSize,
	};

	protected Controller_FloatingMenu() {
		super(R.layout.controller__floating_menu);
	}

	@Override
	protected void extractMembers() {
		upperBar = getViewById(R.id.LL_UpperBar);
		rightBar = getViewById(R.id.LL_RightBar);
		for (int viewId : viewIds) {
			View view = getViewById(viewId);
			view.setOnClickListener(this);
			view.setOnLongClickListener(this);
			//			setBackground(view, getDrawable(R.drawable.bg__black));
		}
	}

	@Override
	public void onCreate() {
		getRootView().setOnTouchListener(new OnTouchListener() {
			private long firstTap;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getActionMasked() != MotionEvent.ACTION_DOWN)
					return false;

				if (System.currentTimeMillis() - firstTap < 300) {
					animateButtons(!menuIsVisible);
					return false;
				}

				firstTap = System.currentTimeMillis();
				return false;
			}
		});
	}

	@Override
	public void onClick(View view) {
		if (!canExecute())
			return;

		int i = view.getId();
		if (i == R.id.iv_ScrollToTop) {
			dispatchEvent("Scroll to top", OnMenuItemClickedListener.class, new Processor<OnMenuItemClickedListener>() {
				@Override
				public void process(OnMenuItemClickedListener listener) {
					listener.scrollToTop();
				}
			});
			return;
		}

		if (i == R.id.iv_ScrollToBottom) {
			dispatchEvent("Scroll to bottom", OnMenuItemClickedListener.class, new Processor<OnMenuItemClickedListener>() {
				@Override
				public void process(OnMenuItemClickedListener listener) {
					listener.scrollToBottom();
				}
			});
			return;
		}

		if (i == R.id.iv_IncreaseFontSize) {
			module.changeTextSize(module.getTextSize() + 1);
			dispatchRenderList();
			return;
		}

		if (i == R.id.iv_DecreaseFontSize) {
			module.changeTextSize(module.getTextSize() - 1);
			dispatchRenderList();
			return;
		}

		if (i == R.id.iv_FilterTag) {
			popTagSelectionDialog();
			return;
		}

		if (i == R.id.iv_FilterThread) {
			popThreadSelectionDialog();
			return;
		}

		if (i == R.id.iv_FilterLogLevel) {
			popLogLevelSelectionDialog();
			return;
		}

		if (i == R.id.iv_SelectSource) {
			popSourceSelectionDialog();
			return;
		}

		//		if (i == R.id.iv_Share) {
		//			logViewController.shareSelectedItems();
		//			return;
		//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		showButtons();
		postOnUI(3000, autoHideMenuAction);
	}

	private void dispatchRenderList() {
		dispatchEvent("Render list", OnMenuItemClickedListener.class, new Processor<OnMenuItemClickedListener>() {
			@Override
			public void process(OnMenuItemClickedListener listener) {
				listener.renderList();
			}
		});
	}

	@Override
	public boolean onLongClick(View view) {
		toastDebug(view.getContentDescription().toString());
		return true;
	}

	private void showButtons() {
		animateButtons(true);
	}

	private void hideButtons() {
		animateButtons(false);
	}

	private void animateButtons(boolean visible) {
		// make sure we do not leave any dirt
		removeActionFromUI(autoHideMenuAction);

		float startAlpha = upperBar.getAlpha();
		float targetAlpha = visible ? 1 : 0;
		float scale = visible ? 1 : 0;
		float translate = visible ? 0 : -200;
		int duration = (int) (Math.abs(targetAlpha - startAlpha) * 400);
		upperBar.animate().translationY(translate).scaleX(scale).alpha(targetAlpha).setDuration(duration).start();
		rightBar.animate().translationX(-translate).scaleY(scale).alpha(targetAlpha).setDuration(duration).start();
		this.menuIsVisible = visible;
	}

	private void showSelectionDialog(final DialogModel model) {
		createLayerBuilder()
			.setControllerType(Controller_SelectionDialog.class)
			.setKeepBackground(true)
			.setProcessor(new Processor<Controller_SelectionDialog>() {
				@Override
				public void process(Controller_SelectionDialog dialog) {
					Controller_FloatingMenu.this.dialog = dialog;
					dialog.setModel(model);
				}
			})
			.push();
		hideButtons();
	}

	private void popSourceSelectionDialog() {
		DialogModel<LogcatSource> model = new DialogModel<>();
		model.setItems(module.getAvailableSources());
		model.itemTypes = new Class[]{
			Logcat_LiveSystemLogs.class,
			Logcat_LogFile.class,
			Logcat_ArchivedLogFile.class,
		};

		model.rendererTypes = new Class[]{
			Renderer_LogSource.class,
			Renderer_LogSource.class,
			Renderer_LogSource.class,
		};
		model.isSingleSelection = true;

		showSelectionDialog(model);
	}

	private void popLogLevelSelectionDialog() {
		DialogModel<LogLevel> model = new DialogModel<>();
		model.setItems(LogLevel.values());
		model.itemTypes = new Class[]{
			LogLevel.class,
		};

		model.rendererTypes = new Class[]{
			Renderer_LogLevelItem.class,
		};

		showSelectionDialog(model);
	}

	private void popThreadSelectionDialog() {
		LogcatSource logcatSource = module.getActiveSource();
		DialogModel<String> model = new DialogModel<>();
		model.setItems(logcatSource.getAvailableThreads());
		model.itemTypes = new Class[]{
			String.class,
		};

		model.rendererTypes = new Class[]{
			Renderer_LogThread.class,
		};

		showSelectionDialog(model);
	}

	private void popTagSelectionDialog() {
		LogcatSource logcatSource = module.getActiveSource();
		DialogModel<String> model = new DialogModel<>();
		model.setItems(logcatSource.getAvailableTags());
		model.itemTypes = new Class[]{
			String.class,
		};

		model.rendererTypes = new Class[]{
			Renderer_LogTag.class,
		};

		showSelectionDialog(model);
	}

	@Override
	public void onLogcatSourceChanged() {
		getActivity().onBackPressed();
	}
}



