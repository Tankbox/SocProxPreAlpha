package com.socprox.socproxnew;

import java.util.ArrayList;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONException;
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
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class DashboardActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {
	private static final int STATS_VIEW = 1;
	private static final int CHALLENGE_VIEW = 2;
	private static final int REQUEST_ENABLE_BT = 100;
	protected BluetoothAdapter mBluetoothAdapter;
	private ProgressDialog mProgressDialog;
	private ArrayAdapter<String> mScannedDevices = null;

	private Vector<String> mBluetoothDevices = new Vector<String>();
	private ArrayList<String> mValidUsers = new ArrayList<String>();

	private IntentFilter bluetoothReceiverFilter = null;
	private JSONArray mUsersFromServer = new JSONArray();
	private JSONArray mValidPlayers = new JSONArray();

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	// static AsyncTask<String, Integer, JSONArray> dashboardRestCaller;
	static AsyncTask<Void, Void, JSONArray> dashboardBluetoothHandler;
	private RESTCaller restServiceCaller = new RESTCaller();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		InitializeActionBar();
		InitializeBluetoothRecieverFilters();

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		CheckAndEnableBluetooth();
		InitializeProgressSpinner();

		if (mBluetoothAdapter.isEnabled()) {
			InitializeArrayAdapters();

			try {
				mUsersFromServer = restServiceCaller.execute(
						RESTCaller.getUsersCall()).getJSONArray("body");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// mUsersFromServer = dashboardRestCaller.execute("users").get();
			StartDiscovery();
		}
	}

	public void onLogoutButtonClicked(View v) {
		SaveSharedPreference.setUserName(getApplicationContext(), "");
		Intent home = new Intent(DashboardActivity.this, LoginActivity.class);
		startActivity(home);
	}

	private void InitializeActionBar() {
		// EditText userNameTextField =
		// (EditText)findViewById(R.id.displayUserName);
		// userNameTextField.setText("Welcome " +
		// SaveSharedPreference.getUserName(getApplicationContext()));

		SaveSharedPreference.getUserName(getApplicationContext());
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar
				.setListNavigationCallbacks(
						// Specify a SpinnerAdapter to populate the dropdown
						// list.
						new ArrayAdapter<String>(
								actionBar.getThemedContext(),
								android.R.layout.simple_list_item_1,
								android.R.id.text1,
								new String[] {
										getString(R.string.stats_section),
										getString(R.string.challenge_section), }),
						this);
	}

	private void InitializeBluetoothRecieverFilters() {
		// Register the BroadcastReceiver
		bluetoothReceiverFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		bluetoothReceiverFilter.addAction(BluetoothDevice.ACTION_UUID);
		bluetoothReceiverFilter
				.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		bluetoothReceiverFilter
				.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, bluetoothReceiverFilter); // Don't forget to
																// unregister
																// during
																// onDestroy
	}

	private void InitializeProgressSpinner() {
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
				mBluetoothDevices.add(device.getAddress());
				mScannedDevices.add(device.getAddress());
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				for (int i = 0; i < mBluetoothDevices.size(); ++i) {
					String bluetoothDeviceScanned = mBluetoothDevices
							.elementAt(i);
					for (int j = 0; j < mUsersFromServer.length(); ++j) {
						try {
							String userFromServer = mUsersFromServer
									.getJSONObject(j).get("m_strMac")
									.toString();
							if (userFromServer.equals(bluetoothDeviceScanned)
									&& !mValidUsers
											.contains(bluetoothDeviceScanned)) {
								mValidUsers.add(userFromServer);
								mValidPlayers.put(mUsersFromServer
										.getJSONObject(j));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// TODO Check database for pending challenges
				StartDiscovery();
			}
		}
	};

	private void StartDiscovery() {

		// If we're already discovering, stop it
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}

		// Request discover from BluetoothAdapter
		mBluetoothAdapter.startDiscovery();
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
	public boolean onNavigationItemSelected(int navigationItemPosition, long id) {
		Fragment fragment;
		Bundle args = new Bundle();

		switch (navigationItemPosition + 1) {
		case STATS_VIEW:
			fragment = new StatsFragment();
			break;
		case CHALLENGE_VIEW:
			fragment = new ChallengeFragment();
			args.putString("validPlayers", mValidPlayers.toString());
			break;
		default:
			fragment = new Fragment();
			break;
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		args.putInt(StatsFragment.ARG_SECTION_NUMBER,
				navigationItemPosition + 1);
		fragment.setArguments(args);

		return true;
	}
}
