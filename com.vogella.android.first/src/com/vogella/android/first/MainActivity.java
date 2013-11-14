package com.vogella.android.first;

import java.io.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.View;
import android.bluetooth.*;

public class MainActivity extends Activity {
	// Boolean for debugging
	boolean DEBUG = true;

	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Array adapter for the list of scanned devices
	private ArrayAdapter<String> mScannedDevices = null;
	// Array adapter for the list of users
	private ArrayAdapter<String> mListUsers = null;
	private ArrayAdapter<String> mNearbyUsers_MAC = null;
	private ArrayAdapter<String> mNearbyUsers_NAME = null;
	private ArrayAdapter<String> mListGames = null;
	// ListView for showing the returned data
	private ListView listView = null;
	// Intent request codes
	private static final int REQUEST_ENABLE_BT = 3;
	// String used for new activity start
	public static final String EXTRA_MESSAGE = "com.vogella.android.first.MESSAGE";
	// String used as BASEURL for execution of RESTful calls
	private static final String BASEURL = "http://cjcornell.com/bluegame/REST/";
	private final Context context = this;
	// JSONArray to be used from the REST call
	private JSONArray usersReturned = null;
	private JSONArray gamesReturned = null;

	// Saving all users to a an array from the server
	// This was taken from the previuos build of SocProx
	public JSONArray executeToArray(String url) {
		String finalURL = BASEURL + url;

		// Setup the http variables for the call
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(finalURL);
		String result = null;
		JSONObject jsonObject = null;
		// Get the list of addresses from the server
		try {
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			result = getASCIIContentFromEntity(entity);
			jsonObject = new JSONObject(result);
			// Return the body of the REST call
			return jsonObject.getJSONArray("body");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// If something bad happens, return null
		return null;
	}

	// Gets the ASCII content from each entity in the HTTP response
	// This was taken from the previuos build of SocProx
	protected String getASCIIContentFromEntity(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);
			if (n > 0)
				out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_UUID);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter); // Don't forget to unregister
												// during onDestroy

		// Editing beyond this point

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
		} else {
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

			// Scan for unpaired devices
			scanForPlayers();

			// Finally set the value of the ListView to the value of the
			// mScannedDevices
			// This is just a placeholder to make sure that the scan is behaving
			// the correct way
			listView = (ListView) findViewById(R.id.listOfPlayers);
			listView.setAdapter(mScannedDevices);
		}

		// This code sets the Play button to INVISIBLE and then after 12 seconds
		// (the length of time needed to scan for player) it sets it to VISIBLE
		findViewById(R.id.playButton).setVisibility(View.INVISIBLE);
		findViewById(R.id.playButton).postDelayed(new Runnable() {
			public void run() {
				findViewById(R.id.playButton).setVisibility(View.VISIBLE);
			}
		}, 12000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	public void scanForPlayers() {
		// Start discovery of Bluetooth devices
		if (!mBluetoothAdapter.isDiscovering())
			mBluetoothAdapter.startDiscovery();
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

	// onPlayClick can only be executed after scanning is complete
	// then the user can get a list of the users in the area and
	// select one of them to play a game
	public void onPlayClick(final View view) {
		// Updated REST call to server (working code space)
		// try {
		// // Try to run a new thread for the REST call
		// new Thread(new Runnable() {
		// public void run() {
		// // Getting all games for now
		// String call = RESTCaller.getAllGamesCall();
		// RESTCaller caller = new RESTCaller();
		// gamesReturned = caller.executeToArray(call);
		// }
		// }).start();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		String call = RESTCaller.getAllGamesCall();
		RESTCaller caller = new RESTCaller();
		// gamesReturned = caller.executeToArray(call);
		gamesReturned = executeToArray("getAllGames");

		// Go through the games and get the object to be displayed
		for (int i = 0; i < gamesReturned.length(); ++i) {
			try {
				JSONObject game = gamesReturned.getJSONObject(i);
				game.get("m_strName");
				String s = game.get("m_strName").toString();
				// mListGames.add(gamesReturned.getJSONObject(i).get("m_strName").toString());
				mListGames.add(s);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// // RESTful call to server which pulls a list of all the users
		// try {
		// usersReturned = executeToArray("users");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// // Save the MAC addresses into an ArrayAdapter for comparison
		// for (int i = 0; i < usersReturned.length(); ++i) {
		// try {
		// mListUsers.add(usersReturned.getJSONObject(i).get("m_strMac").toString());
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		//
		// // Step through the list of common devices found in the two
		// // ArrayAdapters and save
		// // them to show the user the nearby players.
		// // TODO: OPTIMIZE THIS LOOKUP SECTION
		// for (int i = 0; i < mListUsers.getCount(); ++i) {
		// for (int j = 0; j < mScannedDevices.getCount(); ++j) {
		// // If the location in the mListUsers and mScannedDevices is a
		// // match, we have
		// // a player nearby
		// if (mListUsers
		// .getItem(i)
		// .toString()
		// .equalsIgnoreCase(mScannedDevices.getItem(j).toString())) {
		// mNearbyUsers_MAC.add(mScannedDevices.getItem(j));
		// mNearbyUsers_MAC.notifyDataSetChanged();
		// }
		// }
		// }
		//
		// // This section looks up all the users in the mNearbyUsers_MAC and
		// saves
		// // their username to
		// // be displayed to the user
		// for (int i = 0; i < mNearbyUsers_MAC.getCount(); ++i) {
		// for (int j = 0; j < usersReturned.length(); ++j) {
		// try {
		// if (mNearbyUsers_MAC
		// .getItem(i)
		// .toString()
		// .equalsIgnoreCase(
		// usersReturned.getJSONObject(j)
		// .get("m_strMac").toString()))
		// mNearbyUsers_NAME.add(usersReturned.getJSONObject(j)
		// .get("m_strUsername").toString());
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }

		// Finally set the value of the ListView to the value of the mNeabyUsers
		listView = (ListView) findViewById(R.id.listOfPlayers);
		// If the mNearbyUsers_MAC isn't empty
		if (DEBUG) {
			listView.setAdapter(mListGames);
		} else {
			if (!mNearbyUsers_NAME.isEmpty())
				// Show the list of nearby players
				listView.setAdapter(mNearbyUsers_NAME);
			// Else alert the user there is no one around them
			else {
				// Create new AlertDialog Builder
				AlertDialog.Builder noPlayersFound = new AlertDialog.Builder(
						context);
				// Set the title
				noPlayersFound
						.setTitle("SocProx didn't find any players near you!");
				// Set the message
				noPlayersFound
						.setMessage("Click \"Try Again\" to scan one more time or \"Cancel\" to go back.");
				// Set if the dialog box can be cancelled or not
				noPlayersFound.setCancelable(false);
				// Set what the positive button does
				noPlayersFound.setPositiveButton("Try Again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								scanForPlayers();
							}
						});
				// Set what the negative button does
				noPlayersFound.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
				// Create the dialog box from the builder we just used
				AlertDialog noPlayersAlert = noPlayersFound.create();
				// Show it on screen
				noPlayersAlert.show();
			}
		}
	}
}
