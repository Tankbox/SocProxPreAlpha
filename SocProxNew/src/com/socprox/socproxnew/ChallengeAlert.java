// Written by Pratyusha
package com.socprox.socproxnew;

import android.os.Bundle;
import android.app.*;
import android.content.DialogInterface;


//A popup which asks user if they want to play now or later
//This should appear on the stats page after the user login/signup 

public class ChallengeAlert extends Activity{
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert);
		
		AlertDialog alertDialog = new  AlertDialog.Builder(this).create();
		alertDialog.setTitle("Challenge Confirmation");
		alertDialog.setMessage("Ready for a challenge?");
		alertDialog.setButton("Play  Now!", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// If this button is clicked, close alert, current activity and display available challenges
				ChallengeAlert.this.finish();
			}
		});
		alertDialog.setButton2("Play Later", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// If this button is clicked, close the alert box, do nothing
				dialog.cancel();
			}
		});
		
		alertDialog.show();
	}
}
