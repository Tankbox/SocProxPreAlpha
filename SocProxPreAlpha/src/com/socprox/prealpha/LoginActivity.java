package com.socprox.prealpha;

import org.json.JSONArray;
import org.json.JSONObject;

import com.socprox.prealpha.RESTCaller.Website;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		 
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void loginButtonClicked(View view) {
		
		try
		{
			new Thread(new Runnable() {
			    public void run() {
			    	String username = ((EditText)findViewById(R.id.usernameEditText)).getText().toString().trim();
					String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString().trim();
					
			    	WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					WifiInfo info = manager.getConnectionInfo();
					String address = info.getMacAddress();
					
			    	String call = RESTCaller.loginCall(Website.SOCPROX, address, username, password);
			    	RESTCaller caller = new RESTCaller();
			    	JSONArray result = caller.executeToArray(call);
			    }
			}).start();
			
		} catch(Exception e)
		{
			System.out.print(e);
			// Need to execute username and password for REST call now
		}
	}
	
	public String getMacAddress(Context context) {
	    WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    String macAddress = wimanager.getConnectionInfo().getMacAddress();
	    if (macAddress == null) {
	        macAddress = "Device don't have mac address or wi-fi is disabled";
	    }
	    return macAddress;
	}
}