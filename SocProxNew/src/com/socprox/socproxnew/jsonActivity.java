// parse JSON Activity -- written by Pratyusha
package com.socprox.socproxnew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;

public class jsonActivity extends Activity {

	private static String url = "http://cjcornell.com/bluegame/REST/users";

	// All the node names in the json data (related to the players) -- as in
	// database(table:user)
	private static final String TAG_USERS = "body"; // title of the json array

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_dashboard);

		JSONArray users = null; // Users JSONArray

		// An instance of parseJSON class
		parseJSON jParse = new parseJSON();

		// url returns JSON string
		JSONObject json = jParse.getJSONFromUrl(url);

		try {
			// get the array of users
			users = json.getJSONArray(TAG_USERS);

			// check if the current user has another player to play with
			if (users.getJSONObject(1) != null) {

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
