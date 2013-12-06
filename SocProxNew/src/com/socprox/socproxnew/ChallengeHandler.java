package com.socprox.socproxnew;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class ChallengeHandler {

	private RESTCaller restServiceCaller = new RESTCaller();
	public ArrayList<ChallengeInstance> challengeInstances;
	
	public ChallengeHandler(){
		challengeInstances = new ArrayList<ChallengeInstance>();
	}
	
	public JSONObject GetChallengeInstance(String MAC, int challengeId) {
		JSONObject challenge = null;
		
		String call = RESTCaller.getChallengeInstanceCall(MAC, challengeId);
		challenge = restServiceCaller.execute(call);
		
		if (challenge == null)
			return null;
		else
			return challenge;
	}

	public JSONObject ChallengeAccepted(String MAC, JSONObject challengeInstance) {
		JSONObject updatedChallenge = null;
		
		try {
			String call = RESTCaller.updateChallengeCall(MAC,
					challengeInstance.getInt("m_iID"),
					"accepted");
			updatedChallenge = restServiceCaller.execute(call);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (updatedChallenge == null)
			return null;
		else
			return updatedChallenge;
	}
	
	public JSONObject ChallengeDenied(String MAC, JSONObject challengeInstance) {
		JSONObject updatedChallenge = null;
		
		try {
			String call = RESTCaller.updateChallengeCall(MAC,
					Integer.parseInt(challengeInstance.getString("m_iID")),
					"denied");
			updatedChallenge = restServiceCaller.execute(call);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (updatedChallenge == null)
			return null;
		else
			return updatedChallenge;
	}
	
	public JSONObject GetPendingChallenges(String MAC) {
		JSONObject pendingChallenges = null;
		
		String call = RESTCaller.getChallengeInstancesCall(MAC, "pending");
		pendingChallenges = restServiceCaller.execute(call);
		
		if (pendingChallenges == null)
			return null;
		else
			return pendingChallenges;
	}
}




