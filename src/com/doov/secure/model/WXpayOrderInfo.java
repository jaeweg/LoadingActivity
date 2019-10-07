package com.doov.secure.model;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class WXpayOrderInfo {
	private String result_code;
	private String return_msg;
	private String trade_no;
	private String appid;
	private String partnerid;
	private String prepayid;
	private String packages;
	private String noncestr;
	private String timestamp;
	private String sign;

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getReturn_msg() {
		return return_msg;
	}

	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}

	public String getPrepayid() {
		return prepayid;
	}

	public void setPrepayid(String prepayid) {
		this.prepayid = prepayid;
	}

	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}

	public String getNoncestr() {
		return noncestr;
	}

	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public static WXpayOrderInfo json2Obj(String content) {
		Gson gson = new Gson();
		try {
			return gson.fromJson(content.replaceAll("package", "packages"), WXpayOrderInfo.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
}
