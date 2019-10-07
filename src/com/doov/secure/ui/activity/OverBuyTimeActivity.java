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

public class OverBuyTimeActivity extends BaseActivity {
	private TextView tips;
	private TextView mHotline;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBodyView(R.layout.activity_over_buy_time);
		setBtnVisible(View.GONE);
		setTitleText(R.string.page_confirm_title);
		tips = (TextView) findViewById(R.id.over_buy_time_tip);
		mHotline = (TextView) findViewById(R.id.hotline);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
