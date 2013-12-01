package com.socprox.socproxnew;

import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChallengeFragment extends Fragment {
	private static final int STATS_VIEW = 1;
	private static final int CHALLENGE_VIEW = 2;
	public static final String ARG_SECTION_NUMBER = "section_number";
	private BluetoothAdapter mBluetoothAdapter;
	private JSONObject userStats = new JSONObject();
	private String challengesCompletedValue;
	private String[] strGameNameArray = new String[5];
	private String[] iTotalPointsArray = new String[5];
	private String[] strGameDescriptionArray = new String[5];
	private RESTCaller restServiceCaller = new RESTCaller();
	
	public ChallengeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dashboard_dummy,
				container, false);
//		TextView dummyTextView = (TextView) rootView
//				.findViewById(R.id.section_label);
//		dummyTextView.setText(Integer.toString(getArguments().getInt(
//				ARG_SECTION_NUMBER)));
		return rootView;
	}
}
