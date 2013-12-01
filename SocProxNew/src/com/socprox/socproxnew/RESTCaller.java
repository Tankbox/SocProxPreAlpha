package com.socprox.socproxnew;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import android.os.AsyncTask;


public class RESTCaller {
	public static final String DEBUG_TAG = "RESTCaller";
	public static final boolean debug = true;
	static final String BASEURL = "http://www.cjcornell.com/bluegame/REST/";	
	
	private class RestCallerAsyncTaskJsonObject extends AsyncTask<String, Integer, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... url) {
			String call = url[0];
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(call);
			String result;
			JSONObject jsonResponse = null;
			try {
				if(debug) 
					Log.d(DEBUG_TAG, "REST Call being attempted: " + call);
				HttpResponse response = httpClient.execute(httpGet, localContext);
				HttpEntity entity = response.getEntity();
				result = getASCIIContentFromEntity(entity);
				jsonResponse = new JSONObject(result);
				if(debug) 
					Log.d(DEBUG_TAG, jsonResponse.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(jsonResponse != null)
			{
				return jsonResponse;
			}
			else
			{
				return null;
			}
		}
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
		
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
        }
	}

	private class RestCallerAsyncTaskJsonArray extends AsyncTask<String, Integer, JSONArray> {
		
		@Override
		protected JSONArray doInBackground(String... url) {
			String call = url[0];
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(call);
			String result;
			JSONArray jsonArrayResponse = null;
			try {
				if(debug) 
					Log.d(DEBUG_TAG, "REST Call being attempted: " + call);
				HttpResponse response = httpClient.execute(httpGet, localContext);
				HttpEntity entity = response.getEntity();
				result = getASCIIContentFromEntity(entity);
				jsonArrayResponse = new JSONArray(result);
				if(debug) 
					Log.d(DEBUG_TAG, jsonArrayResponse.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(jsonArrayResponse != null)
			{
				return jsonArrayResponse;
			}
			else
			{
				return null;
			}
		}
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
		
		protected void onPostExecute(JSONArray result) {
			super.onPostExecute(result);
        }
	}
	
	public JSONObject execute(String url) {
		RestCallerAsyncTaskJsonObject asyncCaller = new RestCallerAsyncTaskJsonObject();
		String finalURL = BASEURL + url;
		
		try {
			return asyncCaller.execute(finalURL).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONArray executeToArray(String url) {
		RestCallerAsyncTaskJsonArray asyncCaller = new RestCallerAsyncTaskJsonArray();
		String finalURL = BASEURL + url;

		try {
			return asyncCaller.execute(finalURL).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		// Setup the http variables for the call
//		HttpClient httpClient = new DefaultHttpClient();
//		HttpContext localContext = new BasicHttpContext();
//		HttpGet httpGet = new HttpGet(finalURL);
//		String result;
//		JSONArray jsonArray = null;
//		try {
//			HttpResponse response = httpClient.execute(httpGet, localContext);
//			HttpEntity entity = response.getEntity();
//			result = getASCIIContentFromEntity(entity);
//			jsonArray = new JSONArray(result);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return jsonArray;
	}
	
	protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);
			if (n > 0) out.append(new String(b, 0, n));
		}
		return out.toString();
	}
	
	public static String challengeStatusCall(String mac1, int id) {
		return "challengeStatus/" + mac1 + "/" + id;
	}
	
	public static String getAllGamesCall() {
		return "getAllGames";
	}
	
	public static String getChallengeCall(String mac1, String mac2) {
		return "getChallenge/" + mac1 + "/" + mac2;
	}
	
	public static String getChallengeInstancesCall(String mac, String statuses) {
		return "getChallengeInstances/" + mac + "/" + statuses;
	}
	
	public static String getChallengeInstanceCall(String mac, int challengeId) {
		return "getChallengeInstance/" + mac + "/" + challengeId;
	}
	
	public static String getUsersCall() {
		return "users/";
	}
	
	public static String getStandings(String mac, int limit, String gameName) {
		String result = "getStandings/";
		if (!mac.equals("")) {
			result += "mac/" + mac + "/";
		}
		if (limit != -1) {
			result += "limit/" + limit + "/";
		}
		if (!gameName.equals("")) {
			result += "gamename/" + gameName;
		}
		return result;
	}
	
	public static String listChallengesCall(String mac1, String mac2) {
		return "listChallenges/" + mac1 + "/" + mac2;
	}
	
	public static String loginCall(Website website, String username) {
		switch(website) {
			case FACEBOOK: return "login/facebook/" + username;
			case TWITTER: return "login/twitter/" + username;
			default: return "";
		
		}
	}
	
	public String loginCall(Website website, String macAddress, String username, String password) {
		switch(website) {
			case FACEBOOK: return "login/" + macAddress + "/facebook/" + username;
			case TWITTER: return "login/" + macAddress + "/twitter/" + macAddress + username;
			case SOCPROX: return "login/" + macAddress + "/socprox/" + username + "/" + password;
			default: return "";
		
		}
	}
	
	public static String registerCall(String macAddress, String username, String password) {
		
		return "register/" + macAddress + "/" + username + "/" + password;
	}
	
	public static String registerAliasCall(Website website, String username, String socproxUsername) {
		switch(website) {
			case FACEBOOK: return "registerAlias/facebook/" + username + "/" + socproxUsername;
			case TWITTER: return "registerAlias/twitter/" + username + "/" + socproxUsername;
			case SOCPROX: return "registerAlias/soxprox/" + username + "/" + socproxUsername;
			default: return "";
		}
	}
	
	public static String updateProfile(String socproxUsername, String firstName, String lastName, String email) {
		String firstPart = "updateProfile/" + socproxUsername + "/";
		String secondPart = "/";
		boolean mark = false;
		if(!firstName.equals("") && !lastName.equals("")) {
			firstPart += "Name";
			secondPart += firstName + "%20" + lastName;
			mark=true;
		}
		if(!email.equals("")) {
			if(mark) {
				firstPart += ",";
				secondPart += ",";
			}
			firstPart += "Facebook_Email";
			secondPart += email;
			mark = true;
		}
		
		if(!mark) {
			return "";
		}
		return (firstPart + secondPart);
	}
	
	public static String updateChallengeCall(String macID, int id, String input) {
		return "updateChallenge/" + macID + "/" + id + "/" + input;
	}
	
	public static String userStatsCall(String macID) {
		return "userStats/" + macID;
	}
	
	public enum Website {
		FACEBOOK, TWITTER, SOCPROX
	}
}
