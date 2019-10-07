package com.doov.secure.ui.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.doos.secure.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.doov.secure.comm.net.Urls;
import com.doov.secure.comm.utils.DoovConstants;
import com.doov.secure.comm.utils.LogUtil;
import com.doov.secure.comm.utils.Md5Utils;
import com.doov.secure.comm.utils.PreferencesUtils;
import com.doov.secure.model.BuyedModel;
import com.doov.secure.model.CardpayOrderInfo;
import com.doov.secure.model.InsuranceModel;
import com.doov.secure.ui.base.BaseActivity;
import com.doov.secure.ui.dialog.CardpayFailDialog;
import com.doov.secure.ui.view.DoovEditText;
import com.ta.utdid2.android.utils.SystemUtils;

public class CardPaySubmitActivity extends BaseActivity{
	private DoovEditText editPwd;
	private String cardPayPwd;
	private String encode_cardPayPwd;
	private String errorDesc;
	private String resultCode;
	private Bundle bundle;
	private String memberId;
	private String user_phone;
	private RelativeLayout web_error;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setBodyView(R.layout.activity_cardpay);
		initView();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showLoading();
		getClient().getIsBuyed(Urls.CONFIRM_BUY);
	}
	private void initView() {
		setTitleText(R.string.card_pay_writer_title);
		setNextText(R.string.card_pay_submit);
		editPwd= (DoovEditText) findViewById(R.id.card_pwd);
		editPwd.setOnFocusChangeListener(DoovEditText.CARDPAY_PWD);
		
	}

	public void skipToNext() {
		cardPayPwd = editPwd.getText().trim();
		//Log.i("TAGA", "skipToNext");
		if(cardPayPwd.length() == 0){
			Toast.makeText(this, "您的卡密码还没填写呢!",Toast.LENGTH_SHORT).show();
			return;
		}else{
			encode_cardPayPwd = Md5Utils.encode(cardPayPwd);
			//Log.i("TAG", "yhw MD5"+encode_cardPayPwd);
			//加载网络提示框
			showLoading();
			bundle = getIntent().getExtras();
			if(bundle != null && bundle.containsKey(DoovConstants.MEMBER_ID)){
				memberId = bundle.getString(DoovConstants.MEMBER_ID);
				user_phone = bundle.getString(DoovConstants.USERPHONGE);
				 //将用户填写的信息资料提交到服务器
				 getClient().getCardpayOrder(Urls.CARD_ORDER,encode_cardPayPwd,memberId,user_phone);
			}
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
	
	public void onSuccess(String url, Object obj) {
		super.onSuccess(url, obj);//回调父类的onSuccess方法,关闭dialog对话框
//		Log.i("TAG", "yhw onSuccess URL:"+url.toString());
		if(obj!=null){
			if(Urls.CONFIRM_BUY.equals(url)){
				getBuyState(obj);
			} else if(Urls.CARD_ORDER.equals(url)){
				
				CardpayOrderInfo cardpayobj = (CardpayOrderInfo)obj;
				errorDesc = cardpayobj.getDesc();
				resultCode = cardpayobj.getResult();
				
				if(resultCode.equals("SUCCESS")){
					//存储支付成功的状态
					PreferencesUtils.setBuyed(this);
					setNextText(R.string.card_pay_ok);
					//自动跳转到支付成功页面
					getClient().getIsBuyed(Urls.CONFIRM_BUY);
				}else if(resultCode.equals("ERROR")){
				     if(errorDesc.equals("NO THIS PAYMENT CODE")){
				    	 String str = "密码不对呀!";
				    	 new CardpayFailDialog(this,str).show();
				     }else if(errorDesc.equals("PAYMENT CODE IS USED")){
				    	 String str1 = "您的卡已被使用过啦!";
				    	 new CardpayFailDialog(this,str1).show();
				     }else if(errorDesc.equals("CARD TYPE ERROR")){
				    	 String str2 = "您的卡不能购买此保险";
				    	 new CardpayFailDialog(this,str2).show();
				     }else if(errorDesc.equals("PAYMENT CODE OVERDUE")){
				    	 String str3 = "您的卡已经过期啦!";
				    	 new CardpayFailDialog(this,str3).show();
				     }else if(errorDesc.equals("MOBILE TYPE ERROR")){
				    	 String str4 = "您的卡不能购买这个机型的保险!";
				    	 new CardpayFailDialog(this,str4).show();
				     }else if(errorDesc.equals("PAYMENT CODE NOT ACTIVATED")){
				    	 String str5 = "卡还没被激活呢!";
				    	 new CardpayFailDialog(this,str5).show();
				     }else if(errorDesc.equals("OTHER ERROR")){
				    	 String str6 = "哎呀,出错了!";
				    	 new CardpayFailDialog(this,str6).show();
				     }else if(errorDesc.equals("INSURANCE NO ERROR")){
				    	 String str7 = "哎呀,出错了!";
				    	 new CardpayFailDialog(this,str7).show();
				     }
				}
			}
		}
		
	}

	//网络请求失败
	public void onFailure(String url, int errorCode) {
		super.onFailure(url, errorCode);
		if (url.equals(Urls.CARD_ORDER) || url.equals(Urls.CONFIRM_BUY)) {
			if (errorCode != 10004) {
				setWebErrorPage();
			}
		} else {
			//当请求参数地址既不等于CARD_ORDER或者CONFIRM_BUY,出现请求延时
			Toast.makeText(
					this,
					getResources()
							.getString(R.string.page_write_net_error_tips),
					Toast.LENGTH_SHORT).show();
			
		}
		
	}
	/**设置是否显示网络异常页面
	 * @Description:TODO
	 * @param b
	 * void
	 */
	private void setWebErrorPage() {
		Toast.makeText(
				this,
				getResources()
						.getString(R.string.page_write_net_error_tips),
				Toast.LENGTH_SHORT).show();
		
	}

	/**
	 * 弹出支付成功
	 */
	private void toastOK() {
		Toast.makeText(this, "恭喜您支付成功",
				Toast.LENGTH_SHORT).show();
	}
}
