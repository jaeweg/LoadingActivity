package com.doov.register;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import com.doov.secure.base.AppContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

public class DoovRegGenInfo {
	private static final String TAG = "DoovReg";
	private Context mContext;
	public Double longitude;
	public Double latitude;
    private static DoovRegGenInfo mRegGenInfo;
    
	public static DoovRegGenInfo getInstance(Context context)
	{
		if(mRegGenInfo == null)
		{
			mRegGenInfo = new DoovRegGenInfo(context);
		}
		return mRegGenInfo;
	}
	
	private DoovRegGenInfo(Context context) {
		super();
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	/*public String getImei() {
		TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		//String imei = telephonyManager.getDeviceId();
		String imei = telephonyManager.getDeviceId(0);
		Log.d(TAG, "1--imei = " + imei);
		if ((imei == null) || imei.equals("") || (imei.length() < 15)) {
			imei = telephonyManager.getDeviceId(1);
			Log.d(TAG, "2--imei = " + imei);
			if ((imei == null) || imei.equals("") || (imei.length() < 15)) {
				imei = telephonyManager.getDeviceId();
				Log.d(TAG, "3--imei = " + imei);
				if ((imei == null) || imei.equals("") || (imei.length() < 15)) {
					imei = "000000000000000";
				} else if (imei.length() > 15) {
					imei = imei.substring(imei.length() - 15);
				}
			} else if (imei.length() > 15) {
				imei = imei.substring(imei.length() - 15);
			}
		} else if (imei.length() > 15) {
			imei = imei.substring(imei.length() - 15);
		}
		Log.d(TAG, "return IMEI " + imei);
        return imei;
	}*/
	
	/*
	 * add by huangjiawei at 2016/11/3 begin
	 */
	public String getImei(Context context) {
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
	
	public String getVersion() {
        Log.d(TAG, "return version " + Build.DISPLAY);
        return Build.DISPLAY;
	}
	
	public String getRelease() {
        Log.d(TAG, "return release " + Build.VERSION.RELEASE);
        return Build.VERSION.RELEASE;
	}
	
	public String getDisplayMetrics() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = mContext.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
	    String displayMetrics = screenWidth + "*" + screenHeight;
	    Log.d(TAG, "return displaymetrics " + displayMetrics);
	    return displayMetrics;
	}
	
	public String getOperatorName() {
		TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String operatorName = telephonyManager.getSimOperator();
		if (TextUtils.isEmpty(operatorName)) {
			operatorName="00000";
		}
		Log.d(TAG, "return operatorname " + operatorName);
		return operatorName;
	}

	public String getLocation() {
		NumberFormat nFormat = NumberFormat.getNumberInstance(); 
	    nFormat.setMaximumFractionDigits(DoovRegConst.LOCATION_POINT_MAX);
		Log.d(TAG, "return location " + nFormat.format(longitude) + "," + nFormat.format(latitude));
		return nFormat.format(longitude) + "," + nFormat.format(latitude);
	}

	public String getDataTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");     
		String datetime = sDateFormat.format(new java.util.Date()); 
		Log.d(TAG, "return datatime " + datetime);
		return datetime;
	}

	public String getNetwork() {
		String network = null;
		ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			network = info.getExtraInfo();
		}
		Log.d(TAG, "return network " + network);
		return network;
	}
	
	public String getPhoneType() {
        Log.d(TAG, "return phonetype " + DoovRegConst.DEFAULT_TYPE);
        return DoovRegConst.DEFAULT_TYPE;
	}
	
	/*
	 * add by huangjiawei at 2016/11/2 for match different type
	 */
	
	public String getPhoneTypeAndRAM(Context context) {
		String extratype = "Unknown";
	
		extratype = SystemProperties.get(DoovRegConst.EXTRA_TYPE, "");
		Log.d(TAG, "extratype = " + extratype);
		if (!"".contentEquals(extratype)){
			Log.d(TAG, "return ASD type");
			return DoovRegConst.DEFAULT_TYPE + "-" + extratype;
		}
		
        Log.d(TAG, "return phonetype " + DoovRegConst.DEFAULT_TYPE);
        Log.d(TAG, "return getRAM = " + getRAM(context));
        if ((getRAM(context) > 3)&&(getPhoneType().contains("L8"))){
        	Log.d(TAG, "return H type");
        	return DoovRegConst.DEFAULT_TYPE + "-H";
        }else if ((getTotalROM())&&(getPhoneType().contains("A5"))){
        	Log.d(TAG, "return H type");
        	return DoovRegConst.DEFAULT_TYPE + "-H";
        }else if ((getTotalROM())&&(getPhoneType().contains("M2"))){
        	Log.d(TAG, "return H type");
        	return DoovRegConst.DEFAULT_TYPE + "-H";        	
        }else if ((getRAM(context) > 1)&&(getPhoneType().contains("V3"))){
        	Log.d(TAG, "return H type");
        	return DoovRegConst.DEFAULT_TYPE + "-H";            	
        }else{
        	Log.d(TAG, "return normal type");
        	return DoovRegConst.DEFAULT_TYPE;
        }
	}
	
	public static int getRAM(Context context){
		return (int)Math.ceil(getTotalMemory(context));
	}

	private static float getTotalMemory(Context context){
	        String str1 = "/proc/meminfo";
	        String str2;
	        String[] arrayOfString;
	        float initial_memory = 0;  
	        try 
	        {
	            FileReader localFileReader = new FileReader(str1);
	            BufferedReader localBufferedReader = new BufferedReader(
	            localFileReader, 8192);
	            str2 = localBufferedReader.readLine();
	
	            arrayOfString = str2.split("\\s+");
	            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() ;
	            localBufferedReader.close();

	        } catch (IOException e) {
	        }
	        return initial_memory/(1024*1024);
	    }
	
	@SuppressLint("NewApi") 
	private static boolean getTotalROM() {//total_memory
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long getTotalBytes = stat.getTotalBytes();
        Log.d(TAG,"getTotalBytes =" + getTotalBytes);
//        long blockSize = stat.getBlockSize();
//        long availableBlocks = stat.getAvailableBlocks();
//        long totalRam = availableBlocks * blockSize;
//		Log.d(TAG,"setTotalMemory totalRam=" + totalRam);
		float total = getTotalBytes/1024f/1024f/1024f;
		Log.d(TAG,"setRunning totalRam=" +total );
		if(total<=16f){
			return false;
		}else{
			return true;
		}
	}
	/*
	 * add by huangjiawei at 2016/11/2 for match different type
	 */
	
}
