package com.doov.secure.comm.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import com.doov.register.DoovRegConst;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MobileInfo {
	private static final String QUERYIP = "QueryIP";
	private static final String QUERYIMEI = "QueryIMEI";
	public static final String QUERYMOBILE = "QueryMobile";
	private static final String APPID = "AppID";
	private static final String APPENV = "AppEnv";
	private static final String CLIENTVERSION = "ClientVersion";
	private static final String ACCOUNTS = "Accounts";
	private static final String SIGNTYPE = "SignType";
	private static final String SIGN = "Sign";
	public static final String PAYPROVIDER = "PayProvider";
	private static final String MOBILEYPTE = "MobileType";
	private Context mContext;
	private static MobileInfo mobileInfo;
	private static final String TAG = "MobileInfo";

	private MobileInfo(Context context) {
		mContext = context;
	}

	public static MobileInfo getInstance(Context context) {
		if (mobileInfo == null) {
			mobileInfo = new MobileInfo(context);
		}
		return mobileInfo;
	}
	/**
	 * 无忧险订单支付 ,请求的基本参数
	 * */
	public RequestParams getParams() {
		RequestParams params = new RequestParams();
		params.put(QUERYIP, getLocalIpAddress());
		params.put(QUERYIMEI, getIMEI(mContext));
		params.put(QUERYMOBILE, getPhoneNumber());
		params.put(APPID, getAppID());
		params.put(APPENV, getAppENV());
		params.put(CLIENTVERSION, getAPPVersion());
		params.put(ACCOUNTS, getUserAccount());
		params.put(SIGNTYPE, getSignType());
		params.put(SIGN, getSign());
		params.put(PAYPROVIDER, getPayprovider());
		params.put(MOBILEYPTE, getMobileType());
		return params;
	}
	/**
	 * 无忧险订单_支付卡支付,添加额外的获取手机号码(这个号码是用户输入的号码)
	 * */
	public RequestParams getParams_cardpay(String userPhoneNO) {
		RequestParams params = new RequestParams();
		params.put(QUERYIP, getLocalIpAddress());
		params.put(QUERYIMEI, getIMEI(mContext));
		params.put(QUERYMOBILE, getPhoneNumber_cardpay(userPhoneNO));
		params.put(APPID, getAppID());
		params.put(APPENV, getAppENV());
		params.put(CLIENTVERSION, getAPPVersion());
		params.put(ACCOUNTS, getUserAccount());
		params.put(SIGNTYPE, getSignType());
		params.put(SIGN, getSign());
		params.put(PAYPROVIDER, getPayprovider());
		params.put(MOBILEYPTE, getMobileType());
		return params;
	}
	/**
	 * 获取IP地址
	 */
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return "0.0.0.0";
	}

	/**
	 * 获取imei号
	 */
	/*public String getIMEI() {
		if (mContext != null) {
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei1 = tm.getDeviceId(0);
			Log.i("lijuan", "getIMEI:: imei1 = " + imei1);
			if ((imei1 == null) || imei1.equals("") || (imei1.length() < 15)) {
				String imei2 = tm.getDeviceId(1);
				Log.i("lijuan", "getIMEI:: imei2 = " + imei2);
				if ((imei2 == null) || imei2.equals("")
						|| (imei2.length() < 15)) {
					String imei = tm.getDeviceId();
					Log.i("lijuan", "getIMEI:: imei = " + imei);
					if (imei == null) {
						return "000000000000000";
					} else if (imei.equals("") || imei.length() < 15) {
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

	/**
	 * 获取电话号码
	 * 
	 * @return
	 */
	public String getPhoneNumber() {
		String nb = null;
		if (mContext != null) {
			try {
				TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
				nb = tm.getLine1Number();
				if (nb == null) {
					return nb = "00000000001";
				} else if (nb.equals("") || nb.length() < 11) {
					return nb = "00000000001";
				} else if (nb.length() > 11) {
					nb = nb.substring(nb.length() - 11);
					return nb;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return nb;
		} else {
			return "00000000001";
		}
	}
	/**
	 * 获取电话号码
	 */
	public String getPhoneNumber_cardpay(String userPhoneNO) {
		String nb = null;
		if (mContext != null) {
			try {
				TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
				nb = tm.getLine1Number();
				if (nb == null) {
					return nb = userPhoneNO;
				} else if (nb.equals("") || nb.length() < 11) {
					return nb = "00000000001";
				} else if (nb.length() > 11) {
					nb = nb.substring(nb.length() - 11);
					return nb;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return nb;
		} else {
			return "00000000001";
		}
	}
	/**
	 * 获取进程名字
	 */
	private String getAppID() {
		return "DoovInsurance";
	}

	/**
	 * 获取系统版本号
	 */
	private String getAppENV() {
		return "system:android,version:" + android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 获取APP版本信息
	 */
	private String getAPPVersion() {
		try {
			PackageInfo pkgInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			return pkgInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0";
	}

	/**
	 * 获取账号
	 */
	private String getUserAccount() {
		return "SecureAccount";
	}

	/**
	 * 获取签名类型
	 */
	private String getSignType() {
		return "RSA";
	}

	/**
	 * 获取签名
	 */
	private String getSign() {
		return "1glihU9DPWee+UJ82u3+mw3Bdnr9u01at0M/xJnPsGuHh+JA5bk3zbWaoWhU6GmLab3dIM4JNdktTcEUI9/FBGhgfLO39BKX/eBCFQ3bXAmIZn4l26fiwoO613BptT44GTEtnPiQ6+tnLsGlVSrFZaLB9FVhrGfipH2SWJcnwYs=";
	}

	/**
	 * 获取支付信息
	 */
	private String getPayprovider() {
		return "0";
	}

	/**
	 * 获取手机型号
	 */
	private String getMobileType() {
//		return "DOOV_L5";
		return android.os.Build.MODEL.replace(" ", "_");
	}
}
