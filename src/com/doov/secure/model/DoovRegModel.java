package com.doov.secure.model;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class DoovRegModel {
	private String sys;
	private String activate;
	private String IMEI;
	public String getSys() {
		return sys;
	}
	public void setSys(String sys) {
		this.sys = sys;
	}
	public String getActivate() {
		return activate;
	}
	public void setActivate(String activate) {
		this.activate = activate;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public static DoovRegModel json2Obj(String content) {
		DoovRegModel regModel = null;
		Gson gson = new Gson();
		try {
			List<DoovRegModel> regModels = gson.fromJson(content, new TypeToken<List<DoovRegModel>>() {
			}.getType());
			regModel = regModels.get(0);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return regModel;
	}
}
