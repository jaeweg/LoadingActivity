package com.doov.secure.comm.utils;

import com.doov.secure.model.BuyedModel;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {
	private static final String TAG = PreferencesUtils.class.getSimpleName();
	public static String PREFERENCE_NAME = "doovsecure_preferences";
	private static String TIME = "time";
	private static String BUYED = "isbuy";
	private static String RESULT = "buyresult";
	private static String InsuranceNO = "InsuranceNO";
	private static String State = "state";
	private static String NET_TIPS = "net_tip";
	private static String SHOW_DAYS = "showDays";
	private static String BUY_TIME = "BuyTime";

	/**
	 * 设置通知显示开始时间
	 * 
	 * @param context
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean setTime(Context context, long value) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(TIME, value);
		return editor.commit();
	}

	/**
	 * 获取设置的通知显示开始时间
	 * 
	 * @param context
	 * @return
	 */
	public static long getTime(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		return settings.getLong(TIME, -1);
	}

	/**
	 * 设置已经购买无忧险(和审核结果无关)
	 * 
	 * @param context
	 * @return
	 */
	public static boolean setBuyed(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(BUYED, true);
		return editor.commit();
	}

	/**
	 * 获取是否购买无忧险
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getBuyed(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		return settings.getBoolean(BUYED, false);
	}

	/**
	 * 存储购买信息
	 */
	public static boolean setBuyModel(Context context, BuyedModel model) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(InsuranceNO, model.getInsuranceNO());
		editor.putInt(State, model.getType());
		return editor.commit();
	}

	/**
	 * 获取购买信息
	 * 
	 * @param context
	 * @return
	 */
	public static BuyedModel getBuyModel(Context context) {
		BuyedModel model = new BuyedModel();
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		model.setInsuranceNO(settings.getString(InsuranceNO, ""));
		model.setType(settings.getInt(State, 1));
		return model;
	}

	/**
	 * 获取加载页提醒网络流量的 checkbox选择情况
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getShowNetTips(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		return settings.getBoolean(NET_TIPS, true);
	}

	/**
	 * 设置加载页提醒网络流量的 checkbox选择情况
	 * 
	 * @param context
	 * @return
	 */
	public static boolean setShowNetTips(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(NET_TIPS, false);
		return editor.commit();
	}
	
	/**
	 * 保存服务器上的购买无忧险过期时间
	 */		 
	public static boolean setShowDays(Context context, int value) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(SHOW_DAYS, value);
		return editor.commit();
	}	

	/**
	 * 获取本地保存的服务器上购买无忧险的过期时间
	 */	
	public static int getShowDays(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		return settings.getInt(SHOW_DAYS, 3);
	}	

	/**
	 * 保存无忧险购买的时间
	 */
	public static boolean setBuyTime(Context context, long value) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(BUY_TIME, value);
		return editor.commit();
	}

	/**
	 * 获取无忧险购买的时间
	 */
	public static long getBuyTime(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		return settings.getLong(BUY_TIME, -1);
	}	
}
