package com.nu.art.cyborg.logcat.ui;

import com.nu.art.core.interfaces.Getter;
import com.nu.art.cyborg.core.CyborgAdapter;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgRecycler;
import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.core.dataModels.ListDataModel;
import com.nu.art.cyborg.logcat.R;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by TacB0sS on 16/03/2018.
 */

public class Controller_SelectionDialog<Type>
	extends CyborgController {

	public static class DialogModel<Type> {

		private Collection<Type> items;
		public Class<? extends ItemRenderer<? extends Type>>[] rendererTypes;
		public Class<? extends Type>[] itemTypes;
		public boolean isSingleSelection;

		public void setItems(Collection<Type> items) {
			this.items = items;
		}

		public void setItems(Type[] items) {
			this.items = Arrays.asList(items);
		}
	}

	private CyborgRecycler recyclerView;
	private DialogModel<Type> model;

	protected Controller_SelectionDialog() {
		super(R.layout.controller__selection_dialog);
	}

	@Override
	protected void extractMembers() {
		recyclerView = getViewById(R.id.rv_Selection);
	}

	public void setModel(DialogModel<Type> model) {
		this.model = model;
		show();
	}

	@Override
	public void onCreate() {
		dismissOnTouchEvent();
	}

	void invalidateDataModel() {
		recyclerView.invalidateDataModel();
	}

	private void show() {
		CyborgAdapter<Type> adapter = new CyborgAdapter<>(this, model.rendererTypes);
		recyclerView.setAdapter(adapter);

		ItemsResolver resolver = new ItemsResolver();
		adapter.setResolver(resolver);
	}

	private class ItemsResolver
		implements Getter<ListDataModel<Type>> {

		ListDataModel<Type> dataModel = new ListDataModel<>(model.itemTypes);

		@Override
		public ListDataModel<Type> get() {
			dataModel.clear();
			dataModel.addAll(model.items);
			return dataModel;
		}
	}
}
