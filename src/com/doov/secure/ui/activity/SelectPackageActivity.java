package com.doov.secure.ui.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.doos.secure.R;
import com.doov.secure.base.AppContext;
import com.doov.secure.comm.utils.DoovConstants;
import com.doov.secure.comm.utils.LogUtil;
import com.doov.secure.model.SecureModel;
import com.doov.secure.ui.base.BaseActivity;

public class SelectPackageActivity extends BaseActivity {
	private WebView webView;
	private SecureModel mGoodsA, mGoodsB;
	private TextView goodsA_name, goodsB_name, goodsA_price, goodsB_price;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBodyView(R.layout.activity_selcect_package);
		initView();
		initData();
		// lijuan add for buy time tip at 20151222
		showBuyTimeTips();
	}

	/**
	 * 进行赋值
	 */
	private void initData() {
		setTitleText(R.string.page_main_info_title);
		mGoodsA = (SecureModel) getIntent().getExtras().get(DoovConstants.GOODS_A);
		mGoodsB = (SecureModel) getIntent().getExtras().get(DoovConstants.GOODS_B);
		/* lijuan modify to use local image at 20160309 */
		//webView.loadUrl(mGoodsA.getDetialUrl());
		String rmb = getResources().getString(R.string.page_select_rmb_txt);
		goodsA_name.setText(mGoodsA.getGoodsName());
		goodsB_name.setText(mGoodsB.getGoodsName());
		goodsA_price.setText(rmb + Double.parseDouble(mGoodsA.getGoodsPrice())/100);
		goodsB_price.setText(rmb + Double.parseDouble(mGoodsB.getGoodsPrice())/100);
	}

	/**
	 * 初始化各类组件
	 */
	private void initView() {
		webView = (WebView) findViewById(R.id.webView);
		goodsA_name = (TextView) findViewById(R.id.goodsA_name);
		goodsB_name = (TextView) findViewById(R.id.goodsB_name);
		goodsA_price = (TextView) findViewById(R.id.goodsA_price);
		goodsB_price = (TextView) findViewById(R.id.goodsB_price);
		// 隐藏底部按钮
		setBtnVisible(View.GONE);
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		// lijuan modify to use local image at 20160309
		/* 
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
		*/
		webView.getSettings().setDefaultTextEncodingName("gbk");
		String path = "wuyouxian.html";
		webView.loadUrl("file:///android_asset/" + path);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}

	public void selcetPackage(View v) {
		switch (v.getId()) {
		case R.id.packageA:
			AppContext.secureModel = mGoodsA;
			if(mGoodsA == null){
				LogUtil.i("yhw DoovSecure", "mGoodsA is null:? " + mGoodsA);
				return;
			}
			break;
		case R.id.packageB:
			AppContext.secureModel = mGoodsB;
			if(mGoodsB == null){
				Log.i("yhw DoovSecure", "mGoodsB is null:? " + mGoodsB);
				return;
			}
			break;
		default:
			Log.i("yhw DoovSecure", "mGoodsA and mGoodsB is not all click ");
			return;
		}
		startActivityTo(PackageInfoActivity.class);
	}

	/**
	 * lijuan add for buy time tips at 20151222
	 */
	private void showBuyTimeTips() {
		new AlertDialog.Builder(this)
				.setMessage(getString(R.string.buy_time_tips))
				.setNegativeButton(getString(R.string.know_notice),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//finish();
							}
						}).setCancelable(false).show();
	}	
}
