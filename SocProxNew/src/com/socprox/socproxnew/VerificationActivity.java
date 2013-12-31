package com.socprox.socproxnew;

//import com.google.zxing.client.android.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class VerificationActivity {}/*extends CaptureActivity {
	
	private Button scanBtn;
	private TextView formatTxt, contentTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verification);
		
		scanBtn = (Button)findViewById(R.id.scan_button);
		formatTxt = (TextView)findViewById(R.id.scan_format);
		contentTxt = (TextView)findViewById(R.id.scan_content);
		
		scanBtn.setOnClickListener((OnClickListener) this);
		
	}
	
	public void onClick(View v){
		//respond to clicks
		if(v.getId()==R.id.scan_button){
			//scan
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			startActivityForResult(intent, 0);
			}
	}

	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		   if (requestCode == 0) {
		      if (resultCode == RESULT_OK) {
		         String contents = intent.getStringExtra("SCAN_RESULT");
		         String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
		         formatTxt.setText("FORMAT: " + format);
					contentTxt.setText("CONTENT: " + contents);
		         // Handle successful scan
		      } else if (resultCode == RESULT_CANCELED) {
		         // Handle cancel
		    	  Toast toast = Toast.makeText(getApplicationContext(),
		  		        "No scan data received!", Toast.LENGTH_SHORT);
		  		  toast.show();
		      }
		   }
		}
}
*/
