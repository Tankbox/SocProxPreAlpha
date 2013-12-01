package com.socprox.socproxnew;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatsFragment extends Fragment {
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

	public StatsFragment() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		String call = RESTCaller.userStatsCall(mBluetoothAdapter.getAddress());
		userStats = restServiceCaller.execute(call);
		
		
		Populate();
	}
	
	private void Populate() {
		try {
			challengesCompletedValue = userStats.getJSONObject("body").getString("m_iChallengesCompleted");
			for (int i = 0; i < userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").length(); ++i) {
				strGameNameArray[i] = userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").getJSONObject(i).getString("m_strGameName");
				iTotalPointsArray[i] = userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").getJSONObject(i).getString("m_iTotalPoints");
				strGameDescriptionArray[i] = userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").getJSONObject(i).getString("m_strGameDescription");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dashboard_dummy,
				container, false);
		
		TextView iChallengeCompleted = (TextView) rootView.findViewById(R.id.iChallengesCompleted);
		TextView iTotalPoints = (TextView) rootView.findViewById(R.id.iTotalPoints);
		TextView strGameDescription = (TextView) rootView.findViewById(R.id.strGameDescription);
		TextView iTotalPoints2 = (TextView) rootView.findViewById(R.id.iTotalPoints2);
		TextView strGameDescription2 = (TextView) rootView.findViewById(R.id.strGameDescription2);
		TextView iChallengeCompletedValue = (TextView) rootView.findViewById(R.id.iChallengesCompletedValue);
		TextView strGameName = (TextView) rootView.findViewById(R.id.strGameName);
		TextView iTotalPointsValue = (TextView) rootView.findViewById(R.id.iTotalPointsValue);
		TextView strGameDescriptionValue = (TextView) rootView.findViewById(R.id.strGameDescriptionValue);
		TextView strGameName2 = (TextView) rootView.findViewById(R.id.strGameName2);
		TextView iTotalPointsValue2 = (TextView) rootView.findViewById(R.id.iTotalPointsValue2);
		TextView strGameDescriptionValue2 = (TextView) rootView.findViewById(R.id.strGameDescriptionValue2);
					
		iChallengeCompletedValue.setText(challengesCompletedValue);
		try {
			if (userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").length() >= 1) {
				strGameName.setText(strGameNameArray[0]);
				iTotalPointsValue.setText(iTotalPointsArray[0]);
				strGameDescriptionValue.setText(strGameDescriptionArray[0]);
			}
			if (userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").length() >= 2) {
				strGameName2.setText(strGameNameArray[1]);
				iTotalPointsValue2.setText(iTotalPointsArray[1]);
				strGameDescriptionValue2.setText(strGameDescriptionArray[1]);			
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		
//		TextView dummyEditText = (TextView) rootView
//				.findViewById(R.id.section_label);
//		dummyEditText.setText(Integer.toString(getArguments().getInt(
//				ARG_SECTION_NUMBER)));
		return rootView;
	}
	

}
