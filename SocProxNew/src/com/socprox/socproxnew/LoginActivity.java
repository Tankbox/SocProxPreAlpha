package com.socprox.socproxnew;





import com.socprox.socproxnew.RESTCaller.Website;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


/*This is the first screen after app launches which consists of the login interface*/
public class LoginActivity extends Activity {
	
	private String userName, password;

	//this method loads when the app starts
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); 				//load the login.xml
        
        //hide action bar for the login screen
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }  
	
	//
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
