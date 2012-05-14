package com.hippocrene.view;

import com.hippocrene.R;
import com.hippocrene.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SomeActivity extends Activity{
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.some);
	        Intent intent = new Intent(SomeActivity.this, FreeBikeActivity.class);
	        startActivity(intent);
	        finish();
	 }
}