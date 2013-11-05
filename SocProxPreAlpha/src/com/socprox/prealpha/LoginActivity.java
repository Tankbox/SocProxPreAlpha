package com.socprox.prealpha;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void loginButtonClicked(View view) {
		String username = ((EditText)findViewById(R.id.usernameEditText)).getText().toString().trim();
		String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString().trim();
		try
		{
			String call = RESTCaller.loginCall(null, "socprox", username, password);
	    	RESTCaller caller = new RESTCaller();
	    	JSONObject result = caller.execute(call);
	    	
	    	if (result.get("success").equals(0))
	    	{
	    		
	    	}
	    	else
	    	{
	    		
	    	}
		} catch(Exception e)
		{
			System.out.print(e);
			// Need to execute username and password for REST call now
		}
	}
}