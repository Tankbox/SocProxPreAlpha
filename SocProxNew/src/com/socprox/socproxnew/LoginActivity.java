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
import android.graphics.Color;
import android.os.AsyncTask;
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
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); 				//load the login.xml
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //hide action bar for the login screen
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        
        mProgressDialog = new ProgressDialog(LoginActivity.this);
    	mProgressDialog.setMessage("Logging In");
    	mProgressDialog.setIndeterminate(true);
    	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }  
	
<<<<<<< HEAD
	//
//	public void onLoginButtonClicked(View v){
//    	userName = ((EditText)findViewById(R.id.user_name)).getText().toString().trim();
//    	password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
//
//    	// execute this when the login must be fired
//    	LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
//    	loginAsyncTask.execute();
//    }
	
//	private class LoginAsyncTask extends AsyncTask<String, Integer, Boolean> {
//        @Override
//        protected Boolean doInBackground(String... sUrl) {
//        	boolean result = false;
//            try {
//            	String call = RESTCaller.loginCall(Website.SOCPROX, mBluetoothAdapter.getAddress(), userName, password);
//            	result = executeREST(call);
//            } catch (Exception e) {
//            	if(d) {
//    				Log.d(DEBUG_TAG, "Error on REST execution.");
//    			}
//            }
//            return result;
//        }
//        
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mProgressDialog.show();
//        }
        
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
    //}
=======
	public void onLoginButtonClicked(View v){
    	userName = ((EditText)findViewById(R.id.user_name)).getText().toString().trim();
    	password = ((EditText)findViewById(R.id.password)).getText().toString().trim();

    	// execute this when the login must be fired
    	LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
    	loginAsyncTask.execute();
    }
	
	private class LoginAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... sUrl) {
        	boolean result = false;
            try {
            	String call = RESTCaller.loginCall(Website.SOCPROX, mBluetoothAdapter.getAddress(), userName, password);
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
        
        private boolean executeREST(String call) {
    		RESTCaller caller = new RESTCaller();
    		JSONObject restCallResponse = caller.execute(call);
    		boolean error = false;
          
    		try {
    			JSONObject restBody = restCallResponse.getJSONObject("body");
    			socproxUsername = restBody.getString("m_strUsername");
    			
    			//Login successful
    			JSONObject userJsonObject = new JSONObject();
    			userJsonObject.put("username", socproxUsername);
    			userJsonObject.put("userMac", restBody.getString("m_strMac"));
    			userJsonObject.put("realName", restBody.getString("m_strName"));
    			userJsonObject.put("email", restBody.getString("m_strFacebook"));
    			User.getInstance(userJsonObject, LoginActivity.this);
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
    	          	String errorMessage = restCallResponse.getString("message");
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
        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            
            if(result){
            	startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            }
            else{
            	Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }
>>>>>>> 169199f7f3a7452702796986902290dac8ce0984
}
