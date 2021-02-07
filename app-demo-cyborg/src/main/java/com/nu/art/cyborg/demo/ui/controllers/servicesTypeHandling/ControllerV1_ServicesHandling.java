

package com.nu.art.cyborg.demo.ui.controllers.servicesTypeHandling;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.ServiceConnection;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgAdapter;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.core.dataModels.ListDataModel;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.demo.ui.controllers.servicesTypeHandling.ServicesModule.ServicesTypes;

/**
 * Created by TacB0sS on 16-May 2016.
 */
public class ControllerV1_ServicesHandling
	extends CyborgController {

	@ViewIdentifier(viewId = R.id.ServiceSpinner,
	                listeners = ViewListener.OnItemSelected)
	private Spinner serviceSelector;

	@ViewIdentifier(viewId = R.id.ConnectionsSpinner,
	                listeners = ViewListener.OnItemSelected)
	private Spinner connectionsSelector;

	@ViewIdentifier(viewId = R.id.ServiceStateInfo)
	private TextView serviceInfo;

	@ViewIdentifier(viewId = R.id.StartService,
	                listeners = ViewListener.OnClick)
	private TextView startService;

	@ViewIdentifier(viewId = R.id.StopService,
	                listeners = ViewListener.OnClick)
	private TextView stopService;

	@ViewIdentifier(viewId = R.id.BindService,
	                listeners = ViewListener.OnClick)
	private TextView bindService;

	@ViewIdentifier(viewId = R.id.UnbindService,
	                listeners = ViewListener.OnClick)
	private TextView unbindService;

	private ListDataModel<ServiceConnection> connectionsDataModel;

	private ServicesModule servicesModule;

	private Runnable updateState = new Runnable() {
		int count = 0;

		@Override
		public void run() {
			if (count == 10)
				count = 0;

			updateServiceInfo();

			count++;
			if (count == 10)
				return;

			postOnUI(500, this);
		}
	};

	private CyborgAdapter<ServiceConnection> connectionsAdapter;

	public ControllerV1_ServicesHandling() {
		super(R.layout.controller__service_handling);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onCreate() {
		CyborgAdapter<ServicesTypes> servicesTypesAdapter = new CyborgAdapter(this, ServiceTypeRenderer.class) {
			@Override
			protected ServiceTypeRenderer instantiateItemRendererType(Class renderersType) {
				return new ServiceTypeRenderer();
			}
		};

		ListDataModel<ServicesTypes> servicesDataModel = new ListDataModel<>(ServicesTypes.class);
		servicesDataModel.add(ServicesTypes.values());
		servicesTypesAdapter.setDataModel(servicesDataModel);
		serviceSelector.setAdapter(servicesTypesAdapter.getArrayAdapter());

		connectionsAdapter = new CyborgAdapter(this, ServiceConnectionRenderer.class) {
			@Override
			protected ServiceConnectionRenderer instantiateItemRendererType(Class renderersType) {
				return new ServiceConnectionRenderer();
			}
		};

		connectionsDataModel = new ListDataModel(ServiceConnection.class);
		connectionsAdapter.setDataModel(connectionsDataModel);
		updateServiceInfo();
	}

	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedView, int position, long id) {
		switch (parentView.getId()) {
			case R.id.ServiceSpinner:
				updateServiceInfo();
		}
	}

	private void updateServiceInfo() {
		Class<? extends Service> serviceType = ServicesTypes.values()[serviceSelector.getSelectedItemPosition()].serviceType;
		String stateInfo = servicesModule.getServiceTypeInfo(serviceType);
		serviceInfo.setText(stateInfo);
		connectionsDataModel.clear();

		ServiceConnection[] serviceConnections = servicesModule.getServiceConnections(serviceType);
		if (serviceConnections.length > 0)
			connectionsDataModel.add(serviceConnections);
		else
			connectionsDataModel.add((ServiceConnection) null);

		connectionsSelector.setAdapter(connectionsAdapter.getArrayAdapter());
	}

	@Override
	public void onClick(View v) {
		Class<? extends Service> serviceType = ServicesTypes.values()[serviceSelector.getSelectedItemPosition()].serviceType;
		int selectedItemPosition = connectionsSelector.getSelectedItemPosition();
		ServiceConnection serviceConnection = connectionsDataModel.getItemForPosition(selectedItemPosition);

		switch (v.getId()) {
			case R.id.StartService:
				servicesModule.startService(getActivity(), serviceType);
				break;
			case R.id.StopService:
				servicesModule.stopService(getActivity(), serviceType);
				break;
			case R.id.BindService:
				servicesModule.bindService(getActivity(), serviceType, Context.BIND_AUTO_CREATE);
				break;
			case R.id.UnbindService:
				if (serviceConnection == null) {
					toastDebug("No Connection selected");
					return;
				}
				servicesModule.unbindService(getActivity(), serviceType, serviceConnection);
				break;
		}
		postOnUI(200, updateState);
	}

	@Override
	protected void onDestroy() {
		servicesModule.unbindAll(getActivity());
		super.onDestroy();
	}

	private class ServiceTypeRenderer
		extends ItemRenderer<ServicesTypes> {

		@ViewIdentifier(viewId = R.id.ServiceName)
		TextView serviceType;

		public ServiceTypeRenderer() {
			super(R.layout.renderer__service_type);
		}

		@Override
		protected void renderItem(ServicesTypes item) {
			serviceType.setText(item.labelId);
		}
	}

	private class ServiceConnectionRenderer
		extends ItemRenderer<ServiceConnection> {

		@ViewIdentifier(viewId = R.id.Connection)
		TextView connectionId;

		public ServiceConnectionRenderer() {
			super(R.layout.renderer__service_connection);
		}

		@Override
		@SuppressLint("SetTextI18n")
		protected void renderItem(ServiceConnection connection) {
			if (connection == null) {
				connectionId.setText("No Connections");
				return;
			}
			connectionId.setText("Connection: " + connection.toString().split("@")[1]);
		}
	}
}
