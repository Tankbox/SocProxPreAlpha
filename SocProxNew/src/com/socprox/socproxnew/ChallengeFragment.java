package com.socprox.socproxnew;

import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChallengeFragment extends Fragment {
	public static final String ARG_SECTION_NUMBER = "section_number";
	private JSONObject userStats = new JSONObject();
	private RESTCaller restServiceCaller = new RESTCaller();
	
	public ChallengeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.challenge_fragment,
				container, false);
		return rootView;
	}
}
