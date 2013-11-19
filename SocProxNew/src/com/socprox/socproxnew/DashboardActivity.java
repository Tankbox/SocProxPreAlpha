package com.socprox.socproxnew;


import org.json.JSONException;
import org.json.JSONObject;

import com.socprox.socproxnew.RESTCaller.Website;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	private final static String DEBUG_TAG = "DashboardActivity";
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 100;
	private ProgressDialog mProgressDialog;
	private String username;
    private String password;
    private final static boolean d = true;
    private static String socproxUsername;
	private ArrayAdapter<String> mScannedDevices = null;
	private ArrayAdapter<String> mListUsers = null;
	private ArrayAdapter<String> mNearbyUsers_MAC = null;
	private ArrayAdapter<String> mNearbyUsers_NAME = null;
	private ArrayAdapter<String> mListGames = null;
	private IntentFilter bluetoothReceiverFilter = null;
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	//this method fires when this screen loads
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
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

// -- Joe's code starts here -- //

		// Register the BroadcastReceiver
		bluetoothReceiverFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		bluetoothReceiverFilter.addAction(BluetoothDevice.ACTION_UUID);
		bluetoothReceiverFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		bluetoothReceiverFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, bluetoothReceiverFilter); 	// Don't forget to unregister
																// during onDestroy
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		CheckAndEnableBluetooth();
		if(mBluetoothAdapter.isEnabled())
		{
			mProgressDialog = new ProgressDialog(DashboardActivity.this);
	    	mProgressDialog.setMessage("Finding Players");
	    	mProgressDialog.setIndeterminate(true);
	    	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    	
			InitializeArrayAdapters();
			ScanForPlayers();
			mProgressDialog.dismiss();
		}
			// This code sets the Play button to INVISIBLE and then after 12 seconds
		// (the length of time needed to scan for player) it sets it to VISIBLE
//		findViewById(R.id.playButton).setVisibility(View.INVISIBLE);
//		findViewById(R.id.playButton).postDelayed(new Runnable() {
//			public void run() {
//				findViewById(R.id.playButton).setVisibility(View.VISIBLE);
//			}
//		}, 12000);			
		
		// -- Not quite sure what this does just yet -- //

	}
	
	private void InitializeArrayAdapters() {
		// Initialize ArrayAdapters for comparison
		mScannedDevices = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		mListUsers = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		mNearbyUsers_MAC = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		mNearbyUsers_NAME = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		mListGames = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);		
	}
	
	private void CheckAndEnableBluetooth() {
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not supported on this device.",
					Toast.LENGTH_LONG).show();
			finish();
		}

		// This if-else statement is a request to turn Bluetooth on
		// The body of the 'if' section should go in the on-create after login
		// The else section should go in the onCreate of the "Play" activity.
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Else it is on, so let's make some magic happen
		}
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the address to an array adapter to show in a ListView
				mScannedDevices.add(device.getAddress());
			}
		}
	};
	
	public void ScanForPlayers() {
		// Start discovery of Bluetooth devices
		if (!mBluetoothAdapter.isDiscovering())
			mBluetoothAdapter.startDiscovery();
		
		while(mBluetoothAdapter.isDiscovering()) {
			Log.d(DEBUG_TAG, "Scanning");
		}
		Log.d(DEBUG_TAG, "Finished Scanning for MAC addresses");
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
	
	

}
