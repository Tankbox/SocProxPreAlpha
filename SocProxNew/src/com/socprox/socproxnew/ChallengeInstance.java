package com.socprox.socproxnew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ChallengeInstance {
	public int challengeInstanceId;
	public int challengeId;
	public List<String> userIds;
	public String status;
	public String acceptanceStatus;
	protected Challenge challenge;
	
	public ChallengeInstance(JSONObject challengeInstance, String thisMacAddress)
	{
		userIds = new ArrayList<String>();
		this.InitializeChallengeInstanceFromJson(challengeInstance);
		this.challenge = new Challenge(challengeId, thisMacAddress);
	}
	
	private void InitializeChallengeInstanceFromJson(JSONObject challengeInstance)
	{
		try {
			challengeInstance.get("success");
			JSONObject challengeInstanceBody = challengeInstance.getJSONObject("body");
			this.challengeInstanceId = challengeInstanceBody.getInt("m_iID");
			this.challengeId = challengeInstanceBody.getInt("m_iChallengeID");
			String tempUserIds = challengeInstanceBody.getString("m_strUserIDs");
			if(tempUserIds.contains(";"))
			{
				this.userIds = Arrays.asList(tempUserIds.split(";"));
			}
			else
			{
				this.userIds.add(tempUserIds);
			}
			this.status = challengeInstanceBody.getString("m_strStatus");
			this.acceptanceStatus = challengeInstanceBody.getString("m_strAccepts");
			
			
		} catch (JSONException e) {
			Log.d("ChallengeInstance Constructor", "JSON object not parsed successfully");
			e.printStackTrace();
		}
		
	}
}
