// parse JSON Activity -- written by Pratyusha
package com.socprox.socproxnew;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class jsonActivity extends ListActivity {
	
	private static String url = " "; // url that contains JSON data
	
	//All the node names in the json data (related to the players) -- as in database(table:user)
	private static final String TAG_USERS = "users"; // title of the json array
	private static final String TAG_ID = "UserId";
	private static final String TAG_UNAME = "Username";
	private static final String TAG_MAC_ADDRESS = "MAC_Address";
	private static final String TAG_PASSWORD = "Password";
	private static final String TAG_SSID = "SSID";
	private static final String TAG_FB_EMAIL = "Facebook_Email";
	private static final String TAG_NAME = "Name";
	private static final String TAG_PIC_LOC = "Picture_Location";
	private static final String TAG_PLAY_STATUS = "Playing_Status";
	private static final String TAG_TOTAL_POINTS = "Total_Points";
	private static final String TAG_LAST_LOC = "Last_Location";
	private static final String TAG_DEVICE_TYPE = "Device_Type";
	private static final String TAG_GAMES_FIN = "Games_Finished";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_dashboard);
		
		// ArrayList for LlistView
		ArrayList<HashMap <String, String>> userList = new ArrayList<HashMap <String, String>>();
		
		JSONArray users = null; // Users JSONArray
		
		// An instance of parseJSON class
		parseJSON jParse = new parseJSON();
		
		// url returns JSON string
		JSONObject json = jParse.getJSONFromUrl(url);
		
		try {
			// get the array of users
			users = json.getJSONArray(TAG_USERS);
			
			// Loop through the array of users
			for(int i =0; i < users.length(); i++) {
				JSONObject usersObj = users.getJSONObject(i);
				
				// get items in users array and store them in appropriate variable
				String id = usersObj.getString(TAG_ID);
				String username = usersObj.getString(TAG_UNAME);
				String macAddress = usersObj.getString(TAG_MAC_ADDRESS);
				String password = usersObj.getString(TAG_PASSWORD);
				String ssid = usersObj.getString(TAG_SSID);
				String fbEmail = usersObj.getString(TAG_FB_EMAIL);
				String name = usersObj.getString(TAG_NAME);
				String picLoc = usersObj.getString(TAG_PIC_LOC);
				String playStatus = usersObj.getString(TAG_PLAY_STATUS);
				String totalPoints = usersObj.getString(TAG_TOTAL_POINTS);
				String lastLoc = usersObj.getString(TAG_LAST_LOC);
				String devType = usersObj.getString(TAG_DEVICE_TYPE);
				String gamesFin = usersObj.getString(TAG_GAMES_FIN);
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				map.put(TAG_ID, id); // may not need
				map.put(TAG_UNAME, username);
				map.put(TAG_MAC_ADDRESS, macAddress); // may not need to display
				//map.put(TAG_PASSWORD, password); not needed 
				map.put(TAG_SSID, ssid); // may not need
				map.put(TAG_FB_EMAIL, fbEmail); // may not need
				map.put(TAG_NAME, name);
				map.put(TAG_PIC_LOC, picLoc);
				map.put(TAG_PLAY_STATUS, playStatus);
				map.put(TAG_TOTAL_POINTS, totalPoints);
				map.put(TAG_LAST_LOC, lastLoc); // may not need
				map.put(TAG_DEVICE_TYPE, devType); // may not need
				map.put(TAG_GAMES_FIN, gamesFin);
				
				userList.add(map);
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//ListAdapter adapter = new SimpleAdapter(this, userList, );
		
	}
}
	
	
	


