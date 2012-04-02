package com.hirethisdeveloper.jsonexampleapp2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class JSONExampleApp2Activity extends Activity {
	
	private static final String EMPTY_STRING = "";
	
	private TextView jsonOutput;
	private EditText inputSearchQuery;
    private Button btnGo;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.findAllViewsById();
        
        // handle btnGo click
        btnGo.setOnClickListener(new OnClickListener() {            
            public void onClick(View v) {
                String thisOut = EMPTY_STRING;
            	jsonOutput.setText(thisOut);
            	
            	String queryText = inputSearchQuery.getText().toString();
            	//jsonOutput.append(queryText);
            	
            	String readTwitterFeed = readTwitterFeed(queryText);
            	
            	
        		try {        			
        			JSONObject jsonObject = new JSONObject(readTwitterFeed);
        			JSONArray jsonArray = jsonObject.getJSONArray("results");
        			
        			thisOut = "Number of entries: " + jsonArray.length() + "\n";
        			
        			for (int i = 0; i < jsonArray.length(); i++) {
        				JSONObject jItem = jsonArray.getJSONObject(i);
        				String txt = jItem.getString("text");
        				String who = jItem.getString("from_user");
        				
        				thisOut += "------------------------------------------------\n";
        				thisOut += "From: " + who + "\n" + txt + "\n\n";
        			}
        			
        			jsonOutput.append(thisOut);     			
        			
        		} catch (Exception e) {
        			jsonOutput.append("Error parsing feed.");
        			e.printStackTrace();
        		}
            	
            }
        });
        
        
        inputSearchQuery.setOnFocusChangeListener(new DftTextOnFocusListener(getString(R.string.inputsearchquery)));
    }

    
    
    // ====================================================================
    // helper functions --------------
    
    public String readTwitterFeed(String queryTxt) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(
				"http://search.twitter.com/search.json?q=" + queryTxt);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(JSONExampleApp2Activity.class.toString(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
    
    
    
    public void longToast(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    
    private void findAllViewsById() {
    	jsonOutput = (TextView) findViewById(R.id.jsonOutput);
    	inputSearchQuery = (EditText) findViewById(R.id.inputSearchQuery);
        btnGo = (Button) findViewById(R.id.button1);
    }
    
    
    private class DftTextOnFocusListener implements OnFocusChangeListener {
        
        private String defaultText;

        public DftTextOnFocusListener(String defaultText) {
            this.defaultText = defaultText;
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (v instanceof EditText) {
                EditText focusedEditText = (EditText) v;
                // handle obtaining focus
                if (hasFocus) {
                    if (focusedEditText.getText().toString().equals(defaultText)) {
                        focusedEditText.setText(EMPTY_STRING);
                    }
                }
                // handle losing focus
                else {
                    if (focusedEditText.getText().toString().equals(EMPTY_STRING)) {
                        focusedEditText.setText(defaultText);
                    }
                }
            }
        }
    }
    
    
}