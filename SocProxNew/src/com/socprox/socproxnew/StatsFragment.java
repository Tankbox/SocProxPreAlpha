package com.socprox.socproxnew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
		ListView statsList = (ListView) rootView.findViewById(R.id.list);
		List<Map<String, String>> statsMap = new ArrayList<Map<String, String>>();
		
		TextView totalPoints = (TextView) rootView.findViewById(R.id.totalPointsTextView);
		totalPoints.setText("Total Points Earned:\t\t\t" + challengesCompletedValue);
		for (int i = 0; i < strGameNameArray.length; ++i) {
			if (strGameNameArray[i] != null) {
				Map<String, String> oneChallenge = new HashMap<String, String>(2);
				String lineOne = "Description:\t\t\t\t" + strGameDescriptionArray[i] + "\n";
				String lineTwo = "Points Earned:\t\t" + iTotalPointsArray[i];
				oneChallenge.put("name", strGameNameArray[i]);
				oneChallenge.put("description", lineOne + lineTwo);
				statsMap.add(oneChallenge);
			}
		}
		
		SimpleAdapter adapter = new SimpleAdapter(inflater.getContext(), statsMap,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "description"},
                new int[] {android.R.id.text1,
                           android.R.id.text2});			
		
		statsList.setAdapter(adapter);
		return rootView;
	}
	

}
