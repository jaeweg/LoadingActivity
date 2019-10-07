package com.doov.secure.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class IpPortModel implements Serializable{
	String IP;
	String Port;

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getPort() {
		return Port;
	}

	public void setPort(String port) {
		Port = port;
	}

	@Override
	public String toString() {
		return "http://" + IP + ":" + Port;
	}

	public static IpPortModel json2Obj(String content) {
		IpPortModel portModel = null;
		Gson gson = new Gson();
		try {
			List<IpPortModel> portModels = gson.fromJson(content, new TypeToken<List<IpPortModel>>() {
			}.getType());
			portModel = portModels.get(0);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return portModel;
	}
}
