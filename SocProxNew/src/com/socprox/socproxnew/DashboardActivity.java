package com.socprox.socproxnew;


import org.json.JSONException;
import org.json.JSONObject;


import com.socprox.socproxnew.RESTCaller.Website;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	private final static String DEBUG_TAG = "LoginActivity";
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 100;
	private ProgressDialog mProgressDialog;
	private String username;
    private String password;
    private final static boolean d = true;
    private static String socproxUsername;
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	//this method fires when this screen loads
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.challenge_section),
								getString(R.string.stats_section), }), this);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Button loginButton = (Button) findViewById(R.id.btn_login);
		
		// if device does not support Bluetooth
		if(mBluetoothAdapter == null) {
			Toast.makeText(this,
				"This device does not support Bluetooth.",
				Toast.LENGTH_LONG).show();
			Log.d(DEBUG_TAG, "Device does not support Bluetooth.");
			loginButton.setEnabled(false);
		} else {
			if(!mBluetoothAdapter.isEnabled()) {
				// prompt user to enable bluetooth
			    boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
                if(firstrun){
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    
                    // Save the state
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .commit();
                }    
			}
		}
		mProgressDialog = new ProgressDialog(DashboardActivity.this);
    	mProgressDialog.setMessage("Logging In");
    	mProgressDialog.setIndeterminate(true);
    	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
	
	//Button listener for when you click the login button
    public void onLoginButtonClicked(View v){
    	username = ((EditText)findViewById(R.id.user_name)).getText().toString().trim();
    	password = ((EditText)findViewById(R.id.password)).getText().toString().trim();

    	// execute this when the login must be fired
    	LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
    	loginAsyncTask.execute();
    }

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		return true;
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_dashboard_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
	
	private boolean executeREST(String call) {
		RESTCaller caller = new RESTCaller();
		JSONObject jsonObject = caller.execute(call);
		boolean error = false;
      
		try {
			JSONObject jsonObjectBody = jsonObject.getJSONObject("body");
			socproxUsername = jsonObjectBody.getString("m_strUsername");
			
			//Login successful
			JSONObject jObj = new JSONObject();
			jObj.put("username", socproxUsername);
			jObj.put("userMac", jsonObjectBody.getString("m_strMac"));
			jObj.put("realName", jsonObjectBody.getString("m_strName"));
			jObj.put("email", jsonObjectBody.getString("m_strFacebook"));
			User.getInstance(jObj, DashboardActivity.this);
		} catch (JSONException ex) {
			//If there is no m_strUsername field then there was an error (user not in database).
			error=true;
			ex.printStackTrace();
			if(d) {
				Log.d(DEBUG_TAG, "Error on REST return.");
			}
		}
      
        if(error) {
        	try {
	        	//Login error
	          	String errorMessage = jsonObject.getString("message");
	          	Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_LONG).show();
	        } catch (JSONException e) {
	    		e.printStackTrace();
	    		if(d) {
					Log.d(DEBUG_TAG, "No error specified by REST.");
				}
	    	}
        }
        
        if(d) {
			Log.d(DEBUG_TAG, "Boolean error = " + error);
		}
        return !error;
  	}
	
	private class LoginAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... sUrl) {
        	boolean result = false;
            try {
            	String call = RESTCaller.loginCall(Website.SOCPROX, mBluetoothAdapter.getAddress(), username, password);
            	result = executeREST(call);
            } catch (Exception e) {
            	if(d) {
    				Log.d(DEBUG_TAG, "Error on REST execution.");
    			}
            }
            return result;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }
        
//        @Override
//        protected void onPostExecute(Boolean result) {
//            super.onPostExecute(result);
//            mProgressDialog.dismiss();
//            if(result){
//            	Intent intent = new Intent(DashboardActivity.this, GameActivity.class);
//            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            	startActivity(intent);
//            }
//            else{
//            	Toast.makeText(DashboardActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

}
