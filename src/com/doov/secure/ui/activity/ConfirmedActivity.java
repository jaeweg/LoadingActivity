package com.doov.secure.ui.activity;

import com.doos.secure.R;
import com.doos.secure.R.layout;
import com.doov.secure.ui.base.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ConfirmedActivity extends BaseActivity {
	private static final int SUCCESS = 2;
	private static final int NOPASS = 3;
	private static final int FAIL = 1;
	private TextView tips;
	private TextView mHotline;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBodyView(R.layout.activity_confirmed);
		setBtnVisible(View.GONE);
		setTitleText(R.string.page_confirm_title);
		mHotline = (TextView) findViewById(R.id.doov_hotline);
		String memberid = getIntent().getStringExtra("memberid");
		int type = getIntent().getIntExtra("type", 0);
		tips = (TextView) findViewById(R.id.tips);
		String tip = null;
		switch (type) {
		case FAIL:
			tip = getResources().getString(R.string.confirming, memberid);
			mHotline.setText(getResources().getString(R.string.hotline_server));
			break;
		case NOPASS:
			tip = getResources().getString(R.string.confirm_failure);
			mHotline.setText(getResources().getString(R.string.hotline_server));
			break;
		case SUCCESS:
			tip = getResources().getString(R.string.confirm_succes, memberid);
			mHotline.setText(getResources().getString(R.string.hotline_insurance));
			break;
		default:
			break;
		}
		tips.setText(tip);

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
