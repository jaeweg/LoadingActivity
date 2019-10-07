package com.doov.register;
	
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
//ghshuai 去掉百度定位，换GPS定位 --begin
//import com.baidu.location.LocationClientOption.LocationMode;
//ghshuai 去掉百度定位，换GPS定位 --end
public class DoovRegConst {
	/**
	 * actions
	 */
	public static final String ACTION_SMS_RECEIVED =
			"android.provider.Telephony.SMS_RECEIVED";
	
	public static final String ACTION_PHONE_STATE =
			TelephonyManager.ACTION_PHONE_STATE_CHANGED;
	
	public static final String ACTION_CONNECTIVITY =
			ConnectivityManager.CONNECTIVITY_ACTION;
	
	//data save
	public static final String REG_CONFIG_FILE = "config";
	public static final String SMS_NUM = "sms_num";
	public static final String CALL_TIME = "call_time";
	public static final String NET_SATUS = "net_status";
	public static final String REG_STATUS = "reg_status";
	public static final String REG_ALLOW = "reg_allow";
	public static final int NET_AVAILABLE_TRUE = 1;
	public static final int NET_AVAILABLE_FALSE = 0; 
	
	//condition
	public static final int SMS_NUM_MAX = 2; //2;
	public static final int CALL_TIME_MAX = 300; //300;
	public static final int LOCATION_POINT_MAX = 5;
	
	//provider define
	public static final Uri CALL_LOG_URI = CallLog.Calls.CONTENT_URI;
	public static final String CALL_LOG_DURATION = CallLog.Calls.DURATION;
	public static final String CALL_LOG_SORT_ORDER = CallLog.Calls.DEFAULT_SORT_ORDER;
	
	public static final Uri  SMS_URI_INBOX = Uri.parse("content://sms/inbox");
	public static final String SMS_ID = "_id";
	
	public static final String DEFAULT_TYPE = android.os.Build.DEVICE;
	public static final String EXTRA_TYPE = "ro.custom.modem";
	
	public static final String REG_URL_QIJI = "http://QiJi.tj.doov.cn:9090/api/ReceiveData_QiJi";
	public static final String REG_URL_QINGGUO = "http://QingGuo.tj.doov.cn:9090/api/ReceiveData_QingGuo";
	public static final String REG_URL_CUICAN = "http://L6.tj.doov.cn:9090/api/ReceiveData_L6";
	public static final String REG_URL_JIANAI = "http://L5M.tj.doov.cn:9090/api/ReceiveData_L5M";
	public static final String REG_URL_FEIYANG = "http://M1t.tj.doov.cn:9090/api/ReceiveData_M1t";
	public static final String REG_URL_XIATIAN = "http://A6.tj.doov.cn:9090/api/ReceiveData_A6";
	public static final String REG_URL_ANGEL = "http://Angel.tj.doov.cn:9090/api/ReceiveData_Angel";
	public static final String REG_URL_CHUANQI = "http://ChuanQi.tj.doov.cn:9090/api/ReceiveData_ChuanQi";
	public static final String REG_URL_QINGMU = "http://L8Plus.tj.doov.cn:9090/api/ReceiveData_L8Plus";
	public static final String REG_URL_QINGTIAN = "http://A5.tj.doov.cn:9090/api/ReceiveData_A5";
	public static final String REG_URL_ZHANFANG = "http://A8.tj.doov.cn:9090/api/ReceiveData_A8";
	public static final String REG_URL_FEIYUE = "http://M2.tj.doov.cn:9090/api/ReceiveData_M2";
	public static final String REG_URL_QINGXIN = "http://L9.tj.doov.cn:9090/api/ReceiveData_L9";
	public static final String REG_URL_FEIXIANG = "http://V3.tj.doov.cn:9090/api/ReceiveData_V3";
	public static final String REG_URL_ZHIAI = "http://L9_mini.tj.doov.cn:9090/api/ReceiveData_L9_mini";
	public static final String REG_URL_ANNIE = "http://A9.tj.doov.cn:9090/api/ReceiveData_A9";
	public static final String REG_URL_FEIWU_CP = "http://V5.tj.doov.cn:9090/api/ReceiveData_V5";
	public static final String REG_URL_FEIWU_OP = "http://V5C.tj.doov.cn:9090/api/ReceiveData_V5C";
	public static final String REG_URL_FEILONG = "http://V6.tj.doov.cn:9090/api/ReceiveData_V6";
	public static final String REG_URL_JIXIANG = "http://L520.tj.doov.cn:9090/api/ReceiveData_L520";
	public static final String REG_URL_LIANGXIA = "http://L525.tj.doov.cn:9090/api/ReceiveData_L525";
	public static final String REG_URL_XINDONG = "http://L925.tj.doov.cn:9090/api/ReceiveData_L925";
	
	public static final String COORD_TYPE = "bd09ll";
	// ghshuai 去掉百度定位，换GPS定位 --begin
	//public static final LocationMode LOCATION_MODE = LocationMode.Battery_Saving;
	// ghshuai 去掉百度定位，换GPS定位 --end
	public static final int ACTION_LOCATION_RESULT = 1;
	public static final int ACTION_REGISTER_RESULT = 2;
}
