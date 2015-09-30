package com.liwei.uiversion1;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * @Description:TODO
 * @author:liwei
 * @time:2015-2-5 下午9:56:53
 */
public class State extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_state);
	}

}
