// parse JSON Activity -- written by Pratyusha
package com.socprox.socproxnew;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class jsonActivity extends Activity {
	
	private static String url = "http://cjcornell.com/bluegame/REST/users"; // url that contains JSON data
	
	//All the node names in the json data (related to the players) -- as in database(table:user)
	private static final String TAG_USERS = "body"; // title of the json array
	private static final String TAG_ID = "m_iID";
	private static final String TAG_UNAME = "m_strUsername";
	private static final String TAG_MAC_ADDRESS = "m_strMac";
	//private static final String TAG_PASSWORD = "m_strPassword";
	//private static final String TAG_SSID = "m_strSSID";
	//private static final String TAG_FB_EMAIL = "m_strFacebook";
	private static final String TAG_NAME = "m_strName";
	//private static final String TAG_PIC_LOC = "m_strPicLoc";
	//private static final String TAG_PLAY_STATUS = "m_strStatus";
	private static final String TAG_TOTAL_POINTS = "m_iTotalPoints";
	private static final String TAG_LAST_LOC = "m_strLastLoc";
	private static final String TAG_DEVICE_TYPE = "m_strDevice";
	private static final String TAG_GAMES_FIN = "m_iGamesFin";
	
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_dashboard);
		
		JSONArray users = null; // Users JSONArray
		
		// An instance of parseJSON class
		parseJSON jParse = new parseJSON();
		
		// url returns JSON string
		JSONObject json = jParse.getJSONFromUrl(url);
		
		try {
			// get the array of users
			users = json.getJSONArray(TAG_USERS);
			
			// check if the current user has another player to play with
			if(users.getJSONObject(1) != null) { 
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}
}
	
	
	


