package com.socprox.socproxnew;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StatsFragment extends Fragment {
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
		View rootView = inflater.inflate(R.layout.stats_fragment,
				container, false);
		ArrayList<String> statsArrayList = new ArrayList<String>();
		ListView statsList = (ListView) rootView.findViewById(R.id.list);
		
		statsArrayList.add(challengesCompletedValue);
		try {
			if (userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").length() >= 1) {
				statsArrayList.add(strGameNameArray[0]);
				statsArrayList.add(iTotalPointsArray[0]);
				statsArrayList.add(strGameDescriptionArray[0]);
				
			}
			if (userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").length() >= 2) {		
				statsArrayList.add(strGameNameArray[1]);
				statsArrayList.add(iTotalPointsArray[1]);
				statsArrayList.add(strGameDescriptionArray[1]);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		
		ArrayAdapter<String> statsArrayAdapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, statsArrayList);
		statsList.setAdapter(statsArrayAdapter);
		return rootView;
	}
	

}
