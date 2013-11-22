// A class to parse JSON Array -- written by Pratyusha
package com.socprox.socproxnew;
//package com.androidhive.jsonparsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class parseJSON {
	
	static InputStream inputStream = null;
	static JSONObject jsonObj = null;
	static String json = "";
	
	public parseJSON() {
	}
	
	public JSONObject getJSONFromUrl(String url) {
		
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			
			inputStream = httpEntity.getContent();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
			
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			
			while((line = buffReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			inputStream.close();
			json = stringBuilder.toString();
		} catch (Exception e) {
			Log.e("Parse  JSON", "Error parsing data " + e.toString());
		}
		
		return jsonObj;
		}
}
