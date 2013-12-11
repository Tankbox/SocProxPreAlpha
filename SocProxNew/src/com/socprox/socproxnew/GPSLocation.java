package com.socprox.socproxnew;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class GPSLocation {

	public static double[] getLocation(Context context) {
		double latitude = 0, longitude = 0;
		double[] lastLocation = new double[2];

		// Get the location manager
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(bestProvider);
		try {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		} catch (NullPointerException e) {
			latitude = -1.0;
			longitude = -1.0;
		}
		lastLocation[0] = latitude;
		lastLocation[1] = longitude;
		return lastLocation;
	}
}
