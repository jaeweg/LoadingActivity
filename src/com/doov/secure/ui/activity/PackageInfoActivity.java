package com.doov.secure.ui.activity;

import com.doos.secure.R;
import com.doos.secure.R.layout;
import com.doov.secure.base.AppContext;
import com.doov.secure.ui.base.BaseActivity;
import com.doov.secure.ui.view.DoovWebView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class PackageInfoActivity extends BaseActivity {
	private WebView webView;
	//private CheckBox checkBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBodyView(R.layout.activity_package_info);
		//将底部按钮文字"下一步" 改为"我同意本条款" by yhw
		Button btn = (Button) findViewById(R.id.base_next);
		btn.setText(R.string.page_button);
		initView();
	}

	private void initView() {
		setTitleText(R.string.page_main_info_title);
		//setBtnClicked(false);
		setBtnClicked(true);
		webView = (DoovWebView) findViewById(R.id.webView);
		//checkBox = (CheckBox) findViewById(R.id.checkBox);
		//checkBox.setOnCheckedChangeListener(this);
//		webView.loadUrl("http://m.doov.com.cn/care/");
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.getSettings().setDefaultTextEncodingName("gbk");
		if (AppContext.secureModel != null) {
			String path=AppContext.secureModel.getGoodsName().contains("碎屏")?"suipingxian.html":"zhengjixian.html";
			webView.loadUrl("file:///android_asset/"+path);
		}
		//因为已经将 我已阅读单选框去掉
//		new Handler().postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				checkBox.setVisibility(View.VISIBLE);
//			}
//		}, 1000);
	}

	@Override
	public void skipToNext() {
		startActivityTo(WriteInfoActivity.class);
	}

	/*@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		setBtnClicked(isChecked);
	}*/
}
