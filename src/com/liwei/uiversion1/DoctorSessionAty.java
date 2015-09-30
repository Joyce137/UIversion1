package com.liwei.uiversion1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * @Description:医生会话界面
 * @author:liwei
 * @time:2015-3-9 上午10:37:19
 */
public class DoctorSessionAty extends Activity {

	/*
	 * (non-Javadoc)
	 * 2015-03-11  未解决:EditText被弹出来的输入框挡住,该如何处理
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.doctor_session);

		Button backButton = (Button) findViewById(R.id.back_button1);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				// onBackPressed();
			}
		});

		// EditText ed = (EditText) findViewById(R.id.doctor_session_editText);

		Intent intent = getIntent();
		String dName = intent.getStringExtra("doctor_name");
		String gName = intent.getStringExtra("grade_name");

		TextView d_tv = (TextView) findViewById(R.id.doctor_name);
		TextView g_tv = (TextView) findViewById(R.id.doctor_gradeName);

		d_tv.setText(dName);
		g_tv.setText(gName);

	}

}
