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

import android.content.ClipData;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import java.util.HashMap;

@SuppressWarnings("unchecked")
public class DND_Handler<ItemType> {

	public static abstract class DragHandler<ItemType> {

		protected abstract ItemType convert(View view);

		protected DragShadowBuilder createDragShadowBuilder(View view) {
			return new View.DragShadowBuilder(view);
		}

		protected ClipData createClipData(View view) {
			return ClipData.newPlainText("", "");
		}

		public void onDragStarted(View view) {
			view.setAlpha(0.2f);
		}
	}

	public static abstract class DropHandler<ItemType> {

		protected boolean canAdd(View view, ItemType item) {
			return true;
		}

		protected void renderDrop(View container, ItemType item, float x, float y) {}

		protected boolean onPerformDrop(View container, View dragged, ItemType item) {
			ViewGroup owner = (ViewGroup) dragged.getParent();
			owner.removeView(dragged);
			((ViewGroup) container).addView(dragged);
			return true;
		}

		protected void onExited(View container) {}

		protected void onDropEnded(View view, ItemType item, boolean success) {
			view.setAlpha(1f);
		}
	}

	private class Transferable {

		View view;

		ItemType item;

		public Transferable(View view, ItemType item) {
			this.view = view;
			this.item = item;
		}
	}

	private OnDragListener dropListener = new OnDragEventListener() {

		@Override
		protected boolean onDragEntered(View container, DragEvent event) {
			DropHandler<ItemType> dropHandler = dropHandlers.get(container);
			Transferable localState = (Transferable) event.getLocalState();
			return dropHandler.canAdd(localState.view, localState.item);
		}

		@Override
		protected boolean onDragLocation(View container, DragEvent event) {
			DropHandler<ItemType> dropHandler = dropHandlers.get(container);
			Transferable localState = (Transferable) event.getLocalState();
			dropHandler.renderDrop(container, localState.item, event.getX(), event.getY());
			return true;
		}

		@Override
		protected boolean onDragExited(View container, DragEvent event) {
			DropHandler<ItemType> dropHandler = dropHandlers.get(container);
			dropHandler.onExited(container);
			return true;
		}

		@Override
		protected boolean onDragEnded(View container, DragEvent event) {
			Transferable localState = (Transferable) event.getLocalState();
			DropHandler<ItemType> dropHandler = dropHandlers.get(container);
			dropHandler.onDropEnded(localState.view, localState.item, event.getResult());
			return true;
		}

		@Override
		protected boolean onDropped(View container, DragEvent event) {
			Transferable transferable = (Transferable) event.getLocalState();
			DropHandler<ItemType> dropHandler = dropHandlers.get(container);
			return dropHandler.onPerformDrop(container, transferable.view, transferable.item);
		}
	};

	private class DragTouchListener
		implements OnTouchListener {

		long last;

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			long dt = System.currentTimeMillis() - last;
			if (event.getAction() == MotionEvent.ACTION_DOWN && event.getPointerCount() == 1 && dt > 100) {
				last = System.currentTimeMillis();
				DragHandler<ItemType> dragHandler = dragHandlers.get(view);
				DragShadowBuilder shadowBuilder = dragHandler.createDragShadowBuilder(view);
				ClipData clipData = dragHandler.createClipData(view);
				ItemType item = dragHandler.convert(view);
				view.startDrag(clipData, shadowBuilder, new Transferable(view, item), 0);
				dragHandler.onDragStarted(view);
				return true;
			}
			return false;
		}
	}

	DragTouchListener dragTouchListener = new DragTouchListener();

	HashMap<View, DragHandler<ItemType>> dragHandlers = new HashMap<>();

	HashMap<View, DropHandler<ItemType>> dropHandlers = new HashMap<>();

	public DND_Handler() {}

	public final void addDraggableView(View view, DragHandler<ItemType> dragHandler) {
		view.setOnTouchListener(dragTouchListener);
		dragHandlers.put(view, dragHandler);
	}

	public final void addDropView(View dropView, DropHandler<ItemType> condition) {
		dropHandlers.put(dropView, condition);
		dropView.setOnDragListener(dropListener);
	}
}
