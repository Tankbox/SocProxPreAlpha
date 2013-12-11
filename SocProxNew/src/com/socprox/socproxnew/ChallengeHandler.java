package com.socprox.socproxnew;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChallengeHandler {

	private RESTCaller restServiceCaller = new RESTCaller();
	public ArrayList<ChallengeInstance> challengeInstances;

	public ChallengeHandler() {
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

	public JSONObject ChallengeAccepted(String MAC, int challengeInstanceId) {
		JSONObject updatedChallenge = null;

		String call = RESTCaller.updateChallengeCall(MAC,
				challengeInstanceId, "accepted");
		updatedChallenge = restServiceCaller.execute(call);

		if (updatedChallenge == null)
			return null;
		else
			return updatedChallenge;
	}

	public JSONObject ChallengeDenied(String MAC, int challengeInstanceId) {
		JSONObject updatedChallenge = null;

		String call = RESTCaller.updateChallengeCall(MAC,
				challengeInstanceId, "denied");
		updatedChallenge = restServiceCaller.execute(call);

		if (updatedChallenge == null)
			return null;
		else
			return updatedChallenge;
	}

	private JSONObject GetPendingChallengeInstances(String MAC) {
		JSONObject pendingChallenges = null;

		String call = RESTCaller.getChallengeInstancesCall(MAC, "pending");
		pendingChallenges = restServiceCaller.execute(call);

		if (pendingChallenges == null)
			return null;
		else
			return pendingChallenges;
	}

	public ChallengeInstance GetMostRecentChallengeInstance(String MAC) {
		JSONObject pendingChallenges = null;

		pendingChallenges = GetPendingChallengeInstances(MAC);
		ChallengeInstance mostRecentChallengeInstanceObject = null;
		try {
			if (!pendingChallenges.getBoolean("success"))
				return null;
			
			JSONObject responseCallParsedBody = pendingChallenges
					.getJSONObject("body");
			JSONArray responseCallGamesArray = responseCallParsedBody
					.getJSONArray("Games");
			JSONObject mostRecentChallengeInstance = FindMostRecentChallengeInstance(responseCallGamesArray);

			mostRecentChallengeInstanceObject = new ChallengeInstance(
					mostRecentChallengeInstance, MAC);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mostRecentChallengeInstanceObject;
	}

	private JSONObject FindMostRecentChallengeInstance(JSONArray games)
			throws JSONException {
		int largestChallengeId = -1;
		JSONObject mostRecentChallenge = new JSONObject();
		for (int i = 0; i < games.length(); i++) {
			JSONObject game = games.getJSONObject(i);
			JSONArray challengesInGame = game.getJSONArray("challenges");
			JSONObject challenge = challengesInGame
					.getJSONObject(challengesInGame.length() - 1);
			int challengeId = challenge.getInt("m_iID");
			if (challengeId > largestChallengeId) {
				largestChallengeId = challengeId;
				mostRecentChallenge = challenge;
			}
		}
		return mostRecentChallenge;
	}

	public boolean CreateChallengeInstance(String myMac, String otherPlayerMac) {
		String createChallengeInstanceRestCall = RESTCaller.getChallengeCall(myMac, otherPlayerMac);		
		try {
			return restServiceCaller.execute(createChallengeInstanceRestCall).getBoolean("success");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
