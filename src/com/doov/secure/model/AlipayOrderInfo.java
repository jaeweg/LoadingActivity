package com.doov.secure.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONException;

import com.doov.secure.comm.alipay.Rsa;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class AlipayOrderInfo {
	private String PayProvider;
	private String service;
	private String _input_charset;
	private String partner;
	private String notify_url;
	private String OrderType;
	private String GoodsID;
	private String subject;
	private String payment_type;
	private double total_fee;
	private String body;
	private String it_b_pay;
	private String show_url;
	private String extern_token;
	private String seller_id;
	private String app_id;
	private String appenv;
	private String out_trade_no;
	private String PK;

	public String getPayProvider() {
		return PayProvider;
	}

	public void setPayProvider(String payProvider) {
		PayProvider = payProvider;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String get_input_charset() {
		return _input_charset;
	}

	public void set_input_charset(String _input_charset) {
		this._input_charset = _input_charset;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getOrderType() {
		return OrderType;
	}

	public void setOrderType(String orderType) {
		OrderType = orderType;
	}

	public String getGoodsID() {
		return GoodsID;
	}

	public void setGoodsID(String goodsID) {
		GoodsID = goodsID;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

	public double getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(double total_fee) {
		this.total_fee = total_fee;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getIt_b_pay() {
		return it_b_pay;
	}

	public void setIt_b_pay(String it_b_pay) {
		this.it_b_pay = it_b_pay;
	}

	public String getShow_url() {
		return show_url;
	}

	public void setShow_url(String show_url) {
		this.show_url = show_url;
	}

	public String getExtern_token() {
		return extern_token;
	}

	public void setExtern_token(String extern_token) {
		this.extern_token = extern_token;
	}

	public String getSeller_id() {
		return seller_id;
	}

	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getAppenv() {
		return appenv;
	}

	public void setAppenv(String appenv) {
		this.appenv = appenv;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getPK() {
		return PK;
	}

	public void setPK(String pK) {
		PK = pK;
	}

	public static AlipayOrderInfo json2Obj(String content) {
		AlipayOrderInfo ordModel = null;
		Gson gson = new Gson();
		try {
			List<AlipayOrderInfo> ordModels = gson.fromJson(content, new TypeToken<List<AlipayOrderInfo>>() {
			}.getType());
			ordModel = ordModels.get(0);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return ordModel;
	}

	public String parseOrderParamToAlipayQuery() throws JSONException {
		// 返回的是分
		double totalFree = getTotal_fee() * 1.0 / 100;
		String alipayStr = "";
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("partner=\"");
			sb.append(getPartner());
			sb.append("\"&out_trade_no=\"");
			sb.append(getOut_trade_no());
			sb.append("\"&subject=\"");
			sb.append(getSubject());
			sb.append("\"&body=\"");
			sb.append(getBody());
			sb.append("\"&total_fee=\"");
			sb.append(totalFree);
			sb.append("\"&notify_url=\"");
			// 网址需要做URL编码
			sb.append(getNotify_url());
			sb.append("\"&service=\"");
			sb.append(getService());
			sb.append("\"&_input_charset=\"");
			sb.append(get_input_charset());
			sb.append("\"&return_url=\"");
			sb.append("http://m.alipay.com");
			sb.append("\"&payment_type=\"");
			sb.append(getPayment_type());
			sb.append("\"&seller_id=\"");
			sb.append(getSeller_id());

			// 如果show_url值为空，可不传
			sb.append("\"&it_b_pay=\"");
			sb.append(getIt_b_pay());
			sb.append("\"");

			// 获取字符串， 并进行签名
			alipayStr = sb.toString();
			String privateK = getPK();
			if (privateK == null || privateK.length() == 0) {
				throw (new JSONException("not privateK return"));
			}
			String oderSign = Rsa.sign(alipayStr, privateK);
			if (oderSign == null) {
				return null;
			}

			oderSign = URLEncoder.encode(oderSign, get_input_charset());
			alipayStr += "&sign=\"" + oderSign + "\"&" + "sign_type=\"RSA\"";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw (new JSONException("not support charset"));
		}
		return alipayStr;
	}
}
