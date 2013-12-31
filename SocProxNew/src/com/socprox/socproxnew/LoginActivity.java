package com.socprox.socproxnew;

import org.json.JSONException;
import org.json.JSONObject;

//import com.socialproximity.socprox.RESTCaller;
//import com.socialproximity.socprox.User;
//import com.socialproximity.socprox.LoginActivity;
import com.socprox.socproxnew.RESTCaller.Website;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/*This is the first screen after app launches which consists of the login interface*/
public class LoginActivity extends Activity {
	private final static String DEBUG_TAG = "LoginActivity";
	private String userName, password;
	private final static boolean d = true;
	private BluetoothAdapter mBluetoothAdapter;
	private static String socproxUsername;
	private ProgressDialog mProgressDialog;
	private RESTCaller restServiceCaller = new RESTCaller();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login); // load the login.xml

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// hide action bar for the login screen
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		mProgressDialog = new ProgressDialog(LoginActivity.this);
		mProgressDialog.setMessage("Logging In");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if (CheckLoggedInUser()) {
			startActivity(new Intent(LoginActivity.this,
					DashboardActivity.class));
			finish();
		}
	}

	private Boolean CheckLoggedInUser() {
		if (SaveSharedPreference.getUserName(getApplicationContext()).isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public void onLoginButtonClicked(View v) {
		userName = ((EditText) findViewById(R.id.user_name)).getText()
				.toString().trim();
		password = ((EditText) findViewById(R.id.password)).getText()
				.toString().trim();

		JSONObject restCallResponse = restServiceCaller
				.execute(restServiceCaller.loginCall(Website.SOCPROX,
						mBluetoothAdapter.getAddress(), userName, password));
		boolean error = false;
		try {
			JSONObject restBody = restCallResponse.getJSONObject("body");
			socproxUsername = restBody.getString("m_strUsername");

			// Login successful
			JSONObject userJsonObject = new JSONObject();
			userJsonObject.put("username", socproxUsername);
			userJsonObject.put("userMac", restBody.getString("m_strMac"));
			userJsonObject.put("realName", restBody.getString("m_strName"));
			userJsonObject.put("email", restBody.getString("m_strFacebook"));
			User.getInstance(userJsonObject, LoginActivity.this);
		} catch (JSONException ex) {
			// If there is no m_strUsername field then there was an error (user
			// not in database).
			error = true;
			ex.printStackTrace();
			if (d) {
				Log.d(DEBUG_TAG, "Error on REST return.");
			}
		}

		if (error) {
			mProgressDialog.dismiss();
			try {
				// Login error
				String errorMessage = restCallResponse.getString("message");
				Toast.makeText(getBaseContext(), errorMessage,
						Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				e.printStackTrace();
				if (d) {
					Log.d(DEBUG_TAG, "No error specified by REST.");
				}
			}
		}

		if (d) {
			Log.d(DEBUG_TAG, "Boolean error = " + error);
		}

		if (!error) {
			SaveSharedPreference.setUserName(getApplicationContext(), userName);
			startActivity(new Intent(LoginActivity.this,
					DashboardActivity.class));
		} else {
			Toast.makeText(LoginActivity.this, "Login failed!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void onSignupButtonClicked(View v) {
		this.startActivity(new Intent(LoginActivity.this, SignupActivity.class));
	}
}
