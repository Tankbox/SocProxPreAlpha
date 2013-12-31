package com.socprox.socproxnew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Challenge {
	public int challengeId;
	public String internalName;
	public String internalDescription;
	public String name;
	public String instructions;
	public int categoryId;
	public int gameId;
	public int verificationId;
	public String description;

	public Challenge(JSONObject challenge) {
		this.InitializeChallengeFromJson(challenge);
	}

	public Challenge(int challengeId, String thisMacAddress) {
		RESTCaller listChallengesRequest = new RESTCaller();
		String restUrl = RESTCaller.listChallengesCall(thisMacAddress);
		JSONObject listChallengesResponse = listChallengesRequest
				.execute(restUrl);
		try {
			JSONArray arrayOfChallenges = listChallengesResponse
					.getJSONArray("body");
			for (int i = 0; i < arrayOfChallenges.length(); i++) {
				JSONObject challenge = arrayOfChallenges.getJSONObject(i);
				if (challengeId == challenge.getInt("m_iID")) {
					this.InitializeChallengeFromJson(challenge);
					break;
				}
			}

		} catch (JSONException e) {
			Log.d("ChallengeInstance Constructor",
					"JSON object not parsed successfully");
			e.printStackTrace();
		}
	}

	private void InitializeChallengeFromJson(JSONObject challenge) {
		try {
			this.challengeId = challenge.getInt("m_iID");
			this.internalName = challenge.getString("m_strIntName");
			this.internalDescription = challenge.getString("m_strIntDesc");
			this.name = challenge.getString("m_strName");
			this.instructions = challenge.getString("m_strInstr");
			this.categoryId = challenge.getInt("m_iCatID");
			this.gameId = challenge.getInt("m_iGameID");
			this.verificationId = challenge.getInt("m_iVerificationID");
			this.description = challenge.getString("m_strDesc");
		} catch (JSONException e) {
			Log.d("ChallengeInstance SetInstanceVariablesFromJson",
					"JSON object not parsed successfully");
			e.printStackTrace();
		}
	}
}
