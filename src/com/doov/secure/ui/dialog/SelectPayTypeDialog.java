package com.doov.secure.ui.dialog;

import com.doos.secure.R;
import com.doov.secure.comm.eventbus.BusEvent;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class SelectPayTypeDialog extends Dialog implements android.view.View.OnClickListener {
	private TextView weixinpay, alipay;
	private Button cancel;
	private View TextView;
	private TextView cardpay;
	public SelectPayTypeDialog(Context context) {
		this(context,R.style.select_time_dialog);
	}
	
	public SelectPayTypeDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 取消dialog标题栏
		setContentView(R.layout.dialog_select_pay_type);
		initView();
		initStyle();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		weixinpay = (TextView) findViewById(R.id.weixinpay);
		alipay = (TextView) findViewById(R.id.alipay);
		cardpay = (TextView) findViewById(R.id.cardpay);
		cancel=(Button) findViewById(R.id.cancel);
		weixinpay.setOnClickListener(this);
		alipay.setOnClickListener(this);
		cardpay.setOnClickListener(this);
		cancel.setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.alipay:
			EventBus.getDefault().post(new BusEvent(BusEvent.USE_ALIPAY));
			break;
		case R.id.weixinpay:
			EventBus.getDefault().post(new BusEvent(BusEvent.USE_WXPAY));
			break;
			//下面为后期支付卡支付，XXX为定义的按钮id ，点击后的的操作在WXPayEntrtActivity里面的onEventMainThread 进行处理
		case R.id.cardpay:
			EventBus.getDefault().post(new BusEvent(BusEvent.USE_PAYCARD));
			break;
		default:
			break;
		}
		dismiss();
	}
}
