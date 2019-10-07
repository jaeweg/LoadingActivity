package com.doov.secure.comm.net;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.doov.secure.base.AppContext;
import com.doov.secure.model.AlipayOrderInfo;
import com.doov.secure.model.BuyedModel;
import com.doov.secure.model.CardpayOrderInfo;
import com.doov.secure.model.DoovRegModel;
import com.doov.secure.model.InsuranceModel;
import com.doov.secure.model.IpPortModel;
import com.doov.secure.model.SecureModel;
import com.doov.secure.model.WXpayOrderInfo;
import com.doov.secure.model.ServerTimeModel;
import com.doov.secure.wxapi.Constants;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.doov.secure.comm.utils.LogUtil;

public class NetClient {
	private static final String TAG = NetClient.class.getSimpleName();
	private static final String LONGITUDE = "Longitude";
	private static final String LATITUDE = "Latitude";
	private static final String GOODSTYPE = "GoodsType";
	private static final String IP = "IP";
	private static final String PORT = "Port";
	private static final String GOODSID = "GoodsID";
	private static final String USERNAME = "UserName";
	private static final String IDCARD = "IdCard";
	private static final String BUYTIME = "BuyTime";
	private static final String ISACTIVE = "IsActive";
	private static final String GOODSNAME = "GoodsName";
	private static final String GOODSDETAIL = "GoodsDetail";
	private static final String MEMBERID = "InsuranceNO";
	private static final String OSVERSION = "OsVersion";
	private static final String TOTALFEE = "TotalFee";
	private static final String GOODSBODY = "GoodsBody";
	private static final String TRADENO = "TradeNO";
	private static final String TRANSACTIONID = "TransactionID";
	private static final String PUBLICID = "PublicID";
	private static final String MCHID = "MchID";
	private static final String ACCOUNTS = "Accounts";
	private static final String SUBTYPE = "subtype";
	private static final int goodsType = 7;
	private static final int subType = 2; // doos --> 2 , aliDoos --> 3
	private static final int latitude = 0;
	private static final int longitude = 0;
	private AsyncHttpClient client;
	private INetClient mCallback;
	private Context mContext;
	private static final String PAYMENTCODE = "PaymentCode";
	private static final String INSURANCENO = "InsuranceNO";
	

	public NetClient(Context context, INetClient callback) {
		mCallback = callback;
		mContext = context;
		client = new AsyncHttpClient();
		
	}
	public void cancel(){
		client.cancelRequests(mContext, true);
	}
	/**
	 * 检查是否激活销量统计
	 * 
	 * @param context
	 * @param url
	 */
	public void checkIsReg(final String url) {
//		TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		/*
		String imei = telephonyManager.getDeviceId();
		if (TextUtils.isEmpty(imei)) {
			imei = "000000000000000";
		}
		*/
		/*String imei = telephonyManager.getDeviceId(0);
		if ((imei == null) || imei.equals("") || (imei.length() < 15)) {
			imei = telephonyManager.getDeviceId(1);
			if ((imei == null) || imei.equals("") || (imei.length() < 15)) {
				imei = telephonyManager.getDeviceId();
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
		}*/		
		/*
		 * add by huangjiawei at 2016/11/3 begin
		 */
		String[] mImeiArray = null;
		String imei = "000000000000000";
	    Intent intent = mContext.registerReceiver(null, new IntentFilter("intent_action_imei_meid"));
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
	    /*
		 * add by huangjiawei at 2016/11/3 end
		 */
		LogUtil.i("lijuan", "[checkIsReg] imei = " + imei);
		HttpEntity entity = null;
		try {
			entity = new StringEntity(imei + ";" + android.os.Build.DEVICE + ";" + Build.DISPLAY + ";");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post(url, entity, new HttpHandler(url) {

			@Override
			public void resultOk(String content) {
				DoovRegModel obj = DoovRegModel.json2Obj(content);
				mCallback.onSuccess(url, obj);
			}
		});
	}

	/**
	 * 获取IP和端口号
	 */
	public void getIPandPort(final String url) {
		RequestParams params = MobileInfo.getInstance(mContext).getParams();
		params.put(LONGITUDE, String.valueOf(longitude));
		params.put(LATITUDE, String.valueOf(latitude));
		get(url, params, new HttpHandler(url) {
			@Override
			public void resultOk(String content) {
				IpPortModel obj = IpPortModel.json2Obj(content);
				mCallback.onSuccess(url, obj);
			}
		});
	}

	/**
	 * 获取无忧险套餐信息
	 */
	public void getGoodsInfo(final String url) {
		RequestParams params = MobileInfo.getInstance(mContext).getParams();
		params.put(GOODSTYPE, String.valueOf(goodsType));
		params.put(IP, AppContext.portModel.getIP());
		params.put(PORT, AppContext.portModel.getPort());
		params.put(GOODSID, String.valueOf(0));
		get(url, params, new HttpHandler(url) {
			@Override
			public void resultOk(String content) {
				List<SecureModel> obj = SecureModel.json2Obj(content);
				mCallback.onSuccess(url, obj);
			}
		});
	}

	/**
	 * 获取是否已经购买无忧险接口
	 */
	public void getIsBuyed(final String url) {
		RequestParams params = MobileInfo.getInstance(mContext).getParams();
		params.put(GOODSTYPE, String.valueOf(goodsType));
		params.put("Page", String.valueOf(1));
		params.put("CountsPerPage", String.valueOf(1));
		get(url, params, new HttpHandler(url) {
			@Override
			public void resultOk(String content) {
				//Log.i("TAG", content+"yyyy");
				BuyedModel obj = BuyedModel.json2Obj(content);
				mCallback.onSuccess(url, obj);
			}
		});
	}

	/**
	 * 获取是否在商城已经购买无忧险接口
	 */
	public void getBuyedFromShop(final String url) {
		RequestParams params = MobileInfo.getInstance(mContext).getParams();
		get(url, params, new HttpHandler(url) {
			@Override
			public void resultOk(String content) {
				BuyedModel obj = BuyedModel.json2Obj(content);
				mCallback.onSuccess(url, obj);
			}
		});
	}
	
	
	/**
	 * 提交用户信息，生成会员号
	 * 
	 * @param url
	 * @param username
	 *            姓名
	 * @param mobile
	 *            手机号
	 * @param IdCard
	 *            身份证号
	 */
	public void postUserInfo(final String url, String username, String mobile, String IdCard, String buytime) {
		if(AppContext.secureModel != null){
		RequestParams params = MobileInfo.getInstance(mContext).getParams();
		params.put(USERNAME, username);
		params.put(IDCARD, IdCard);
		params.put(BUYTIME, buytime);
		params.put(MobileInfo.QUERYMOBILE, mobile);
		params.put(ISACTIVE, String.valueOf(AppContext.isActivate));
		params.put(GOODSID, AppContext.secureModel.getGoodsID());
		params.put(OSVERSION, Build.DISPLAY);
		get(url, params, new HttpHandler(url) {
			@Override
			public void resultOk(String content) {
				InsuranceModel obj = InsuranceModel.json2Obj(content);
				mCallback.onSuccess(url, obj);
			}
		});
		}
	}

	/**
	 * 支付宝订单查询
	 * 
	 * @param url
	 * @param goodsId
	 * @param goodsType
	 */
	public void getAlipayOrder(final String url, String goodsId, String goodsName, String memberid) {
		RequestParams params = MobileInfo.getInstance(mContext).getParams();
		params.put(GOODSTYPE, String.valueOf(goodsType));
		params.put(SUBTYPE, String.valueOf(subType));// add by huangjiwei
		params.put(LONGITUDE, String.valueOf(longitude));
		params.put(LATITUDE, String.valueOf(latitude));
		params.put(GOODSID, goodsId);
		params.put(GOODSNAME, goodsName);
		params.put(MEMBERID, memberid);
		get(url, params, new HttpHandler(url) {
			@Override
			public void resultOk(String content) {
				AlipayOrderInfo obj = AlipayOrderInfo.json2Obj(content);
				mCallback.onSuccess(url, obj);
			}
		});
	}

	/**
	 * 微信支付订单查询
	 */
	public void getWXpayOrder(final String url,String memberid) {
		if(AppContext.secureModel != null){
		RequestParams params = MobileInfo.getInstance(mContext).getParams();
		params.put(MobileInfo.PAYPROVIDER, String.valueOf(1));
		params.put(LONGITUDE, String.valueOf(longitude));
		params.put(LATITUDE, String.valueOf(latitude));
		params.put(GOODSTYPE, String.valueOf(goodsType));
		params.put(SUBTYPE, String.valueOf(subType));
		params.put(ACCOUNTS, String.valueOf(0000000));
		params.put(MEMBERID, memberid);
		params.put(GOODSID, AppContext.secureModel.getGoodsID());
		params.put(GOODSDETAIL, AppContext.secureModel.getGoodsDesc());
		params.put(TOTALFEE, AppContext.secureModel.getGoodsPrice());
		params.put(GOODSBODY, AppContext.secureModel.getGoodsName());
		get(url, params, new HttpHandler(url) {
			@Override
			public void resultOk(String content) {
				WXpayOrderInfo obj = WXpayOrderInfo.json2Obj(content);
				mCallback.onSuccess(url, obj);
			}
		});
		}
	}
	/**
	 * 支付卡支付订单查询
	 */
	public void getCardpayOrder(final String url,String PaymentCode,String memeberId,String userPhoneNO){
		RequestParams params = MobileInfo.getInstance(mContext).getParams_cardpay(userPhoneNO);
		//支付卡业务参数请求
		params.put(GOODSTYPE, String.valueOf(goodsType));
		params.put(GOODSID, AppContext.secureModel.getGoodsID());//服务器回传参数
		params.put(LONGITUDE, "110");//经度参数
		params.put(LATITUDE, "110");//纬度参数
		params.put(INSURANCENO, memeberId);//朵唯会员卡号
		params.put(PAYMENTCODE, PaymentCode);
		//Log.i(TAG,"yhw RequestParams:------"+params);
		get(url,params,new HttpHandler(url) {
				@Override
				public void resultOk(String content) {
					//Log.i(TAG,"yhw resultOk-------- "+content.toString());
						
						CardpayOrderInfo cardpayobj = CardpayOrderInfo.json2Obj(content);
						mCallback.onSuccess(url, cardpayobj);//回调mCallback这个对象对方法onSuccess的实现
					}
			});
	}
	/**
	 * 获取微信支付结果
	 */
	public void getWXpayResult(final String url, String tradeno, String transactionid) {
		if(AppContext.secureModel != null){
		RequestParams params = MobileInfo.getInstance(mContext).getParams();
		params.put(GOODSID, AppContext.secureModel.getGoodsID());
		params.put(TRADENO, tradeno);
		params.put(GOODSTYPE, String.valueOf(goodsType));
		params.put(SUBTYPE, String.valueOf(subType));
		params.put(ACCOUNTS, String.valueOf(0000000));
		get(url, params, new HttpHandler(url) {
			@Override
			public void resultOk(String content) {
				try {
					JSONObject json = new JSONObject(content);
					String result = json.optString("return_code", "");
					mCallback.onSuccess(url, "SUCCESS".equals(result));
				} catch (JSONException e) {
					mCallback.onSuccess(url, false);
					e.printStackTrace();
				}
			}
		});
		}
	}

	void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		if (url.startsWith("/")) {
			url = AppContext.portModel.toString() + url;
		}

		client.post(url, params, responseHandler);
	}

	void post(String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
		client.post(mContext, url, entity, "application/x-www-form-urlencoded", responseHandler);
	}

	void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		if (url.startsWith("/")) {
			// lijuan modify at 20160405
			if (AppContext.portModel != null) {
				url = AppContext.portModel.toString() + url;
			}
		}
//		Log.e("zwy", "params:" + params.toString());
		client.get(url, params, responseHandler);
	}

	public abstract class HttpHandler extends AsyncHttpResponseHandler {
		private String mUrl;

		public HttpHandler(String url) {
			mUrl = url;
		}

		public abstract void resultOk(String content);

		@Override
		@Deprecated
		public void onSuccess(String content) {
		//	Log.i("zwytest","response huidiao success");
			if (content.contains("errorcode")) {
				content = content.substring(1, content.length() - 1);
				try {
					JSONObject json = new JSONObject(content);
					int code = json.getInt("errorcode");
					mCallback.onFailure(mUrl, code);
				} catch (JSONException e) {
					e.printStackTrace();
					mCallback.onFailure(mUrl, 0);
				}

//				Log.e("zwy", mUrl + "   failure:   " + content);
			} else {
			//	Log.i("zwy"," AsyncHttpResponseHandler success");
				resultOk(content);
//				Log.e("zwy", "success:" + content);
			}
			super.onSuccess(content);
		}

		@Override
		@Deprecated
		public void onFailure(int statusCode, Throwable error, String content) {
			mCallback.onFailure(mUrl, statusCode);
//			Log.e("zwy", "error code :" + statusCode + "  error " + error);
			super.onFailure(statusCode, error, content);
		}
	}
	
	/**
	 * 获取服务器上系统时间和无忧险购买过期时间
	 */
	public void getServerTime(final String url) {
		//LogUtil.i("lijuan", "getServerTime, url = " + url);
		if (AppContext.portModel != null) {
			RequestParams params = MobileInfo.getInstance(mContext).getParams();
			get(url, params, new HttpHandler(url) {
				@Override
				public void resultOk(String content) {
					ServerTimeModel obj = ServerTimeModel.json2Obj(content);
					mCallback.onSuccess(url, obj);
				}
			});
		}
	}		
}
