package com.socprox.socproxnew;




import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); 
    }  
	
	public void onLoginButtonClicked(View v) {
		this.startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
		
	}
}
