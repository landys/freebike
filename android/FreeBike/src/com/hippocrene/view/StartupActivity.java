package com.hippocrene.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hippocrene.R;

public class StartupActivity extends Activity{
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.startup);
	        
	        Intent intent = new Intent(StartupActivity.this, FreeBikeActivity.class);
	        startActivity(intent);
	        
	        finish();
	 }
}