/*
 * The google-maps module, is an implementation of Google Maps SDK
 * for Android, allowing you to use the power of Cyborg, with Google Maps.
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.cyborg.maps.google;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cyborg.googlemapsmodule.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nu.art.core.generics.Processor;
import com.nu.art.cyborg.core.CyborgController;

import java.util.ArrayList;
import java.util.Random;

public class Controller_GoogleMap
	extends CyborgController
	implements OnMapReadyCallback, OnMapClickListener, OnMapLongClickListener, OnMarkerClickListener, OnInfoWindowClickListener {

	public static class MapMarker {

		public LatLng position;

		private float color;

		private String title;

		private int iconRes;

		private Marker marker;

		public MapMarker(LatLng position, float color, String title, int iconRes) {
			this.position = position;
			this.color = color;
			this.title = title;
			this.iconRes = iconRes;
		}
	}

	private MapFragment mapFragment;

	private GoogleMap googleMap;

	private Marker myMarker;

	private ArrayList<MapMarker> markers = new ArrayList<>();

	//	private PolylineOptions rectLine;

	private Polyline polyline;

	/**
	 * Range is between 2 to 21
	 */
	private int cameraZoom = 16;

	private int mapLayoutId;

	public Controller_GoogleMap() {
		super(R.layout.controller__google_maps_fragment);
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		setMapFragmentId();
	}

	private void setMapFragmentId() {
		Random random = new Random();
		while (getActivity().findViewById(mapLayoutId = Math.abs(random.nextInt())) != null)
			;

		FrameLayout fl = new FrameLayout(getActivity());

		((ViewGroup) getRootView()).addView(fl);
		fl.setId(mapLayoutId);
		getActivity().findViewById(fl.getId());
	}

	private void removeAllMarkers() {
		googleMap.clear();
	}

	public void showMap() {
		showMap(null);
	}

	/**
	 * Show the map and pass initial map options.
	 *
	 * @param mapOptions a GoogleMapOptions object
	 *
	 * @see <a href="https://developers.google.com/maps/documentation/android-api/map#programmatically">Map options</a>
	 */
	public void showMap(GoogleMapOptions mapOptions) {
		if (mapFragment != null)
			return;

		if (mapOptions == null)
			mapFragment = MapFragment.newInstance();
		else
			mapFragment = MapFragment.newInstance(mapOptions);

		addMapFragmentToViewHierarchy();
	}

	private void addMapFragmentToViewHierarchy() {
		final FragmentManager fm = getActivity().getFragmentManager();

		if (getActivity().findViewById(mapLayoutId) == null)
			return;

		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(mapLayoutId, mapFragment, "map-fragment-" + mapLayoutId);
		ft.commit();
		mapFragment.getMapAsync(Controller_GoogleMap.this);
	}

	public UiSettings getUiSettings() {
		if (googleMap == null) {
			logError("Need to call showMap() before accessing the map ui settings");
			return null;
		}

		return googleMap.getUiSettings();
	}

	/**
	 * Set the map type
	 *
	 * @param mapType for example {@link GoogleMap#MAP_TYPE_NORMAL}
	 */
	public void setMapType(int mapType) {
		googleMap.setMapType(mapType);
	}

	/**
	 * Set the map style
	 *
	 * @param mapStyle example - setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style_default))
	 *
	 * @see <a href="https://developers.google.com/maps/documentation/android-api/styling#pass_a_json_style_object_to_your_map">Map styling</a>
	 */
	public void setMapStyle(MapStyleOptions mapStyle) {
		try {
			// Customise the styling of the base map using a JSON object defined
			// in a raw resource file.
			boolean success = googleMap.setMapStyle(mapStyle);

			if (!success) {
				logError("Style parsing failed.");
			}
		} catch (Resources.NotFoundException e) {
			logError("Can't find map style. Error: ", e);
		}
	}

	/**
	 * @param locationButtonClickListener supply a click listener for the 'my location' button
	 *
	 * @see <a href="https://developers.google.com/maps/documentation/android-api/location#the_my_location_layer">Adding 'may location' layer</a>
	 */
	@SuppressWarnings("MissingPermission")
	public void showMyLocationIndicatorAndButton(OnMyLocationButtonClickListener locationButtonClickListener) {
		googleMap.setMyLocationEnabled(true);

		if (locationButtonClickListener != null)
			googleMap.setOnMyLocationButtonClickListener(locationButtonClickListener);
	}

	@SuppressWarnings("MissingPermission")
	public void hideMyLocationIndicatorAndButton() {
		googleMap.setMyLocationEnabled(false);
	}

	public void removeMarker(MapMarker marker) {
		markers.remove(marker);
	}

	public void updateRoute() {
		PolylineOptions polyline = new PolylineOptions().width(3).color(Color.RED);
		for (MapMarker marker : markers) {
			polyline.add(marker.position);
		}

		updateRoutesOnMap(polyline);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.googleMap = googleMap;
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		this.googleMap.setOnMapClickListener(this);
		this.googleMap.setOnMapLongClickListener(this);
		this.googleMap.setOnMarkerClickListener(this);
		this.googleMap.setOnInfoWindowClickListener(this);
		dispatchEvent("Map is ready.", OnMapReadyListener.class, new Processor<OnMapReadyListener>() {
			@Override
			public void process(OnMapReadyListener listener) {
				listener.onMapReady(getRootView().getId());
			}
		});
	}

	public void setDefaultCameraZoom(int cameraZoom) {
		this.cameraZoom = cameraZoom;
	}

	public CameraPosition getCameraPosition() {
		return googleMap.getCameraPosition();
	}

	public void setCameraLocation(LatLng position, boolean animate) {
		setCameraLocation(position, cameraZoom, animate);
	}

	public void setCameraLocation(LatLng position, int cameraZoom, boolean animate) {
		if (position == null)
			return;

		CameraUpdate newLocation = CameraUpdateFactory.newLatLngZoom(position, cameraZoom);
		if (animate)
			googleMap.animateCamera(newLocation);
		else
			googleMap.moveCamera(newLocation);
	}

	public Marker addMarker(MapMarker mapMarker) {
		MarkerOptions markerOptions = new MarkerOptions().position(mapMarker.position)
		                                                 .title(mapMarker.title)
		                                                 .icon(mapMarker.iconRes == 0 ? BitmapDescriptorFactory.defaultMarker(mapMarker.color)
		                                                                              : BitmapDescriptorFactory.fromResource(mapMarker.iconRes));
		markers.add(mapMarker);

		return mapMarker.marker = googleMap.addMarker(markerOptions);
	}

	public void addPolyline(PolylineOptions polylineOption) {
		googleMap.addPolyline(polylineOption);
	}

	private void updateRoutesOnMap(PolylineOptions rectLine) {
		if (polyline != null)
			polyline.remove();
		polyline = googleMap.addPolyline(rectLine);
	}

	@Override
	public void onMapClick(final LatLng latLng) {
		dispatchEvent("Map event MAP_CLICK was called.", OnGoogleMapListener.class, new Processor<OnGoogleMapListener>() {
			@Override
			public void process(OnGoogleMapListener listener) {
				listener.onMapClick(getRootView().getId(), latLng);
			}
		});
	}

	@Override
	public void onMapLongClick(final LatLng latLng) {
		dispatchEvent("Map event MAP_LONG_CLICK was called.", OnGoogleMapListener.class, new Processor<OnGoogleMapListener>() {
			@Override
			public void process(OnGoogleMapListener listener) {
				listener.onMapLongClick(getRootView().getId(), latLng);
			}
		});
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		dispatchEvent("Map event MAP_MARKER_CLICK was called.", OnGoogleMapListener.class, new Processor<OnGoogleMapListener>() {
			@Override
			public void process(OnGoogleMapListener listener) {
				listener.onMarkerClick(getRootView().getId(), marker);
			}
		});
		return true;
	}

	@Override
	public void onInfoWindowClick(final Marker marker) {
		dispatchEvent("Map event MAP_INFO_WINDOW_CLICK was called.", OnGoogleMapListener.class, new Processor<OnGoogleMapListener>() {
			@Override
			public void process(OnGoogleMapListener listener) {
				listener.onInfoWindowClick(getRootView().getId(), marker);
			}
		});
	}
}
