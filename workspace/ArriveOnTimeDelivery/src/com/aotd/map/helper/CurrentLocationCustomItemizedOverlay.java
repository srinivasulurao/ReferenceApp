/***
 * Copyright (c) 2011 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.aotd.map.helper;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class CurrentLocationCustomItemizedOverlay<Item extends OverlayItem> extends BalloonItemizedOverlay<CurrentLocationCustomOverlayItem> {

	private ArrayList<CurrentLocationCustomOverlayItem> m_overlays = new ArrayList<CurrentLocationCustomOverlayItem>();
	private Context c;
	
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, false);
	}

	public CurrentLocationCustomItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
     	c = mapView.getContext();
	}

	public void addOverlay(CurrentLocationCustomOverlayItem overlay) {
	    m_overlays.add(overlay);	    
	    populate();
	}
	
	public void removeOverlay(){
		m_overlays.clear();		
	}

	@Override
	protected CurrentLocationCustomOverlayItem createItem(int i) {
		return m_overlays.get(i);
	}
	
	
	@Override
	public int size() {
		return m_overlays.size();
	}
	
	
	@Override
	protected BalloonOverlayView<CurrentLocationCustomOverlayItem> createBalloonOverlayView() {
		
		return new CurrentLocationCustomBalloonOverlayView<CurrentLocationCustomOverlayItem>(
				getMapView().getContext(), getBalloonBottomOffset());
	}

}
