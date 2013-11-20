package com.socprox.socproxnew;


import java.io.Console;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	private final static String DEBUG_TAG = "LoginActivity";
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 100;
	private ProgressDialog mProgressDialog;
    private final static boolean d = true;
    private static String socproxUsername;
	private ArrayAdapter<String> mScannedDevices = null;
	private IntentFilter bluetoothReceiverFilter = null;
	private JSONArray mUsersFromServer = null;
	private JSONArray mValidPlayers = null;
	
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	static AsyncTask<String, Integer, JSONArray> dashboardRestCaller;
	static AsyncTask<Void, Void, JSONArray> dashboardBluetoothHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_dashboard);
				
		dashboardRestCaller = new DashboardAsyncRestCaller();
		dashboardBluetoothHandler = new DashboardAsyncBluetoothHandler();
		
		InitializeActionBar();
		InitializeBluetoothRecieverFilters();
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		CheckAndEnableBluetooth();
		InitializeProgressSpinner();

		if(mBluetoothAdapter.isEnabled())
		{
			InitializeArrayAdapters();			
			dashboardRestCaller.execute("users");
			dashboardBluetoothHandler.execute();
			
			try {
				mValidPlayers = dashboardRestCaller.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mValidPlayers = dashboardBluetoothHandler.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Save the MAC addresses into an ArrayAdapter for comparison
		for (int i = 0; i < mScannedDevices.getCount(); ++i) {
			for (int j = 0; j < mUsersFromServer.length(); ++j) {
				try {
					if (mUsersFromServer.getJSONObject(i)
							.get("m_strMac").toString() == mScannedDevices
							.getItem(j)) {
						mValidPlayers.put(mUsersFromServer.getJSONObject(i));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void onLogoutButtonClicked(View v){
    	SaveSharedPreference.setUserName(getApplicationContext(), "");
    	Intent home = new Intent(DashboardActivity.this, LoginActivity.class);
    	startActivity(home);
    }
	
	private void InitializeActionBar()
	{
		EditText userNameTextField = (EditText)findViewById(R.id.displayUserName);
		userNameTextField.setText("Welcome " + SaveSharedPreference.getUserName(getApplicationContext()));
		
		String s = SaveSharedPreference.getUserName(getApplicationContext());
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
	}
	
	private void InitializeBluetoothRecieverFilters()
	{
		// Register the BroadcastReceiver
		bluetoothReceiverFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		bluetoothReceiverFilter.addAction(BluetoothDevice.ACTION_UUID);
		bluetoothReceiverFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		bluetoothReceiverFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, bluetoothReceiverFilter); 	// Don't forget to unregister
																// during onDestroy
	}
	
	private void InitializeProgressSpinner()
	{
		mProgressDialog = new ProgressDialog(DashboardActivity.this);
    	mProgressDialog.setMessage("Logging In");
    	mProgressDialog.setIndeterminate(true);
    	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
	
	private void InitializeArrayAdapters() {
		// Initialize ArrayAdapters for comparison
		mScannedDevices = new ArrayAdapter<String>(this,
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

	public static class DummySectionFragment extends Fragment {
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

	private class DashboardAsyncRestCaller extends AsyncTask<String, Integer, JSONArray> {
        @Override
        protected  JSONArray doInBackground(String... sUrl) {
        	mUsersFromServer = executeREST(sUrl[0]);

        	if(mUsersFromServer == null)
        		return null;
        	else
        		return mUsersFromServer;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        
        private JSONArray executeREST(String call) {
        	RESTCaller caller = new RESTCaller();
        	JSONObject users = caller.execute(call);
        	try {
				return users.getJSONArray("body");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return null;
      	}
        
        @Override
        protected void onPostExecute(JSONArray result) {
//			// Save the MAC addresses into an ArrayAdapter for comparison
//			for (int i = 0; i < mScannedDevices.getCount(); ++i) {
//				for (int j = 0; j < mUsersFromServer.length(); ++j) {
//					try {
//						if (mUsersFromServer.getJSONObject(i)
//								.get("m_strMac").toString() == mScannedDevices
//								.getItem(j)) {
//							result.put(mUsersFromServer.getJSONObject(i));
//						}
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
			super.onPostExecute(result);
			mProgressDialog.dismiss();
        }
    }
	
	private class DashboardAsyncBluetoothHandler extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... sVoids) {
        	ScanForPlayers();
            return null;
        }
    	
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
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }
        
        @Override
        protected void onPostExecute(JSONArray result) {
//			// Save the MAC addresses into an ArrayAdapter for comparison
//			for (int i = 0; i < mScannedDevices.getCount(); ++i) {
//				for (int j = 0; j < mUsersFromServer.length(); ++j) {
//					try {
//						if (mUsersFromServer.getJSONObject(i)
//								.get("m_strMac").toString() == mScannedDevices
//								.getItem(j)) {
//							result.put(mUsersFromServer.getJSONObject(i));
//						}
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
			super.onPostExecute(result);
			mProgressDialog.dismiss();
        }
    }

}
