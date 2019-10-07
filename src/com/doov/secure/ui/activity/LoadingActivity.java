package com.doov.secure.ui.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.doos.secure.R;
import com.doov.secure.base.AppContext;
import com.doov.secure.comm.net.Urls;
import com.doov.secure.comm.utils.DoovConstants;
import com.doov.secure.comm.utils.PreferencesUtils;
import com.doov.secure.model.BuyedModel;
import com.doov.secure.model.DoovRegModel;
import com.doov.secure.model.IpPortModel;
import com.doov.secure.model.SecureModel;
import com.doov.secure.receiver.DoovSecureReceiver;
import com.doov.secure.ui.base.BaseActivity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.doov.secure.comm.utils.LogUtil;

public class LoadingActivity extends BaseActivity {
	private DoovRegModel regmodel;
	boolean isNetError = false;
	public static final String TAG = "LoadingActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		init();		
		/*
		//Log.i("TAG", PreferencesUtils.getBuyed(this)+"-----");
		// 判断是否购买过，购买过直接从本地获取数据跳转 ，没有则从服务器获取
		if (PreferencesUtils.getBuyed(this)) {
			BuyedModel m = PreferencesUtils.getBuyModel(this);
			//获取本地存储状态，如果为审核成功就直接跳转，如果是审核中或者审核失败则继续访问网络获取一次数据
			// Log.i("TAG", m.getType() +"+++++");
			if (m.getType() == 2) {//2为成功状态
				Bundle bundle = new Bundle();
				bundle.putInt("type", m.getType());
				bundle.putString("memberid", m.getInsuranceNO());
				startActivityTo(bundle, ConfirmedActivity.class);
				finish();
			} else {
				//状态 为审核中 或 审核失败  重新访问服务器
				init();
			}
		} else {
			setContentView(R.layout.activity_loading);
			init();

		}
		*/
	}

	private void init() {
		if (PreferencesUtils.getShowNetTips(this)) {
			showNetTips();
		//	Log.i("zwytest","NET_TIPS is true ?");
		} else {
			requestClient();
		}
	}

	private void requestClient() {
		String imei = getIMEI(this);
		LogUtil.i("lijuan", "[requestClient] imei = " + imei);
		if (imei.equals("000000000000000")) {
			showInvalidIMEITips();
			return;
		}
						
		if (!checkNetAvailable()) {
			showNetSettingDialog();
		} else {
			showLoading();
			getClient().checkIsReg(Urls.CHECK_REG);
		//	Log.i("zwytest", "net is available and check reg");
		}
	}

	@Override
	protected void onResume() {
		if (isNetError) {
			requestClient();
		}
		isNetError = false;
		super.onResume();
	}

	private void showNetSettingDialog() {
		new AlertDialog.Builder(this)
				.setMessage(getString(R.string.no_net_client_tips))
				.setPositiveButton(getString(R.string.net_setting),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								isNetError = true;
								Intent intent = new Intent(
										Settings.ACTION_SETTINGS);
								startActivity(intent);
							}
						})
				.setNegativeButton(getString(R.string.exit),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).setCancelable(false).show();
	}

	private void showNetErrorDialog() {

		new AlertDialog.Builder(this)
				.setMessage(getString(R.string.net_client_error))
				.setPositiveButton(getString(R.string.net_refresh),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								showLoading();
								getClient().checkIsReg(Urls.CHECK_REG);
							}
						})
				.setNegativeButton(getString(R.string.exit),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).setCancelable(false).show();

	}

	/**
	 * 提示网络连接需要占用流量提示
	 */
	private void showNetTips() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_loading_tips, null);
		final CheckBox mBox = (CheckBox) view.findViewById(R.id.checkBox);
		new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton(getString(R.string.agree_and_continue),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// 检查是否勾选 不再提醒如果勾选将勾选状态存在本地
								if (mBox.isChecked()) {
									PreferencesUtils
											.setShowNetTips(LoadingActivity.this);
								}
								requestClient();
							}
						})
				.setNegativeButton(getString(R.string.exit),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).setCancelable(false).show();
	}

	/**
	 * 检查是否有网络连接
	 */
	private boolean checkNetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return (info != null && info.isAvailable());
	}

	@Override
	public void onSuccess(String url, Object obj) {
		if (obj != null) {
			if (url.equals(Urls.IP_ADDRESS)) {
				IpPortModel ipm = (IpPortModel) obj;
				AppContext.portModel = ipm;
				getClient().getBuyedFromShop(Urls.CONFIRM_BUY_FROM_SHOP);
			} else if (url.equals(Urls.GOODS_INFO)) {
				dismissLoading();
				List<SecureModel> list = (List<SecureModel>) obj;
				Bundle bundle = new Bundle();
				bundle.putSerializable(DoovConstants.GOODS_A, list.get(0));
				bundle.putSerializable(DoovConstants.GOODS_B, list.get(1));
				startActivityTo(bundle, SelectPackageActivity.class);
				finish();
			} else if (url.equals(Urls.CHECK_REG)) {
				regmodel = (DoovRegModel) obj;
				if (!TextUtils.isEmpty(regmodel.getActivate())) {
					AppContext.isActivate = 1;
				} else {
					AppContext.isActivate = 0;
				}
				// 获取IP和端口号
				getClient().getIPandPort(Urls.IP_ADDRESS);
			} else if (url.equals(Urls.CONFIRM_BUY)) {
				cancelNotification();
				BuyedModel m = (BuyedModel) obj;
				String state = m.getVerifyStatus();
				String memberid = m.getInsuranceNO();
				int type = 0;				
				Bundle bundle = new Bundle();
				if (state.equals("SUCCESS")) {// 审核成功
					type = 2;
				} else if (state.equals("FAIL")) {// 审核中
					type = 1;
				} else if (state.equals("NOPASS")) {// 审核失败
					type = 3;
				}
				type = 2;//lijuan add for confirm success at 20151229
				m.setType(type);
				PreferencesUtils.setBuyModel(this, m);
				PreferencesUtils.setBuyed(this);
				//lijuan add for one year tip at 20151225
				long buyTime = -1;
				String buytime = m.getBuyTime();
				LogUtil.i("lijuan", "[CONFIRM_BUY] buytime = " + buytime);
				if (!TextUtils.isEmpty(buytime)) {
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					try {
						buyTime = sf.parse(buytime).getTime();
						LogUtil.i("lijuan", "[CONFIRM_BUY] buyTime = " + buyTime);
						PreferencesUtils.setBuyTime(this, buyTime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				if (isBuyedOverOneYear(buyTime)) {
					startActivityTo(OverOneYearActivity.class);											
				} else {				
					bundle.putInt("type", type);
					bundle.putString("memberid", memberid);
					startActivityTo(bundle, ConfirmedActivity.class);				
				}
				//end lijuan
				finish();
			} else if (url.equals(Urls.CONFIRM_BUY_FROM_SHOP)) {
				cancelNotification();
				BuyedModel m = (BuyedModel) obj;
				String memberid = m.getInsuranceNO();
				Bundle bundle = new Bundle();
				PreferencesUtils.setBuyed(this);
				m.setType(2);
				PreferencesUtils.setBuyModel(this, m);
				//lijuan add for one year tip at 20151225
				long buyTime = -1;
				String buytime = m.getBuyTime();
				LogUtil.i("lijuan", "[BUY_FROM_SHOP] buytime = " + buytime);
				if (!TextUtils.isEmpty(buytime)) {
					LogUtil.i("lijuan", "[BUY_FROM_SHOP] buytime is not empty");
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					try {
						buyTime = sf.parse(buytime).getTime();
						LogUtil.i("lijuan", "[BUY_FROM_SHOP] buyTime = " + buyTime);
						PreferencesUtils.setBuyTime(this, buyTime);
					} catch (ParseException e) {
						LogUtil.i("lijuan", "[BUY_FROM_SHOP] ParseException");
						e.printStackTrace();
					}
				}
				if (isBuyedOverOneYear(buyTime)) {
					startActivityTo(OverOneYearActivity.class);											
				} else {
					bundle.putInt("type", 2);
					bundle.putString("memberid", memberid);
					startActivityTo(bundle, ConfirmedActivity.class);				
				}
				//end lijuan
				finish();
			}
		} else {
			dismissLoading();
			Toast.makeText(this, getString(R.string.web_error_tips),
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	/**
	 * 取消通知栏提醒
	 */
	private void cancelNotification() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(DoovSecureReceiver.notifyId);// 删除一个特定的通知ID对应的通知
	}

	@Override
	public void onFailure(String url, int errorCode) {
		if (url.equals(Urls.CONFIRM_BUY) && errorCode == 10004) {
			if (AppContext.isActivate == 1) {
				SimpleDateFormat sf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss.SSS");
				try {
					long startTime = sf.parse(regmodel.getActivate()).getTime();
					long sysTime = Long.parseLong(regmodel.getSys());
					long midtime = sysTime - startTime;
					int showDays = PreferencesUtils.getShowDays(this);
					//LogUtil.i("lijuan", "[LoadingActivity--onFailure] showDays = " + showDays);
					if (midtime > (long)1000 * 60 * 60 * 24 * showDays) {
						/*	
						Toast.makeText(this,
								getString(R.string.over_buy_time_tips),
								Toast.LENGTH_SHORT).show();
						sendBroadcast(new Intent(
								DoovConstants.DOOV_HIDE_ICON_ACTION));
						*/
						startActivityTo(OverBuyTimeActivity.class);	
						finish();
						return;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			getClient().getGoodsInfo(Urls.GOODS_INFO);
		} else if (url.equals(Urls.CONFIRM_BUY_FROM_SHOP) && errorCode == 10004) {
			getClient().getIsBuyed(Urls.CONFIRM_BUY);
		} else {
			// Toast.makeText(this, getResString(R.string.net_error_tips),
			// Toast.LENGTH_SHORT).show();
			// finish();
			dismissLoading();
			Log.i("LoadingActivity", "url == " + url + "...." + "errorcode" + errorCode);
			showNetErrorDialog();
		}
	}

	/**
	 * 比较购买是否已经超过1年期限
	 */	
	private boolean isBuyedOverOneYear(long buytime) {
		LogUtil.i("lijuan", "[isBuyedOverOneYear] buytime = " + buytime);
		if (buytime == -1) {
			return false;
		}
		
		try {
			long systemTime = Long.parseLong(regmodel.getSys());
			LogUtil.i("lijuan", "[isBuyedOverOneYear] systemTime = " + systemTime);
			long overtime = systemTime - buytime;
			LogUtil.i("lijuan", "[isBuyedOverOneYear] overtime = " + overtime);
			LogUtil.i("ghshuai", "(long)1000 * 60 * 60 * 24 * 365 = " + (long)1000 * 60 * 60 * 24 * 365);
			if (overtime > (long)1000 * 60 * 60 * 24 * 365) {				
				return true;
			} else {
				return false;
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
			
	/**
	 * lijuan add for invalid imei tips at 20160111
	 */
	private void showInvalidIMEITips() {
		new AlertDialog.Builder(this)
				.setMessage(getString(R.string.invalid_imei_tips))
				.setNegativeButton(getString(R.string.ok),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).setCancelable(false).show();
	}	
	
	/**
	 * 获取imei号
	 */
	/*private String getIMEI() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			String imei1 = tm.getDeviceId(0);
			if ((imei1 == null) || imei1.equals("") || (imei1.length() < 15)) {
				String imei2 = tm.getDeviceId(1);
				if ((imei2 == null) || imei2.equals("") || (imei2.length() < 15)) {
					String imei = tm.getDeviceId();
					if (imei == null) {
						return "000000000000000";
					} else if (imei.equals("") || imei.length() < 15){
						return "000000000000000";						
					} else if (imei.length() > 15) {
						imei = imei.substring(imei.length() - 15);
					}
					return imei;					
				} else if (imei2.length() > 15) {
					imei2 = imei2.substring(imei2.length() - 15);
				}
				return imei2;
			} else if (imei1.length() > 15) {
				imei1 = imei1.substring(imei1.length() - 15);
			}
			return imei1;
		} else {
			return "000000000000000";
		}
	}*/	
	
	/*
	 * add by huangjiawei at 2016/11/3 begin
	 */
	public String getIMEI(Context context) {
		String[] mImeiArray = null;
		String imei = "000000000000000";
	    Intent intent = context.registerReceiver(null, new IntentFilter("intent_action_imei_meid"));
	    Log.i(TAG, "[initImeiAndMeid] intent is " + intent);
	    if (intent != null) {
//	        mMeid = intent.getStringExtra("extra_key_meid");
	        mImeiArray = intent.getStringArrayExtra("extra_key_imei");
//	        Log.i(TAG, "[initImeiAndMeid] mMeid is " + mMeid);
	        if (mImeiArray != null) {
        Log.i(TAG, "[initImeiAndMeid] IMEI length is " + mImeiArray.length);
//	            for (int i = 0; i < mImeiArray.length; ++i) {
//	                Log.i(TAG, "[initImeiAndMeid] IMEI[" + i + "] is " + mImeiArray[i]);
//	            }
	            imei = mImeiArray[0];
	            Log.i(TAG, "[initImeiAndMeid] IMEI[0] is " + imei);
	        } else {
	            Log.i(TAG, "[initImeiAndMeid] IMEI array is " + null);
	        }
	    }
	    return imei;
	}

	/*
	 * add by huangjiawei at 2016/11/3 end
	 */
}
