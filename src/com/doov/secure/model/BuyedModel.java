package com.doov.secure.model;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class BuyedModel {
	private String UserName;
	private String MobileNumber;
	private String IdCard;
	private String InsuranceNO;
	private String IMEI;
	private String VerifyStatus;
	//lijuan add for one year tip at 20151225
	private String BuyTime;
	private String errorcode;
	private int type;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getMobileNumber() {
		return MobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		MobileNumber = mobileNumber;
	}
	public String getIdCard() {
		return IdCard;
	}
	public void setIdCard(String idCard) {
		IdCard = idCard;
	}
	public String getInsuranceNO() {
		return InsuranceNO;
	}
	public void setInsuranceNO(String insuranceNO) {
		InsuranceNO = insuranceNO;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public String getVerifyStatus() {
		return VerifyStatus;
	}
	public void setVerifyStatus(String verifyStatus) {
		VerifyStatus = verifyStatus;
	}
	//lijuan add for one year tip at 20151225
	public String getBuyTime() {
		return BuyTime;
	}
	public void setBuyTime(String buyTime) {
		BuyTime = buyTime;
	}	
	public String getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
	public static BuyedModel json2Obj(String content) {
		BuyedModel regModel = null;
		Gson gson = new Gson();
		try {
			List<BuyedModel> regModels = gson.fromJson(content, new TypeToken<List<BuyedModel>>() {
			}.getType());
			regModel = regModels.get(0);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return regModel;
	}
}
