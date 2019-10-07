package com.doov.secure.model;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class InsuranceModel {
	String InsuranceNO;


	public String getInsuranceNO() {
		return InsuranceNO;
	}


	public void setInsuranceNO(String insuranceNO) {
		InsuranceNO = insuranceNO;
	}


	public static InsuranceModel json2Obj(String content) {
		InsuranceModel model = null;
		Gson gson = new Gson();
		try {
			List<InsuranceModel> models = gson.fromJson(content, new TypeToken<List<InsuranceModel>>() {
			}.getType());
			model = models.get(0);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return model;
	}
}
