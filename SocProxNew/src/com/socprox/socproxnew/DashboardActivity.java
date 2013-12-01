package com.socprox.socproxnew;

import java.util.Vector;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ListFragment;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {
	private static final int STATS_VIEW = 1;
	private static final int CHALLENGE_VIEW = 2;
	private static final int REQUEST_ENABLE_BT = 100;
<<<<<<< HEAD
	private static final String DEBUG_TAG = "DashboardActivity";
    private final static boolean d = true;

	protected BluetoothAdapter mBluetoothAdapter;
	private ProgressDialog mProgressDialog;
    private static String socproxUsername;
=======
	private ProgressDialog mProgressDialog;
>>>>>>> BossingHard
	private ArrayAdapter<String> mScannedDevices = null;

	private Vector<String> mBluetoothDevices = new Vector<String>();
	private Vector<String> mValidUsers = new Vector<String>();

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
<<<<<<< HEAD
				
		//dashboardRestCaller = new DashboardAsyncRestCaller();
		//dashboardBluetoothHandler = new DashboardAsyncBluetoothHandler();
		
=======

		// dashboardRestCaller = new DashboardAsyncRestCaller();
		dashboardBluetoothHandler = new DashboardAsyncBluetoothHandler();

>>>>>>> BossingHard
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
<<<<<<< HEAD
	
	public void onLogoutButtonClicked(View v){
    	SaveSharedPreference.setUserName(getApplicationContext(), "");
    	Intent home = new Intent(DashboardActivity.this, LoginActivity.class);
    	startActivity(home);
    }
	
	private void InitializeActionBar()
	{
//		EditText userNameTextField = (EditText)findViewById(R.id.displayUserName);
//		userNameTextField.setText("Welcome " + SaveSharedPreference.getUserName(getApplicationContext()));
		
		String socproxUsername = SaveSharedPreference.getUserName(getApplicationContext());
=======

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
>>>>>>> BossingHard
		// Set up the action bar to show a dropdown list.
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
<<<<<<< HEAD
		switch(navigationItemPosition + 1) {
			case STATS_VIEW:				
				fragment = new StatsFragment();
				break;
			case CHALLENGE_VIEW:
				fragment = new ChallengeFragment();
				break;
			default:
				fragment = new Fragment();
				break;
		}
		
		Bundle args = new Bundle();
		args.putInt(StatsFragment.ARG_SECTION_NUMBER, navigationItemPosition + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.dashboardFragmentArea, fragment).commit();
		return true;
	}

//	public static class DummySectionFragment extends Fragment {
//		private static final int STATS_VIEW = 1;
//		private static final int CHALLENGE_VIEW = 2;
//		public static final String ARG_SECTION_NUMBER = "section_number";
//		private BluetoothAdapter mBluetoothAdapter;
//		private JSONObject userStats = new JSONObject();
//		static AsyncTask<String, Integer, JSONObject> fragmentRestCaller;
//		private String challengesCompletedValue;
//		private String[] strGameNameArray = new String[5];
//		private String[] iTotalPointsArray = new String[5];
//		private String[] strGameDescriptionArray = new String[5];
//
//		public DummySectionFragment() {
//			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//			fragmentRestCaller = new FragmentAsyncRestCaller();
//			
//			String call = RESTCaller.userStatsCall(mBluetoothAdapter.getAddress());
//			try {
//				userStats = fragmentRestCaller.execute(call).get();
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (ExecutionException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			}
//			
//			Populate();
//		}
//		
//		private void Populate() {
//			try {
//				challengesCompletedValue = userStats.getJSONObject("body").getString("m_iChallengesCompleted");
//				for (int i = 0; i < userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").length(); ++i) {
//					strGameNameArray[i] = userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").getJSONObject(i).getString("m_strGameName");
//					iTotalPointsArray[i] = userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").getJSONObject(i).getString("m_iTotalPoints");
//					strGameDescriptionArray[i] = userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").getJSONObject(i).getString("m_strGameDescription");
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//			View rootView = inflater.inflate(R.layout.fragment_dashboard_dummy,
//					container, false);
//			
//			TextView iChallengeCompleted = (TextView) rootView.findViewById(R.id.iChallengesCompleted);
//			TextView iTotalPoints = (TextView) rootView.findViewById(R.id.iTotalPoints);
//			TextView strGameDescription = (TextView) rootView.findViewById(R.id.strGameDescription);
//			TextView iTotalPoints2 = (TextView) rootView.findViewById(R.id.iTotalPoints2);
//			TextView strGameDescription2 = (TextView) rootView.findViewById(R.id.strGameDescription2);
//			TextView iChallengeCompletedValue = (TextView) rootView.findViewById(R.id.iChallengesCompletedValue);
//			TextView strGameName = (TextView) rootView.findViewById(R.id.strGameName);
//			TextView iTotalPointsValue = (TextView) rootView.findViewById(R.id.iTotalPointsValue);
//			TextView strGameDescriptionValue = (TextView) rootView.findViewById(R.id.strGameDescriptionValue);
//			TextView strGameName2 = (TextView) rootView.findViewById(R.id.strGameName2);
//			TextView iTotalPointsValue2 = (TextView) rootView.findViewById(R.id.iTotalPointsValue2);
//			TextView strGameDescriptionValue2 = (TextView) rootView.findViewById(R.id.strGameDescriptionValue2);
//			
//			switch(getArguments().getInt(ARG_SECTION_NUMBER)) {
//				case STATS_VIEW:				
//					iChallengeCompletedValue.setText(challengesCompletedValue);
//					try {
//						if (userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").length() >= 1) {
//							strGameName.setText(strGameNameArray[0]);
//							iTotalPointsValue.setText(iTotalPointsArray[0]);
//							strGameDescriptionValue.setText(strGameDescriptionArray[0]);
//						}
//						if (userStats.getJSONObject("body").getJSONArray("m_aUserGameStats").length() >= 2) {
//							strGameName2.setText(strGameNameArray[1]);
//							iTotalPointsValue2.setText(iTotalPointsArray[1]);
//							strGameDescriptionValue2.setText(strGameDescriptionArray[1]);			
//						}
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}				
//					break;
//				case CHALLENGE_VIEW:
//					iChallengeCompleted.setVisibility(TextView.INVISIBLE);
//					iTotalPoints.setVisibility(TextView.INVISIBLE);
//					strGameDescription.setVisibility(TextView.INVISIBLE);
//					iTotalPoints2.setVisibility(TextView.INVISIBLE);
//					strGameDescription2.setVisibility(TextView.INVISIBLE);
//					iChallengeCompletedValue.setVisibility(TextView.INVISIBLE);
//					strGameName.setVisibility(TextView.INVISIBLE);
//					iTotalPointsValue.setVisibility(TextView.INVISIBLE);
//					strGameDescriptionValue.setVisibility(TextView.INVISIBLE);
//					strGameName2.setVisibility(TextView.INVISIBLE);
//					iTotalPointsValue2.setVisibility(TextView.INVISIBLE);
//					strGameDescriptionValue2.setVisibility(TextView.INVISIBLE);
//					break;
//				default:
//					break;
//			}			
//			
////			TextView dummyEditText = (TextView) rootView
////					.findViewById(R.id.section_label);
////			dummyEditText.setText(Integer.toString(getArguments().getInt(
////					ARG_SECTION_NUMBER)));
//			return rootView;
//		}
=======
		ListFragment listFragment;
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		switch (position + 1) {
		case STATS_VIEW:
			listFragment = new StatsFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, listFragment).commit();
			break;
		case CHALLENGE_VIEW:
			fragment = new ChallengeFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, fragment).commit();
			break;
		default:
			fragment = new DummySectionFragment();
			fragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, fragment).commit();
			break;
		}

		return true;
	}

	public static class DummySectionFragment extends Fragment {
		private static final int STATS_VIEW = 1;
		private static final int CHALLENGE_VIEW = 2;
		public static final String ARG_SECTION_NUMBER = "section_number";
		private BluetoothAdapter mBluetoothAdapter;
		private JSONObject userStats = new JSONObject();
		static AsyncTask<String, Integer, JSONObject> fragmentRestCaller;
		private String challengesCompletedValue;
		private String[] strGameNameArray = new String[5];
		private String[] iTotalPointsArray = new String[5];
		private String[] strGameDescriptionArray = new String[5];

		public DummySectionFragment() {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			fragmentRestCaller = new FragmentAsyncRestCaller();

			String call = RESTCaller.userStatsCall(mBluetoothAdapter
					.getAddress());
			try {
				userStats = fragmentRestCaller.execute(call).get();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			Populate();
		}

		private void Populate() {
			try {
				challengesCompletedValue = userStats.getJSONObject("body")
						.getString("m_iChallengesCompleted");
				for (int i = 0; i < userStats.getJSONObject("body")
						.getJSONArray("m_aUserGameStats").length(); ++i) {
					strGameNameArray[i] = userStats.getJSONObject("body")
							.getJSONArray("m_aUserGameStats").getJSONObject(i)
							.getString("m_strGameName");
					iTotalPointsArray[i] = userStats.getJSONObject("body")
							.getJSONArray("m_aUserGameStats").getJSONObject(i)
							.getString("m_iTotalPoints");
					strGameDescriptionArray[i] = userStats
							.getJSONObject("body")
							.getJSONArray("m_aUserGameStats").getJSONObject(i)
							.getString("m_strGameDescription");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_dashboard_dummy,
					container, false);

			TextView iChallengeCompleted = (TextView) rootView
					.findViewById(R.id.iChallengesCompleted);
			TextView iTotalPoints = (TextView) rootView
					.findViewById(R.id.iTotalPoints);
			TextView strGameDescription = (TextView) rootView
					.findViewById(R.id.strGameDescription);
			TextView iTotalPoints2 = (TextView) rootView
					.findViewById(R.id.iTotalPoints2);
			TextView strGameDescription2 = (TextView) rootView
					.findViewById(R.id.strGameDescription2);
			TextView iChallengeCompletedValue = (TextView) rootView
					.findViewById(R.id.iChallengesCompletedValue);
			TextView strGameName = (TextView) rootView
					.findViewById(R.id.strGameName);
			TextView iTotalPointsValue = (TextView) rootView
					.findViewById(R.id.iTotalPointsValue);
			TextView strGameDescriptionValue = (TextView) rootView
					.findViewById(R.id.strGameDescriptionValue);
			TextView strGameName2 = (TextView) rootView
					.findViewById(R.id.strGameName2);
			TextView iTotalPointsValue2 = (TextView) rootView
					.findViewById(R.id.iTotalPointsValue2);
			TextView strGameDescriptionValue2 = (TextView) rootView
					.findViewById(R.id.strGameDescriptionValue2);

			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
			case STATS_VIEW:
				iChallengeCompletedValue.setText(challengesCompletedValue);
				try {
					if (userStats.getJSONObject("body")
							.getJSONArray("m_aUserGameStats").length() >= 1) {
						strGameName.setText(strGameNameArray[0]);
						iTotalPointsValue.setText(iTotalPointsArray[0]);
						strGameDescriptionValue
								.setText(strGameDescriptionArray[0]);
					}
					if (userStats.getJSONObject("body")
							.getJSONArray("m_aUserGameStats").length() >= 2) {
						strGameName2.setText(strGameNameArray[1]);
						iTotalPointsValue2.setText(iTotalPointsArray[1]);
						strGameDescriptionValue2
								.setText(strGameDescriptionArray[1]);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case CHALLENGE_VIEW:
				iChallengeCompleted.setVisibility(TextView.INVISIBLE);
				iTotalPoints.setVisibility(TextView.INVISIBLE);
				strGameDescription.setVisibility(TextView.INVISIBLE);
				iTotalPoints2.setVisibility(TextView.INVISIBLE);
				strGameDescription2.setVisibility(TextView.INVISIBLE);
				iChallengeCompletedValue.setVisibility(TextView.INVISIBLE);
				strGameName.setVisibility(TextView.INVISIBLE);
				iTotalPointsValue.setVisibility(TextView.INVISIBLE);
				strGameDescriptionValue.setVisibility(TextView.INVISIBLE);
				strGameName2.setVisibility(TextView.INVISIBLE);
				iTotalPointsValue2.setVisibility(TextView.INVISIBLE);
				strGameDescriptionValue2.setVisibility(TextView.INVISIBLE);
				break;
			default:
				break;
			}

			// TextView dummyEditText = (TextView) rootView
			// .findViewById(R.id.section_label);
			// dummyEditText.setText(Integer.toString(getArguments().getInt(
			// ARG_SECTION_NUMBER)));
			return rootView;
		}

		private class FragmentAsyncRestCaller extends
				AsyncTask<String, Integer, JSONObject> {
			@Override
			protected JSONObject doInBackground(String... sUrl) {
				JSONObject restCall = new JSONObject();
				restCall = executeREST(sUrl[0]);
				if (restCall == null)
					return null;
				else
					return restCall;
			}

			private JSONObject executeREST(String call) {
				RESTCaller caller = new RESTCaller();
				JSONObject userStats = caller.execute(call);
				if (userStats == null)
					return null;
				else
					return userStats;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(JSONObject result) {
				super.onPostExecute(result);
			}
		}
	}

	private class DashboardAsyncRestCaller extends
			AsyncTask<String, Integer, JSONArray> {
		@Override
		protected JSONArray doInBackground(String... sUrl) {
			mUsersFromServer = executeREST(sUrl[0]);

			if (mUsersFromServer == null)
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
			super.onPostExecute(result);
			mProgressDialog.dismiss();
		}
	}

	private class DashboardAsyncBluetoothHandler extends
			AsyncTask<Void, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Void... sVoids) {
			ScanForPlayers();
			return null;
		}

		public void ScanForPlayers() {
			// Start discovery of Bluetooth devices
			if (!mBluetoothAdapter.isDiscovering())
				mBluetoothAdapter.startDiscovery();

			while (mBluetoothAdapter.isDiscovering()) {
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
			super.onPostExecute(result);
			mProgressDialog.dismiss();
		}
	}
>>>>>>> BossingHard

}
