package com.socprox.socproxnew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ChallengeInstance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2649953741916013393L;
	public int challengeInstanceId;
	public int challengeId;
	public List<String> userIds;
	public List<String> opponentsUsernames;
	public String status;
	public String acceptanceStatus;
	protected Challenge challenge;
	public Date challengeTimestamp;
	private SimpleDateFormat mysqlDateTimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private final static long THREE_MINUTES = 180000;

	public ChallengeInstance(JSONObject challengeInstance, String thisMacAddress) {
		userIds = new ArrayList<String>();
		opponentsUsernames = new ArrayList<String>();
		this.InitializeChallengeInstanceFromJson(challengeInstance);
		
		try {
			this.challenge = new Challenge(
					challengeInstance.getJSONObject("m_oChallenge"));
		} catch (JSONException e) {
			this.challenge = new Challenge(challengeId, thisMacAddress);
		}
	}

	private void InitializeChallengeInstanceFromJson(
			JSONObject challengeInstance) {
		try {
			// challengeInstance.get("success");
			// JSONObject challengeInstanceBody =
			// challengeInstance.getJSONObject("body");
			this.challengeInstanceId = Integer.parseInt(challengeInstance
					.getString("m_iID"));
			this.challengeId = Integer.parseInt(challengeInstance
					.getString("m_iChallengeID"));
			String tempUserIds = challengeInstance.getString("m_strUserIDs");
			if (tempUserIds.contains(";")) {
				this.userIds = Arrays.asList(tempUserIds.split(";"));
			} else {
				this.userIds.add(tempUserIds);
			}
			JSONArray opponentsArray = challengeInstance
					.getJSONArray("m_aOpponents");
			for (int i = 0; i < opponentsArray.length(); i++) {
				this.opponentsUsernames.add(opponentsArray.getString(i));
			}
			this.status = challengeInstance.getString("m_strStatus");
			this.acceptanceStatus = challengeInstance.getString("m_strAccepts");
			this.challengeTimestamp = mysqlDateTimeFormatter
					.parse(challengeInstance.getString("m_oDate"));

		} catch (JSONException e) {
			Log.d("ChallengeInstance Constructor",
					"JSON object not parsed successfully");
			e.printStackTrace();
		} catch (ParseException e) {
			Log.d("ChallengeInstance Constructor",
					"Date object was not parsed successfully");
			e.printStackTrace();
		}
	}

	public boolean ChallengeInstanceHasExpired() {
		Date currentDateAndTime = new Date();

		long currentMilliseconds = currentDateAndTime.getTime();
		long challengeInstanceMilliseconds = this.challengeTimestamp.getTime();
		long difference = currentMilliseconds - challengeInstanceMilliseconds;

		if (difference > THREE_MINUTES)
			return true;

		return false;
	}
	
	public boolean UserNeedsToAcceptOrDenyChallengeInstance()
	{
		return false;
		
	}
	
}
