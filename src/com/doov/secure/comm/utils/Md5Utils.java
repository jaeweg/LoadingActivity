package com.doov.secure.comm.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * md5加密工具类
 * @author zy1449
 *
 */
public class Md5Utils {
	public static String encode(String password){
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] result = digest.digest(password.getBytes());
			StringBuffer sb = new StringBuffer();
			for(byte b : result){
				int number = (int)(b & 0xff) ;
				String str = Integer.toHexString(number);
				if(str.length()==1){
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			//出现异常,返回空字符串
			return "";
		}
	}
}
