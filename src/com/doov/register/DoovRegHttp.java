package com.doov.register;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import android.content.Context;
import android.util.Log;

public class DoovRegHttp {
	private static final String TAG = "DoovReg";
	public DoovRegGenInfo doovRegGen = null;
	private List<NameValuePair> paramList;
	private Context mContext;

	public DoovRegHttp(Context context) {
		// TODO Auto-generated constructor stub
		doovRegGen = DoovRegGenInfo.getInstance(context);
		paramList = new ArrayList<NameValuePair>();
		mContext = context;
	}

	public String postReg() {
		String ret = null;
		Log.d(TAG,"postReg");

		try {
			String url = DoovRegConst.REG_URL_QIJI;
			if (doovRegGen.getPhoneType().contains("L5Pro")) {
				url = DoovRegConst.REG_URL_CHUANQI;
			} else if (doovRegGen.getPhoneType().contains("L5M")) {
				url = DoovRegConst.REG_URL_JIANAI;
			} else if (doovRegGen.getPhoneType().contains("L5")) {
				url = DoovRegConst.REG_URL_QINGGUO;
			} else if (doovRegGen.getPhoneType().contains("L6")){
				url = DoovRegConst.REG_URL_CUICAN;	//CBQ add for L6
			} else if (doovRegGen.getPhoneType().contains("M1t")) {
				url = DoovRegConst.REG_URL_FEIYANG;
			} else if (doovRegGen.getPhoneType().contains("A6")) {
				url = DoovRegConst.REG_URL_XIATIAN;
			} else if (doovRegGen.getPhoneType().contains("A3")) {
				url = DoovRegConst.REG_URL_ANGEL;
			} else if (doovRegGen.getPhoneType().contains("L8")) {
				url = DoovRegConst.REG_URL_QINGMU;
			} else if (doovRegGen.getPhoneType().contains("A5")) {
				url = DoovRegConst.REG_URL_QINGTIAN;
			} else if (doovRegGen.getPhoneType().contains("A8")) {
				url = DoovRegConst.REG_URL_ZHANFANG;
			} else if (doovRegGen.getPhoneType().contains("M2")) {
				url = DoovRegConst.REG_URL_FEIYUE;
			} else if (doovRegGen.getPhoneType().contains("L9")) {
				url = DoovRegConst.REG_URL_QINGXIN;
			} else if (doovRegGen.getPhoneType().contains("V3")) {
				url = DoovRegConst.REG_URL_FEIXIANG;
			} else if (doovRegGen.getPhoneType().contains("L9_mini")) {
				url = DoovRegConst.REG_URL_ZHIAI;
			} else if (doovRegGen.getPhoneType().contains("A9")){
				url = DoovRegConst.REG_URL_ANNIE;
			} else if (doovRegGen.getPhoneType().contains("V5")) {
				url = DoovRegConst.REG_URL_FEIWU_CP;
			} else if (doovRegGen.getPhoneType().contains("V5C")) {
				url = DoovRegConst.REG_URL_FEIWU_OP;
			} else if (doovRegGen.getPhoneType().contains("V6")) {
				url = DoovRegConst.REG_URL_FEILONG;
			} else if (doovRegGen.getPhoneType().contains("L520")) {
				url = DoovRegConst.REG_URL_JIXIANG;
			} else if (doovRegGen.getPhoneType().contains("L525")) {
				url = DoovRegConst.REG_URL_LIANGXIA;
			} else if (doovRegGen.getPhoneType().contains("L925")) {
				url = DoovRegConst.REG_URL_XINDONG;
			}
			Log.d(TAG,"PhoneType =" + doovRegGen.getPhoneType());
			
			URL postUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
			Log.d(TAG,"postUrl open connection");
			connection.setDoOutput(true);
			// Read from the connection. Default is true.
			connection.setDoInput(true);
			// Set the post method. Default is GET
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.connect();
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			// The URL-encoded contend
			String content = doovRegGen.getLocation() + ";" +
					doovRegGen.getImei(mContext) + ",000000000000000;" +
					doovRegGen.getDisplayMetrics() + ";" +
					doovRegGen.getPhoneTypeAndRAM(mContext) + ";" + //change doovRegGen.getPhoneType() to doovRegGen.getPhoneTypeAndRAM(mContext) by huangjiawei at 2016/11/02
					doovRegGen.getVersion() + ";" +
					doovRegGen.getRelease() + ";" +
					doovRegGen.getDataTime() + ";" +
					doovRegGen.getNetwork() + ";" +
					doovRegGen.getOperatorName() + ";";
			// URLEncoder.encode(service.doovRegGen.getOperatorName(), "UTF-8")
			// + ";";
			out.write(content.getBytes());

			out.flush();
			out.close(); // flush and close
			if (connection.getResponseCode() == 200) {
				Log.d(TAG, "postReg getResponseCode is 200");
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));

				ret = reader.readLine();
				reader.close();
			}
			else {
				Log.d(TAG, "postReg connection error=" + connection.getResponseCode());
				ret = "0";
			}
			connection.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "IOException " + e.getMessage());
			e.printStackTrace();
		}
		return ret;
	}
}
