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
        
        private final static String STATUS_ACCEPTED = "accepted";
        private final static String STATUS_DENIED = "denied";
    
        JSONObject challenges;
        JSONObject updateChallenge; 
        private String challengeStatus;
         
        String macAdd1;
        String macAdd2;
        String challengeUrl = "http://cjcornell.com/bluegame/REST/getChallenge";
        StringBuilder cUrlBuilder = new StringBuilder();

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

                JSONArray mValidPlayers = null;
                String serializedPlayerJsonArray = getArguments().getString("validPlayers");
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
                
                try { 
                        macAdd1 = mBluetoothAdapter.getAddress(); // mac address of this user
                        // check if the current user has another player to play with
                        if (!mValidPlayers.isNull(0)) {
                                macAdd2 = mValidPlayers.getJSONObject(0).getString(
                                                TAG_MAC_ADDRESS); // mac address of the player who is
                                                                                        // also playing this challenge
                                String challengeRestCall = RESTCaller.getChallengeCall(macAdd1, macAdd2);
                                challenges = restServiceCaller.execute(challengeRestCall);

                                // get challenge information to display on screen
                                TextView challengeName = (TextView)rootView.findViewById(R.id.challengeName);
                                challengeName.setText(challenges.getString("m_strIntName"));

                                TextView challengeDesc = (TextView) rootView.findViewById(R.id.challengeDesc);
                                challengeDesc.setText(challenges.getString("m_strIntDesc"));

                                final Button acceptButton = (Button) rootView.findViewById(R.id.btn_accept);
                                acceptButton.setText("Accept");

                                final String challengeId = challenges.getString("m_iID");

                                acceptButton.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                                // TODO Auto-generated method stub
                                                // pushChallenge updates the status of the challenge in
                                                // the database
                                                challengeStatus = STATUS_ACCEPTED;
                                                acceptButton.setVisibility(Button.GONE);
                                                for (int i = 0; i < 2; i++) {
                                                        if (i == 0) {
                                                                updateUrlBuilder.append(updateChallengeUrl);
                                                                updateUrlBuilder.append('/' + macAdd1 + '/'
                                                                                + challengeId + challengeStatus);
                                                                //updateChallenge = executeUpdateChallengeREST(updateChallengeUrl);
                                                        } else if (i == 1) {
                                                                updateUrlBuilder.append(updateChallengeUrl);
                                                                updateUrlBuilder.append('/' + macAdd2 + '/'
                                                                                + challengeId + challengeStatus);
                                                                //updateChallenge = executeUpdateChallengeREST(updateChallengeUrl);
                                                        }
                                                }
                                        }

                                });

                                Button denyButton = (Button) rootView.findViewById(R.id.btn_deny);
                                denyButton.setText("Deny");

                                denyButton.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                                // TODO Auto-generated method stub
                                                // pushChallenge updates the status of the challenge in
                                                // the database
                                                challengeStatus = STATUS_DENIED;

                                                for (int i = 0; i < 2; i++) {
                                                        if (i == 0) {
                                                                updateUrlBuilder.append(updateChallengeUrl);
                                                                updateUrlBuilder.append('/' + macAdd1 + '/'
                                                                                + challengeId + challengeStatus);
                                                                //updateChallenge = executeUpdateChallengeREST(updateChallengeUrl);
                                                        } else if (i == 1) {
                                                                updateUrlBuilder.append(updateChallengeUrl);
                                                                updateUrlBuilder.append('/' + macAdd2 + '/'
                                                                                + challengeId + challengeStatus);
                                                                //updateChallenge = executeUpdateChallengeREST(updateChallengeUrl);
                                                        }
                                                }
                                        }

                                });
                        }
                } catch (JSONException e) {
                        e.printStackTrace();
                }
                return rootView;
        }
}