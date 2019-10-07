package com.doov.secure.model;

import java.io.Serializable;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class SecureModel implements Serializable {
	private String GoodsName;
	private String GoodsPrice;
	private String GoodsID;
	private String DetialUrl;
	private String GoodsDesc;

	public String getGoodsDesc() {
		return GoodsDesc;
	}

	public void setGoodsDesc(String goodsDesc) {
		GoodsDesc = goodsDesc;
	}

	public String getGoodsName() {
		return GoodsName;
	}

	public void setGoodsName(String goodsName) {
		GoodsName = goodsName;
	}

	public String getGoodsPrice() {
		return GoodsPrice;
	}

	public void setGoodsPrice(String goodsPrice) {
		GoodsPrice = goodsPrice;
	}

	public String getGoodsID() {
		return GoodsID;
	}

	public void setGoodsID(String goodsID) {
		GoodsID = goodsID;
	}

	public String getDetialUrl() {
		return DetialUrl;
	}

	public void setDetialUrl(String detialUrl) {
		DetialUrl = detialUrl;
	}

	public static List<SecureModel> json2Obj(String content) {
		List<SecureModel> list = null;
		Gson gson = new Gson();
		try {
			list = gson.fromJson(content, new TypeToken<List<SecureModel>>() {
			}.getType());
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return list;
	}
}
