package com.doov.secure.ui.activity;

import java.util.Calendar;

import com.doov.register.aidl.IService;
import com.doov.register.aidl.UserData;
import com.doos.secure.R;
import com.doov.secure.comm.net.Urls;
import com.doov.secure.comm.utils.DoovConstants;
import com.doov.secure.interfaces.ISelectTime;
import com.doov.secure.model.InsuranceModel;
import com.doov.secure.ui.base.BaseActivity;
import com.doov.secure.ui.dialog.SelectTimeDialog;
import com.doov.secure.ui.view.DoovEditText;
import com.doov.secure.wxapi.WXPayEntryActivity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.Toast;

public class WriteInfoActivity extends BaseActivity implements OnClickListener {
	/** 姓名 */
	private DoovEditText name;

	/** 手机号码 */
	private DoovEditText mobile;

	/** 身份证号码 */
	private DoovEditText identity;

	/** 购机时间 */
	private DoovEditText buytime;

	private String username, cardID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBodyView(R.layout.activity_write_info);
		initView();
		bindService();
	}

	/**
	 * 启动服务获取账号系统的手机号码
	 */
	private void bindService() {
		Intent intent = new Intent("com.doov.register.aidl.ACTION");
		intent.setPackage("com.doov.register");
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		setTitleText(R.string.page_write_info_title);
		name = (DoovEditText) findViewById(R.id.name);
		mobile = (DoovEditText) findViewById(R.id.mobile);
		identity = (DoovEditText) findViewById(R.id.identity);
		buytime = (DoovEditText) findViewById(R.id.buytime);
		buytime.setOnClickListener(this);
		name.setOnFocusChangeListener(DoovEditText.NAME);
		mobile.setOnFocusChangeListener(DoovEditText.PHONE);
		identity.setOnFocusChangeListener(DoovEditText.IDCARD);
		mobile.setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	@Override
	public void onClick(View v) {
		name.checkinput();
		mobile.checkinput();
		identity.checkinput();
		v.requestFocus();
		// Calendar calendar = Calendar.getInstance();
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH);
		// int day = calendar.get(Calendar.DAY_OF_MONTH);
		// new DatePickerDialog(this, new OnDateSetListener() {
		//
		// @Override
		// public void onDateSet(DatePicker view, int year, int monthOfYear,
		// int dayOfMonth) {
		// StringBuilder time = new StringBuilder();
		// time.append(year);
		// time.append("-");
		// time.append(monthOfYear+1);
		// time.append("-");
		// time.append(dayOfMonth);
		// buytime.setText(time.toString());
		// }
		// }, year, month, day).show();
		new SelectTimeDialog(this, new ISelectTime() {

			@Override
			public void setTime(String time) {
				buytime.setText(time);
			}
		}, buytime.getText()).show();

	}

	@Override
	public void skipToNext() {

		// 先判断 姓名 手机号 身份证号是否正确
		boolean isName = name.checkinput();
		boolean isMobile = mobile.checkinput();
		boolean isId = identity.checkinput();
		if (isName && isMobile && isId) {
			// 再判断日期是否选择
			if (TextUtils.isEmpty(buytime.getText())) {
				Toast.makeText(this, getResources().getString(R.string.page_writer_time_empty_tips), Toast.LENGTH_SHORT).show();
			} else {
				username = name.getText();
				cardID = identity.getText();
				showLoading();
				getClient().postUserInfo(Urls.PUSH_USER_INFO, username, mobile.getText(), cardID, buytime.getText());
			}
		} else {
			if (name.isEmpty() || mobile.isEmpty() || identity.isEmpty()) {
				Toast.makeText(this, getResources().getString(R.string.page_writer_info_empty_tips), Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public void onSuccess(String url, Object obj) {
		
		super.onSuccess(url, obj);
		if (obj != null) {
			InsuranceModel model = (InsuranceModel) obj;
			Bundle bundle = new Bundle();
			bundle.putString(DoovConstants.MEMBER_ID, model.getInsuranceNO());
			bundle.putString(DoovConstants.USERNAME, username);
			bundle.putString(DoovConstants.IDCARD, cardID);
			
			/**将用户的电话号码添加到bundle中用于支付卡的支付*/
			bundle.putString(DoovConstants.USERPHONGE, mobile.getText());
			
			startActivityTo(bundle, WXPayEntryActivity.class);
		}

	}

	@Override
	public void onFailure(String url, int errorCode) {
		Toast.makeText(this, getResources().getString(R.string.page_write_net_error_tips), Toast.LENGTH_SHORT).show();
		super.onFailure(url, errorCode);
	}

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			try {
				UserData mData = IService.Stub.asInterface(service).getUserData();
				if (mData != null) {
					mobile.setText(mData.getMobile());
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} finally {
				unbindService(conn);
			}
		}
	};
}
