/*
 * Copyright (c) 2017 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *
 * This software code is not an 'Open Source'!
 * In order to use this code you MUST have a proper license.
 * In order to obtain a licence please contact me directly.
 *
 * Email: Adam.Zehavi@Nu-Art-Software.com
 */

package com.nu.art.cyborg.dnd;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;

public abstract class OnDragEventListener
	implements OnDragListener {

	@Override
	public final boolean onDrag(View v, DragEvent event) {
		printAction(event);

		switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_ENTERED:
				return onDragEntered(v, event);

			case DragEvent.ACTION_DRAG_EXITED:
				return onDragExited(v, event);

			case DragEvent.ACTION_DRAG_STARTED:
				return onDragStarted(v, event);

			case DragEvent.ACTION_DRAG_ENDED:
				return onDragEnded(v, event);

			case DragEvent.ACTION_DRAG_LOCATION:
				return onDragLocation(v, event);

			case DragEvent.ACTION_DROP:
				return onDropped(v, event);

			default:
				break;
		}
		return true;
	}

	private void printAction(DragEvent event) {
		String log;
		switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_ENTERED:
				log = "ACTION_DRAG_ENTERED";
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				log = "ACTION_DRAG_EXITED (" + event.getX() + ", " + event.getY() + ")";
				break;
			case DragEvent.ACTION_DRAG_STARTED:
				log = "ACTION_DRAG_STARTED";
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				log = "ACTION_DRAG_ENDED (" + event.getX() + ", " + event.getY() + ")";
				break;
			case DragEvent.ACTION_DRAG_LOCATION:
				log = "ACTION_DRAG_LOCATION (" + event.getX() + ", " + event.getY() + ")";
				break;
			case DragEvent.ACTION_DROP:
				log = "ACTION_DROP (" + event.getX() + ", " + event.getY() + ")";
				break;
			default:
				log = "UNKNOWN";
				break;
		}

		Log.i("Drag", log);
	}

	protected boolean onDragEntered(View v, DragEvent event) {
		return true;
	}

	protected boolean onDragExited(View v, DragEvent event) {
		return true;
	}

	protected boolean onDragStarted(View v, DragEvent event) {
		return true;
	}

	protected boolean onDragEnded(View v, DragEvent event) {
		return true;
	}

	protected boolean onDragLocation(View v, DragEvent event) {
		return true;
	}

	protected boolean onDropped(View v, DragEvent event) {
		return true;
	}
}
