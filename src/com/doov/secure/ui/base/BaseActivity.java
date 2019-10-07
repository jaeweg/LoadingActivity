package com.doov.secure.ui.base;

import com.doos.secure.R;
import com.doov.secure.comm.eventbus.BusEvent;
import com.doov.secure.comm.net.INetClient;
import com.doov.secure.comm.net.NetClient;
import com.doov.secure.ui.activity.ConfirmedActivity;
import com.doov.secure.ui.dialog.LoadingDialog;

import de.greenrobot.event.EventBus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BaseActivity extends FragmentActivity {
	/** 内容组件容器 */
	private ViewGroup contentView;

	/** 标题栏布局 */
	private RelativeLayout titleBar;

	/** 标题 */
	private TextView mTitle;

	private Button mBaseNext;

	private NetClient mClient;

	private LoadingDialog dialog;
	public boolean isCloseAll;
	
	private BaseActivity mBaseActivity;
	private final static String TAG = "BaseActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		initBaseView();
		initOthers();
		mBaseActivity = new BaseActivity();
	}


	/**
	 * 显示加载框
	 */
	public void showLoading() {
		if (dialog == null) {
			dialog = new LoadingDialog(this);
		}
		Log.d(TAG,"showLoading-->mBaseActivity.isFinishing()==="
				+(mBaseActivity != null && !mBaseActivity.isFinishing()));
		if (mBaseActivity != null && !mBaseActivity.isFinishing() && !dialog.isShowing()) {
			dialog.show();
			Window dialogWindow = dialog.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			dialogWindow.setGravity(Gravity.CENTER);
			dialogWindow.setAttributes(lp);
		}
	}


	/**
	 * 取消加载框
	 */
	public void dismissLoading() {
		Log.d(TAG,"dismissLoading-->mBaseActivity.isFinishing()==="
				+(mBaseActivity != null && !mBaseActivity.isFinishing()));
		if (mBaseActivity != null && !mBaseActivity.isFinishing() && dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	

	/**
	 * 设置跳转按钮文字显示
	 * 
	 * @param resid
	 */
	public void setNextText(int resid) {
		mBaseNext.setText(getResources().getString(resid));
	}

	/**
	 * 获取网络连接类
	 */
	public NetClient getClient() {
		if (mClient == null) {
			mClient = new NetClient(this, new INetClient() {

				@Override
				public void onSuccess(String url, Object obj) {
					BaseActivity.this.onSuccess(url, obj);
			//		Log.i("zwytest", "success is run------");
				}

				@Override
				public void onFailure(String url, int errorCode) {
					BaseActivity.this.onFailure(url, errorCode);
			//		Log.i("zwytest","fail is run--------");
				}
			});
		}
		return mClient;
	}

	/**
	 * 访问服务器成功回调
	 * @param url
	 * @param obj
	 */
	public void onSuccess(String url, Object obj) {
		dismissLoading();
	}

	/**
	 * 访问服务器失败回调
	 * @param url
	 * @param errorCode
	 */
	public void onFailure(String url, int errorCode) {
		dismissLoading();
	}

	/**
	 * 是否隐藏底部按钮
	 * 
	 * @param isvisible
	 */
	public void setBtnVisible(int visibility) {
		mBaseNext.setVisibility(visibility);
	}

	/**
	 * 设置底部按键是否可用
	 */
	public void setBtnClicked(boolean canClicked) {
		mBaseNext.setEnabled(canClicked);
		mBaseNext.setBackgroundDrawable(getResources().getDrawable(
				canClicked ? R.drawable.base_button_bg
						: R.drawable.base_btn_pressd));
	}

	public void btnclick(View v) {
		skipToNext();
	//	Log.i("TAGA","btnclick is click");
	}

	/**
	 * 跳转到下个界面
	 */
	public void skipToNext() {
	}

	/**
	 * 页面跳转
	 * 
	 * @param cls
	 *            需要跳转的activity类
	 */
	public void startActivityTo(Class<?> cls) {
		startActivity(new Intent(this, cls));
	}

	/**
	 * 页面跳转
	 * 
	 * @param cls
	 *            需要跳转的activity类
	 * @param intent
	 */
	public void startActivityTo(Bundle extras, Class<?> cls) {
		Intent intent = new Intent(this, cls);
		intent.putExtras(extras);
		startActivity(intent);
	}

	/**
	 * 通知所有activity关闭
	 */
	public void notifyClose() {
		EventBus.getDefault().post(new BusEvent(BusEvent.CLOSE_ALL_ACTIVITY));
	}

	/**
	 * 设置标题内容
	 * 
	 * @param title
	 */
	public void setTitleText(int titleres) {
		mTitle.setText(getResources().getString(titleres));
	}

	/**
	 * @return 获取标题栏
	 */
	public RelativeLayout getTitleBar() {
		return titleBar;
	}

	/**
	 * @param res
	 *            layout
	 */
	public void setBodyView(int res) {
		View.inflate(this, res, contentView);
	}

	/**
	 * 初始化组件
	 */
	private void initBaseView() {
		contentView = (ViewGroup) findViewById(R.id.contentview);
		titleBar = (RelativeLayout) findViewById(R.id.base_title_bar);
		mBaseNext = (Button) findViewById(R.id.base_next);
		mTitle = (TextView) findViewById(R.id.base_title);
		// 点击标题栏 执行返回操作
		mTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isCloseAll) {
					notifyClose();
				} else {
					finish();
				}
			}
		});
	}

	/**
	 * 初始化其他需要初始化的东西
	 */
	private void initOthers() {
		EventBus.getDefault().register(this);// 注册eventbus
	}

	@Override
	protected void onDestroy() {
		if (mClient != null) {
			mClient.cancel();
		}
		EventBus.getDefault().unregister(this);// 注销eventbus
		super.onDestroy();
	}

	public void onEventMainThread(BusEvent event) {
		switch (event.getAction()) {
		case BusEvent.CLOSE_ALL_ACTIVITY:
			finish();
			break;
		}
	}
}
