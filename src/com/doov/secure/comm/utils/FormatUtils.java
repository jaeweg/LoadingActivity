package com.doov.secure.comm.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtils {

	public static boolean isIP(String IP) {
		Pattern p = Pattern
				.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher m = p.matcher(IP);
		return m.matches();
	}

	public static boolean isMobileNO(String mobiles) {
		//change by ghshuai 修改17[0,6,7,8]，145等开头的电话不识别
		//add by huangjiawei 173
		Pattern p = Pattern
				.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[0,3,6,7,8])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isPwdOk(String pwd) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9_]{6,16}$");
		Matcher m = p.matcher(pwd);
		return m.matches();
	}

	public static boolean isMobileYES(String mobiles) {
		Pattern p = Pattern.compile("^1[3,4,5,7,8]\\d{9}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static boolean islegal(String pwd) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9]{6,}$");
		Matcher m = p.matcher(pwd);
		return m.matches();
	}

	public static boolean isName(String name){
    	//Pattern p=Pattern.compile("^([\\u4e00-\\u9fa5]+|([a-zA-Z]+\\s?)+)$");
    	Pattern p=Pattern.compile("^([\\u4e00-\\u9fa5]+)$");
		Matcher m = p.matcher(name);
		if( m.matches())
		{
			return m.matches();
        }
        Pattern p1=Pattern.compile("^([a-zA-Z]+\\s?)$"); 
    	Matcher m1 = p1.matcher(name);
    	return m1.matches();         
    }
}
