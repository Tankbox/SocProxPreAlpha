package com.socprox.socproxnew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ChallengeFragment extends Fragment {
	private static final String DEBUG_TAG = "ChallengeFragment";

	public static final String ARG_SECTION_NUMBER = "section_number";
	private RESTCaller restServiceCaller = new RESTCaller();
    private static final String TAG_MAC_ADDRESS = "m_strMac";
    private BluetoothAdapter mBluetoothAdapter;
	
	JSONObject challenges;
	JSONObject updateChallenge; 
	String myMacAddress;
	String macAdd2;
	String challengeUrl = "http://cjcornell.com/bluegame/REST/getChallenge";
	StringBuilder cUrlBuilder = new StringBuilder();
	ChallengeInstance mostRecentChallengeInstance = null;
	String updateChallengeUrl = "http://cjcornell.com/bluegame/REST/updateChallenge/";
	StringBuilder updateUrlBuilder = new StringBuilder();
     
	public ChallengeFragment() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.challenge_fragment,
				container, false);
		
		final Button acceptButton = (Button) rootView.findViewById(R.id.btn_accept);
		final Button denyButton = (Button) rootView.findViewById(R.id.btn_deny);
		acceptButton.setText("Accept");
		denyButton.setText("Deny");
		
		JSONArray mValidPlayers = null;
		String serializedPlayerJsonArray = getArguments().getString("validPlayers");
		mostRecentChallengeInstance = (ChallengeInstance) getArguments().getSerializable("mostRecentChallengeInstance");
		if(!serializedPlayerJsonArray.isEmpty())
		{
			try {
				mValidPlayers = new JSONArray(serializedPlayerJsonArray);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				Log.d(DEBUG_TAG, "Error trying to parse JSON from valid players");
				e1.printStackTrace();
			}
		}
		
		myMacAddress = mBluetoothAdapter.getAddress();
		
		TextView challengeName = (TextView)rootView.findViewById(R.id.challengeName);
		TextView challengeDesc = (TextView) rootView.findViewById(R.id.challengeDesc);
		
		if (mostRecentChallengeInstance != null) {
			challengeName.setText(mostRecentChallengeInstance.challenge.name);
			challengeDesc.setText(mostRecentChallengeInstance.challenge.description);
			denyButton.setVisibility(Button.VISIBLE);
			acceptButton.setVisibility(Button.VISIBLE);
		}
		else {
			challengeName.setText("There are no challenges");
			challengeDesc.setText("But SocProx is scanning for players right now!");	
			denyButton.setVisibility(Button.INVISIBLE);
			acceptButton.setVisibility(Button.INVISIBLE);
		}

		denyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// pushChallenge updates the status of the challenge in
				// the database
				
				ChallengeHandler challengeHandler = new ChallengeHandler();
				challengeHandler.ChallengeDenied(myMacAddress,  mostRecentChallengeInstance.challengeInstanceId);
				denyButton.setVisibility(Button.GONE);
				acceptButton.setVisibility(Button.GONE);
			}
		});
		
		acceptButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// pushChallenge updates the status of the challenge in
				// the database
				
				ChallengeHandler challengeHandler = new ChallengeHandler();
				challengeHandler.ChallengeAccepted(myMacAddress, mostRecentChallengeInstance.challengeInstanceId);
				acceptButton.setVisibility(Button.GONE);
				denyButton.setVisibility(Button.GONE);
			}

		});
		return rootView;
	}
}
