package com.doov.secure.wxapi;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONException;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.doos.secure.R;
import com.doov.secure.base.AppContext;
import com.doov.secure.comm.alipay.Result;
import com.doov.secure.comm.eventbus.BusEvent;
import com.doov.secure.comm.net.MobileInfo;
import com.doov.secure.comm.net.Urls;
import com.doov.secure.comm.utils.DoovConstants;
import com.doov.secure.comm.utils.LogUtil;
import com.doov.secure.comm.utils.PreferencesUtils;
import com.doov.secure.model.AlipayOrderInfo;
import com.doov.secure.model.BuyedModel;
import com.doov.secure.model.CardpayOrderInfo;
import com.doov.secure.model.SecureModel;
import com.doov.secure.model.WXpayOrderInfo;
import com.doov.secure.receiver.DoovSecureReceiver;
import com.doov.secure.ui.activity.CardPaySubmitActivity;
import com.doov.secure.ui.activity.ConfirmedActivity;
import com.doov.secure.ui.base.BaseActivity;
import com.doov.secure.ui.dialog.SelectPayTypeDialog;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelbase.BaseResp.ErrCode;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

// lijuan add for DoovReg at 20151224
import android.content.SharedPreferences;
import com.doov.register.DoovRegGenInfo;
import com.doov.register.DoovRegHttp;
import com.doov.register.DoovRegConst;
// ghshuai 去掉百度定位，换GPS定位 --begin
/*import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;*/
// ghshuai 去掉百度定位，换GPS定位 --end
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WXPayEntryActivity extends BaseActivity implements
		IWXAPIEventHandler {
	private static final String PAY_SUCCESS = "9000";
	private static final String PAY_FAIL = "4000";
	private static final String PAY_CANCEL = "6001";
	private TextView memberid, username, cardid, imei;
	private String memeberId;
	private WXpayOrderInfo mWxinfo;
	private RelativeLayout web_error;
	private IWXAPI mWxApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
	// lijuan add for DoovReg at 20151224
	public static String PREFERENCE_FILE = "config_reg";
	private static String REG_STATE = "reg_state";
	
	private DoovRegHttp doovRegHttp = null;
	private DoovRegGenInfo doovRegGen = null;
	private static boolean waitting = false;
	
	// ghshuai 去掉百度定位，换GPS定位 --begin
	//public LocationClient locationClient;
	//private MyLocationListener myLocationListener;
	private LocationManager locationManager;
	private Location location;
	private Criteria criteria;
	private SelectPayTypeDialog mSelectPayTypeDialog;
	// ghshuai 去掉百度定位，换GPS定位 --end
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Result payResult = new Result((String) msg.obj);
			payResult.parseResult();
			String status = payResult.getResultStatus();
			if (status != null && status.equals(PAY_SUCCESS)) {
				cancelNotification();
				showLoading();
				getClient().getIsBuyed(Urls.CONFIRM_BUY);
			} else if (status != null && status.equals(PAY_CANCEL)) {

			} else {
				toastError();
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBodyView(R.layout.activity_pay);
		initView();
		initData();
		mWxApi.handleIntent(getIntent(), this);

		// lijuan add for DoovReg at 20151224
		doovRegHttp = new DoovRegHttp(this);
		doovRegGen = DoovRegGenInfo.getInstance(this);
		
		// ghshuai 去掉百度定位，换GPS定位 --begin
/*		locationClient = new LocationClient(this);
		myLocationListener = new MyLocationListener();
		locationClient.registerLocationListener(myLocationListener);*/
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		
		// ghshuai 去掉百度定位，换GPS定位 --end
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		mWxApi.handleIntent(intent, this);
	}

	/**
	 * 取消通知栏的无忧险
	 */
	private void cancelNotification() {
		PreferencesUtils.setBuyed(this);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(DoovSecureReceiver.notifyId);// 删除一个特定的通知ID对应的通知
	}

	/**
	 * 初始化组件
	 */
	private void initData() {
		Resources res = getResources();
		Bundle bundle = getIntent().getExtras();
		setTitleText(R.string.page_pay_info_title);
		setNextText(R.string.page_pay_paybtn_text);
		memeberId = bundle.getString(DoovConstants.MEMBER_ID);
//		memberid.setText(res.getString(R.string.page_pay_memberid_text)
//				+ memeberId);
		username.setText(res.getString(R.string.page_pay_name_text)
				+ bundle.getString(DoovConstants.USERNAME));
		cardid.setText(res.getString(R.string.page_pay_cardid_text)
				+ bundle.getString(DoovConstants.IDCARD));
		imei.setText(res.getString(R.string.page_pay_imei_text)
				+ MobileInfo.getInstance(this).getIMEI(this));
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
//		memberid = (TextView) findViewById(R.id.memberid);
		username = (TextView) findViewById(R.id.username);
		cardid = (TextView) findViewById(R.id.cardid);
		imei = (TextView) findViewById(R.id.imei);
		web_error = (RelativeLayout) findViewById(R.id.web_error);
		setWebErrorPage(false);
	}

	@Override
	public void skipToNext() {
		// ghshuai 去掉百度定位，换GPS定位 --begin
		//new SelectPayTypeDialog(this).show();
		mSelectPayTypeDialog = new SelectPayTypeDialog(this);
		mSelectPayTypeDialog.show();
		// lijuan add for DoovReg at 20151224
		Log.d("DoovReg", "skipToNext:  isActivate = " + AppContext.isActivate);
		if (AppContext.isActivate != 1) {
			startLocationForGPS();
		}
		// ghshuai 去掉百度定位，换GPS定位 --end
	}

	@Override
	protected void onResume() {
		showLoading();
		getClient().getIsBuyed(Urls.CONFIRM_BUY);
		super.onResume();
	}
	
	@Override
	public void onEventMainThread(BusEvent event) {
		super.onEventMainThread(event);
		switch (event.getAction()) {
		case BusEvent.USE_ALIPAY:
			showLoading();
			if(AppContext.secureModel != null){
			SecureModel sModel = AppContext.secureModel;
			showLoading();
			getClient().getAlipayOrder(Urls.ALIPAY_ORDER, sModel.getGoodsID(),
					sModel.getGoodsName(), memeberId);
			}
			break;
		case BusEvent.USE_WXPAY:
			if (!mWxApi.isWXAppInstalled()) {
				Toast.makeText(WXPayEntryActivity.this, getString(R.string.no_wx_app),
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				if (!mWxApi.isWXAppSupportAPI()) {
					Toast.makeText(WXPayEntryActivity.this,
							getString(R.string.wx_version_low_tips), Toast.LENGTH_SHORT).show();
					return;
				}
			}
			showLoading();
			getClient().getWXpayOrder(Urls.WXPAY_ORDER, memeberId);
			break;
		case BusEvent.USE_PAYCARD:
			//TODO 处理支付卡的操作,注意需要将会员卡号和用户手机号码存在bundle中
			Bundle bundle = getIntent().getExtras();
			
			Intent intent = new Intent(this,CardPaySubmitActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);
		if (obj != null) {
			Log.d("ghshuai","url==="+url);
			if (url.equals(Urls.ALIPAY_ORDER)) {
				AlipayOrderInfo orderinfo = (AlipayOrderInfo) obj;
				try {
					startAlipay(orderinfo.parseOrderParamToAlipayQuery());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (url.equals(Urls.WXPAY_ORDER)) {
				WXpayOrderInfo wxinfo = (WXpayOrderInfo) obj;
				mWxinfo = wxinfo;
				startWXpay(wxinfo);
			} else if (url.equals(Urls.CONFIRM_BUY)) {
				getBuyState(obj);
			} else if (url.equals(Urls.WXPAY_RESULT)) {
				boolean result = (Boolean) obj;
				if (result) {
					showLoading();
					getClient().getIsBuyed(Urls.CONFIRM_BUY);
				} else {
					toastError();
				}
			}
		} else{
			//网络连接成功后,判断是支付卡支付的接口地址
			//将网络请求的成功回调,页面移动到 自己定义的CardPaySubmitActivity界面
		}

	}

	/**
	 * 获取购买审核状态
	 */
	private void getBuyState(Object obj) {
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
		//lijuan add for one year tip at 20151225
		String buytime = m.getBuyTime();
		Log.d("lijuan", "[WXPAY-getBuyState] buytime = " + buytime);
		if (TextUtils.isEmpty(buytime)) {
			PreferencesUtils.setBuyTime(this, -1);
		} else {					
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			try {
				long buyTime = sf.parse(buytime).getTime();
				PreferencesUtils.setBuyTime(this, buyTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}					
		//end lijuan		
		PreferencesUtils.setBuyModel(this, m);
		bundle.putInt("type", type);
		bundle.putString("memberid", memberid);
		startActivityTo(bundle, ConfirmedActivity.class);
		notifyClose();
	}

	@Override
	public void onFailure(String url, int errorCode) {
		super.onFailure(url, errorCode);
		if (url.equals(Urls.WXPAY_RESULT) || url.equals(Urls.CONFIRM_BUY)) {
			if (errorCode != 10004) {
				setWebErrorPage(true);
			}
		} else {
			Toast.makeText(
					this,
					getResources()
							.getString(R.string.page_write_net_error_tips),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 跳转微信支付
	 */
	private void startWXpay(WXpayOrderInfo wxinfo) {
		PayReq req = new PayReq();
		req.appId = wxinfo.getAppid();
		req.partnerId = wxinfo.getPartnerid();
		req.prepayId = wxinfo.getPrepayid();
		req.nonceStr = wxinfo.getNoncestr();
		req.packageValue = wxinfo.getPackages();
		req.timeStamp = wxinfo.getTimestamp();
		req.sign = wxinfo.getSign();
		mWxApi.registerApp(Constants.APP_ID);
		mWxApi.sendReq(req);
	}

	/**
	 * 跳转支付宝支付
	 */
	private void startAlipay(final String payInfo) {
		Runnable payRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(WXPayEntryActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);
				Message msg = new Message();
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		new Thread(payRunnable).start();
	}

	@Override
	public void onReq(BaseReq arg0) {
	}

	@Override
	public void onResp(BaseResp arg0) {
		if (arg0.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			Log.d("ghshuai","arg0.errCode==="+arg0.errCode);
			if (arg0.errCode == ErrCode.ERR_OK) {
				cancelNotification();
				// 支付成功
				showLoading();
				getClient().getWXpayResult(Urls.WXPAY_RESULT,
						mWxinfo.getTrade_no(), mWxinfo.getPrepayid());
			} else if (arg0.errCode == ErrCode.ERR_USER_CANCEL) {
				// 用户取消
			} else {
				// 其余都归纳为支付失败
				toastError();
			}
		}
	}

	/**
	 * 设置是否显示网络异常页面
	 */
	private void setWebErrorPage(boolean show) {
		web_error.setVisibility(show ? View.VISIBLE : View.GONE);
		setBtnVisible(show ? View.GONE : View.VISIBLE);
		isCloseAll = show;
	}

	public void refresh(View v) {
		showLoading();
		if(mWxinfo!=null){
			getClient().getWXpayResult(Urls.WXPAY_RESULT, mWxinfo.getTrade_no(),
					mWxinfo.getPrepayid());
		}
	}

	/**
	 * 弹出支付失败
	 */
	private void toastError() {
		Toast.makeText(this, getResources().getString(R.string.pay_error),
				Toast.LENGTH_SHORT).show();
	}
		
	/**
	 * lijuan add for DoovReg at 20151224	
	 */
	public void startLocation() {
		Log.d("DoovReg", "[startLocation]");
		
// ghshuai 去掉百度定位，换GPS定位 --begin
/*		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(DoovRegConst.LOCATION_MODE);
		option.setCoorType(DoovRegConst.COORD_TYPE);
		option.setScanSpan(0);
		option.setNeedDeviceDirect(false);
		option.setIsNeedAddress(false);
		locationClient.setLocOption(option);

		locationClient.start();*/
		String bestProvider = locationManager.getBestProvider(getCriteria(), true);
        Log.d("ghshuai", "bestProvider=="+bestProvider);       
        location=locationManager.getLastKnownLocation(bestProvider);     
        locationManager.requestLocationUpdates(bestProvider, 1000, 0,
                locationListener);
// ghshuai 去掉百度定位，换GPS定位 --end
	}

	// ghshuai 去掉百度定位，换GPS定位 --begin
/*	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			// TODO Auto-generated method stub
			if (arg0.getLocType() == BDLocation.TypeGpsLocation 
					|| arg0.getLocType() == BDLocation.TypeNetWorkLocation
					|| arg0.getLocType() == BDLocation.TypeCacheLocation
					|| arg0.getLocType() == BDLocation.TypeOffLineLocationNetworkFail
					|| arg0.getLocType() == BDLocation.TypeOffLineLocation) {
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putDouble("longitude", arg0.getLongitude());
				data.putDouble("latitude", arg0.getLatitude());
				msg.setData(data);
				msg.what = DoovRegConst.ACTION_LOCATION_RESULT;
				regHandler.sendMessage(msg);
				Log.d("DoovReg", "longitude:  "+arg0.getLongitude());
				Log.d("DoovReg", "latitude:  "+arg0.getLatitude());
			}
			else {
				waitting = false;
				Log.d("DoovReg", "baidu location error " + arg0.getLocType());
			}
			locationClient.stop();
			Log.d("DoovReg", "location client stop");
		}
	}	*/
	
    /**
     * 返回查询条件
     * @return
     */
    private Criteria getCriteria(){
    	criteria=new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细 
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);    
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费  
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求  
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
    
    /*
     * 如果GPS定位打开了则调用startLocation;否则跳转到GPS服务设置界面。
     */
    private void startLocationForGPS(){
    	if(locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
			startLocation();
		}else{
			if(mSelectPayTypeDialog.isShowing()){
				mSelectPayTypeDialog.dismiss();
			}
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.doov_gps_dialog_title));
		    builder.setMessage(getResources().getString(R.string.doov_gps_dialog_body));
		    builder.setPositiveButton(R.string.doov_gps_dialog_ok, new DialogInterface.OnClickListener() { 
		        @Override  
		        public void onClick(DialogInterface dialog, int which) { 
		        	dialog.dismiss();
		        	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			        startActivityForResult(intent,0);
		        }  
		    });  
		    builder.setNegativeButton(R.string.doov_gps_dialog_cancel, new DialogInterface.OnClickListener() {
		        @Override  
		        public void onClick(DialogInterface dialog, int which) {  
		            dialog.dismiss();
		        }  
		    }); 
			builder.create().show();
		}
    }
    
    /*
     * LocationListener监听器
     * 当位置发生改变时，会将最新的位置信息显示在界面上 
     */
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
        	if(location != null){
        		Message msg = new Message();
				Bundle data = new Bundle();
				data.putDouble("longitude", location.getLongitude());
				data.putDouble("latitude", location.getLatitude());
				msg.setData(data);
				msg.what = DoovRegConst.ACTION_LOCATION_RESULT;
				regHandler.sendMessage(msg);
				Log.d("ghshuai", "longitude:  "+location.getLongitude());
				Log.d("ghshuai", "latitude:  "+location.getLatitude());
				locationManager.removeUpdates(locationListener);
        	}
        }
 
        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub
        	Log.d("ghshuai", "======onProviderDisabled=====");
        }
 
        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub
        	Log.d("ghshuai", "======onProviderEnabled=====");
        }
 
        @Override
        public void onStatusChanged(String arg0, int status, Bundle extras) {
            // TODO Auto-generated method stub
            switch (status) {
            //GPS状态为可见时
            case LocationProvider.AVAILABLE:
                Log.i("ghshuai", "==AVAILABLE==");
                break;
            //GPS状态为服务区外时
            case LocationProvider.OUT_OF_SERVICE:
                Log.i("ghshuai", "==OUT_OF_SERVICE==");
                break;
            //GPS状态为暂停服务时
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.i("ghshuai", "==TEMPORARILY_UNAVAILABLE==");
                break;
            }
        }
 
    };    
	
	
	
	// ghshuai 去掉百度定位，换GPS定位 --end

	Handler regHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DoovRegConst.ACTION_LOCATION_RESULT:
				Bundle locationData = msg.getData();
				doovRegGen.longitude = locationData.getDouble("longitude");
				doovRegGen.latitude = locationData.getDouble("latitude");
				Log.d("DoovReg", "msg is ACTION_LOCATION_RESULT");

				new Thread(new Runnable() {
					@Override
					public void run() {
						Log.d("DoovReg", "handle message thread run");
						if ((doovRegGen.longitude != 0) && (doovRegGen.latitude != 0)) {
							Message msg = new Message();
							Bundle registerStatus = new Bundle();
							registerStatus.putString("regStatus",doovRegHttp.postReg());
							msg.setData(registerStatus);
							msg.what = DoovRegConst.ACTION_REGISTER_RESULT;
							regHandler.sendMessage(msg);
							Log.d("DoovReg", "send message ACTION_REGISTER_RESULT");
						}
					}
				}).start();
				break;
			case DoovRegConst.ACTION_REGISTER_RESULT:
				Bundle registerStatus = msg.getData();
				String regStatus = registerStatus.getString("regStatus");
				Log.d("DoovReg", "msg is ACTION_REGISTER_RESULT");
				Log.d("DoovReg", "Service return is " + regStatus);
				try {
					if (regStatus != null) {
						int type = Integer.parseInt(regStatus);
						Log.d("DoovReg", "regStatus != null ---type = " + type);
//						writeToSD(type);
						switch (type) {
						case 0:
							writeRegStatus(REG_STATE, type);
							break;
						case 1:
							writeRegStatus(REG_STATE, type);
							sendBroadcast(new Intent("com.doov.register.BEGINREG"));
							break;
						case 2:
							writeRegStatus(REG_STATE, type);
							break;
						default:
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					public void run() {
						// Toast.makeText(DoovRegService.this, "Register check",
						// Toast.LENGTH_LONG).show();
					}
				});
				waitting = false;
				break;
			}
			super.handleMessage(msg);
		}
	};	

	public void writeRegStatus(String key, int value) {
		Log.d("DoovReg", "writeRegStatus-- value = " + value);
		SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	

	
	/*
	private void startRegister() {
		Runnable regRunnable = new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				Bundle registerStatus = new Bundle();
		        registerStatus.putString("regStatus",doovRegHttp.postReg());
				msg.setData(registerStatus);
				//msg.what = DoovRegConst.ACTION_REGISTER_RESULT;
				//handler.sendMessage(msg);
			}
		};
		new Thread(regRunnable).start();
	}	
	*/
}
