package com.doov.secure.ui.dialog;

import com.doos.secure.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CardpayFailDialog extends Dialog implements android.view.View.OnClickListener{
	private TextView cardpay_fail_tips;
	private String mErrorDesc;

	public CardpayFailDialog(Context context) {
		this(context,R.style.select_time_dialog);
	}
	
	public CardpayFailDialog(Context context,int Theme) {
		super(context,Theme);
	}
	
	public CardpayFailDialog(Context context,String errorDesc){
		this(context, R.style.select_time_dialog);
		mErrorDesc = errorDesc;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_cardpay_fali);
		initView();
		initStyle();
	}
	/**
	 * 初始化组件
	 */
	private void initView() {
		cardpay_fail_tips = (TextView) findViewById(R.id.cardpay_fail_tips);
		cardpay_fail_tips.setText(mErrorDesc);
		Button btn_ok = (Button) findViewById(R.id.OK);
		btn_ok.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}
	/**
	 * 设置dialog样式
	 */
	private void initStyle() {
		Window win = getWindow();
		win.setGravity(Gravity.BOTTOM);
		win.setWindowAnimations(R.style.activityAnimation);
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		win.setAttributes(lp);
	}


}
