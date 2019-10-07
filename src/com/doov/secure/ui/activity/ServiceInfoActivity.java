package com.doov.secure.ui.activity;

import com.doos.secure.R;
import com.doov.secure.ui.base.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

public class ServiceInfoActivity extends BaseActivity {
	private ScrollView mScrollView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBodyView(R.layout.activity_service_info);
		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		setTitleText(R.string.page_main_info_title);
		setBtnVisible(View.GONE);
		mScrollView=(ScrollView) findViewById(R.id.scrollview);
	}
}
