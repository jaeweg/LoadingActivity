package com.doov.secure.model;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class ServerTimeModel {
	private String currentTime;
	private String showDays;
	private String errorcode;

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currTime) {
		currentTime = currTime;
	}

	public String getShowDays() {
		return showDays;
	}

	public void setShowDays(String showTime) {
		showDays = showTime;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public static ServerTimeModel json2Obj(String content) {
		ServerTimeModel stModel = null;
		Gson gson = new Gson();
		try {
			List<ServerTimeModel> stModels = gson.fromJson(content, new TypeToken<List<ServerTimeModel>>() {
			}.getType());
			stModel = stModels.get(0);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return stModel;
	}

}
