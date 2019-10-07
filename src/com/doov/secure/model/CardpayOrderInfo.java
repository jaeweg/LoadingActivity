package com.doov.secure.model;

import java.util.List;

import com.alipay.sdk.util.g;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CardpayOrderInfo {
	private String result;
	private String desc;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public static CardpayOrderInfo json2Obj(String content){
		Gson gson = new Gson();
		
		/*List<CardpayOrderInfo> cardPayOrdModel = gson.fromJson(content, new TypeToken<List<CardpayOrderInfo>>() {
		}.getType());*/
		return gson.fromJson(content,CardpayOrderInfo.class);
	}
}
