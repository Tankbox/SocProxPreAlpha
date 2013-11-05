package com.vogella.android.first;

import java.util.*;
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

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.view.View;
import android.bluetooth.*;

public class MainActivity extends Activity {
	
	// Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Array adapter for the list of devices
    private ArrayAdapter<String> mArrayAdapter = null;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    public static final String EXTRA_MESSAGE = "com.vogella.android.first.MESSAGE";
    private static final String BASEURL = "http://cjcornell.com/bluegame/REST/";
    Context context;
	
	// Saving all users to a an array from the server
	public JSONArray executeToArray(String url) {
		String finalURL = BASEURL + url;

		// Setup the http variables for the call
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(finalURL);
		String result = null;
		JSONArray jsonArray = null;
		try {
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			result = getASCIIContentFromEntity(entity);
			jsonArray = new JSONArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

	protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);
			if (n > 0) out.append(new String(b, 0, n));
		}
		return out.toString();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onScanClick (View view)
	{
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        


        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
        
        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
        	Toast.makeText(this, "Scanning...", Toast.LENGTH_LONG).show();
        	mArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_main);
        	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        	
        	// If there are paired devices
        	if (pairedDevices.size() > 0) {
        	    // Loop through paired devices
        	    for (BluetoothDevice device : pairedDevices) {
        	        // Add the name and address to an array adapter to show in a ListView
        	        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        	    }
        	}
        	
        	JSONArray usersReturned = executeToArray("users");
        	
        	for (int i = 0; i < usersReturned.length(); ++i)
        	{
        		try {
					usersReturned.getJSONObject(i).get("macaddress");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	
        	/*
        	mBluetoothAdapter.startDiscovery();
        	
        	final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        	    public void onReceive(Context context, Intent intent) {
        	        String action = intent.getAction();
        	        // When discovery finds a device
        	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        	            // Get the BluetoothDevice object from the Intent
        	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        	            // Add the name and address to an array adapter to show in a ListView
        	            System.out.println(device.getName());
        	            mArrayAdapter.add(device.getName() + "\n" + device.getAddress());   
        	        }
        	    }
        	};
        	// Register the BroadcastReceiver
        	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        	context.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        	
        	// If the device is discovering, stop it
        	if (mBluetoothAdapter.isDiscovering()) {
        		mBluetoothAdapter.cancelDiscovery();
        	}

        	return discoverableDevicesList;
        	*/

        }		
	}
	
	public void onExecuteClick(View view)
	{
        Intent intent = new Intent(this, ListActivity.class);
        
        
        
    	startActivity(intent);
	}
}
