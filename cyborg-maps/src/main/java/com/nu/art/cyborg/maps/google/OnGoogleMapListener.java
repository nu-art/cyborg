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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public interface OnGoogleMapListener {

	void onMapClick(int controllerId, LatLng latLng);

	void onMapLongClick(int controllerId, LatLng latLng);

	void onMarkerClick(int controllerId, Marker marker);

	void onInfoWindowClick(int controllerId, Marker marker);
}
