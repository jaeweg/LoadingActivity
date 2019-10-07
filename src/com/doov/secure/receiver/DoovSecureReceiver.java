package com.doov.secure.receiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.doos.secure.R;
import com.doov.secure.base.AppContext;
import com.doov.secure.comm.eventbus.BusEvent;
import com.doov.secure.comm.net.INetClient;
import com.doov.secure.comm.net.NetClient;
import com.doov.secure.comm.net.Urls;
import com.doov.secure.comm.utils.DoovConstants;
import com.doov.secure.comm.utils.IconUtils;
import com.doov.secure.comm.utils.PreferencesUtils;
import com.doov.secure.model.BuyedModel;
import com.doov.secure.model.DoovRegModel;
import com.doov.secure.model.IpPortModel;
import com.doov.secure.model.ServerTimeModel;
import com.doov.secure.ui.activity.LoadingActivity;

import de.greenrobot.event.EventBus;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.doov.secure.comm.utils.LogUtil;

public class DoovSecureReceiver extends BroadcastReceiver {
	/** Notification管理 */
	private NotificationManager mNotificationManager;
	/** Notification的ID */
	public static int notifyId = 100;
	/** 显示通知时间 */
	private static final int hideNotificationDays = 3;
	/** 图标消失时间 */
	private static final int hideIconDays = 5;//修改为5天 by hkp 20150828
	/** 时间单位（目前按天算） */
	private final long timeUnit = 1000 * 60 * 60 * 24;
	private  int TimeType = Calendar.DAY_OF_MONTH;

	// private int TimeType=Calendar.MINUTE;
	// private final long timeUnit = 1000 * 60;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(DoovConstants.DOOV_NOTIFY_ACTION)) {
			// 接到销量统计后需要请求服务器 查询激活时间（防止重置后 销量统计发送导致数据问题）
			getIsReg(context);
		} else if (action.equals(DoovConstants.BOOT_COMPLETED)) {
			// 获取本地存的闹钟时间
			long beginTime = PreferencesUtils.getTime(context);
			// 如果返回小于0 ，没有收到销量统计通知 或者是 系统重置过
			if (beginTime < 0) {
				delayRequest(context);
				return;
			}
			/**
			// 判断是否已经超过3天期限
			checkOverTime(context, beginTime, System.currentTimeMillis(),
					hideNotificationDays);
			// 判断是否已经超过5天期限，超过需要隐藏
			checkOverTime(context, beginTime, System.currentTimeMillis(),
					hideIconDays);
			*/
			requestServerIP(context);			
		} else if (action.equals(DoovConstants.DOOV_ALARM_ACTION)) {
			// 已经取消3天固定通知栏需求

		} else if (action.equals(DoovConstants.DOOV_HIDE_ICON_ACTION)) {
			// 关闭所有activity
			if (PreferencesUtils.getBuyed(context)) {
				//如果已经购买 ，不进行隐藏icon操作
				return;
			}
			EventBus.getDefault().post(
					new BusEvent(BusEvent.CLOSE_ALL_ACTIVITY));
			IconUtils.showIcon(true, context);
		} else if (action.equals(DoovConstants.DOOV_DISPLAY_ICON_ACTION)) {
			// 需要显示icon
			IconUtils.showIcon(true, context);
		} else if (action.equals(DoovConstants.DOOV_RE_ALARM_ACTION)) {
			//如果已经购买则不会在通知栏显示无忧险通知
			if (PreferencesUtils.getBuyed(context)) {
				return;
			}
			if (!overTime(PreferencesUtils.getTime(context),
					System.currentTimeMillis(), hideNotificationDays)) {
				setNotificationAlarm(context, PreferencesUtils.getTime(context));
			}
		}
	}

	/**
	 * 取消通知栏
	 */
	private void cancelNotify(Context context) {
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		mNotificationManager.cancel(notifyId);// 删除一个特定的通知ID对应的通知
	}

	/**
	 * 延时请求服务器，防止没有网络
	 * 
	 * @param context
	 */
	private void delayRequest(Context context) {
		new DelayCount(context, 1000 * 60 * 5, 1000).start();
	}

	/**
	 * 检查激活时间和当前时间
	 * @param context
	 * @param beginTime
	 * @param sysTime
	 * @param days
	 */
	private void checkOverTime(Context context, long beginTime, long sysTime,
			int days) {
		switch (days) {
		case hideNotificationDays:
			// 如果已经购买，则不需要 通知栏通知
			if (PreferencesUtils.getBuyed(context)) {
				return;
			}
			if (!overTime(beginTime, sysTime, days)) {
				setNotificationAlarm(context, beginTime);
			}
			break;
		case hideIconDays:
			if (overTime(beginTime, sysTime, days)) {
				if (PreferencesUtils.getBuyed(context)) {
					// 如果读取到本地以及购买 则继续显示
					IconUtils.showIcon(true, context);
				} else {
					// 本地未读取到是否购买，需要请求服务器进行判断
					requestIP(context);
				}
			} else {
				IconUtils.showIcon(true, context);
				// 没有超过5天 则继续设置闹铃
				//setIconAlarm(context, beginTime);
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 获取IP
	 */
	private void requestIP(final Context context) {
		new NetClient(context, new INetClient() {

			@Override
			public void onSuccess(String url, Object obj) {
				IpPortModel ipm = (IpPortModel) obj;
				AppContext.portModel = ipm;
				requestBuyFromShop(context);
			}

			@Override
			public void onFailure(String url, int errorCode) {
				new DelayBuydCount(context, 1000 * 60 * 5, 1000).start();
			}
		}).getIPandPort(Urls.IP_ADDRESS);
	}

	/**
	 * 获取是否购买
	 */
	private void requestIsBuyed(final Context context) {
		new NetClient(context, new INetClient() {

			@Override
			public void onSuccess(String url, Object obj) {
				IconUtils.showIcon(true, context);
				saveBuyInfo(context, obj);
				PreferencesUtils.setBuyed(context);
				cancelNotify(context);
			}

			@Override
			public void onFailure(String url, int errorCode) {
				if (errorCode == 10004) {
					IconUtils.showIcon(true, context);
				} else {
					// 没有请求成功就继续请求
					new DelayBuydCount(context, 1000 * 60 * 5, 1000).start();
				}

			}
		}).getIsBuyed(Urls.CONFIRM_BUY);
	}

	/**
	 * 访问服务器 获取是否在网上商城购买无忧险
	 */
	private void requestBuyFromShop(final Context context) {
		new NetClient(context, new INetClient() {

			@Override
			public void onSuccess(String url, Object obj) {
				IconUtils.showIcon(true, context);
				saveBuyInfo(context, obj);
				PreferencesUtils.setBuyed(context);
				cancelNotify(context);
			}

			@Override
			public void onFailure(String url, int errorCode) {
				if (errorCode == 10004) {
					requestIsBuyed(context);
				} else {
					// 没有请求成功就继续请求
					new DelayBuydCount(context, 1000 * 60 * 5, 1000).start();
				}

			}
		}).getBuyedFromShop(Urls.CONFIRM_BUY_FROM_SHOP);
	}

	/**
	 * 保存购买结果在本地
	 */
	private void saveBuyInfo(final Context context, Object obj) {
		BuyedModel m = (BuyedModel) obj;
		String state = m.getVerifyStatus();
		int type = 0;
		if (state.equals("SUCCESS")) {// 审核成功
			type = 2;
		} else if (state.equals("FAIL")) {// 审核中
			type = 1;
		} else if (state.equals("NOPASS")) {// 审核失败
			type = 3;
		}
		type = 2;//lijuan add for confirm success at 20151229
		m.setType(type);
		//lijuan add for one year tip at 20151225
		String buytime = m.getBuyTime();
		//LogUtil.i("lijuan", "[CONFIRM_BUY] buytime = " + buytime);
		if (TextUtils.isEmpty(buytime)) {
			PreferencesUtils.setBuyTime(context, -1);
		} else {					
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			try {
				long buyTime = sf.parse(buytime).getTime();
				PreferencesUtils.setBuyTime(context, buyTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}					
		//end lijuan		
		PreferencesUtils.setBuyModel(context, m);
	}

	/**
	 * 获取是否激活销量统计
	 */
	private void getIsReg(final Context context) {
		new NetClient(context, new INetClient() {

			@Override
			public void onSuccess(String url, Object obj) {
				DoovRegModel regmodel = (DoovRegModel) obj;
				if (regmodel != null) {
					checkTime(regmodel, context);
				}
			}

			@Override
			public void onFailure(String url, int errorCode) {
				delayRequest(context);
			}
		}).checkIsReg(Urls.CHECK_REG);

	}

	/**
	 * 检查服务器返回的激活时间和系统时间
	 * 
	 * @param regmodel
	 * @param context
	 */
	@SuppressLint("SimpleDateFormat")
	protected void checkTime(DoovRegModel regmodel, Context context) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		try {

			// 拿到激活时间
			String startStr = regmodel.getActivate();
			// startStr = "2015-07-01 14:05:00.303";
			// 销量统计没有发广播过来时 获得的激活时间为空
			if (TextUtils.isEmpty(startStr)) {
				return;
			}
			long startTime = sf.parse(startStr).getTime();
			// 获取服务器系统时间
			long sysTime = Long.parseLong(regmodel.getSys());
			// 判断是否超过3天，如果没有 就设置闹铃 并在通知栏显示
			checkOverTime(context, startTime, sysTime, hideNotificationDays);
			// 如果超过15天 需要隐藏应用
			checkOverTime(context, startTime, sysTime, hideIconDays);
			// 在此处获取到激活时间后记录到本地
			PreferencesUtils.setTime(context, startTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 比对是否超过指定时间
	 * @param beginTime
	 *            激活时间
	 * @param sysTime
	 *            当前时间
	 * @return
	 */
	private boolean overTime(long beginTime, long sysTime, int days) {
		long midtime = sysTime - beginTime;
		return midtime > timeUnit * days;
	}

	/**
	 * 设置icon消失闹钟
	 */
	private void setIconAlarm(Context context, long milliseconds) {
		// 设置通知显示的开始时间
		PreferencesUtils.setTime(context, milliseconds);
		// 开启闹钟计时
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		String action = DoovConstants.DOOV_HIDE_ICON_ACTION;
		Intent mIntent = new Intent(action);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, mIntent,
				0);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliseconds);
		calendar.add(TimeType, hideIconDays);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	}

	/**
	 * 设置通知栏消失闹钟
	 */
	private void setNotificationAlarm(Context context, long milliseconds) {
		// 设置通知显示的开始时间
		PreferencesUtils.setTime(context, milliseconds);
		// 开启闹钟计时
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		String action = DoovConstants.DOOV_RE_ALARM_ACTION;
		Intent mIntent = new Intent(action);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, mIntent,
				0);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(TimeType, 1);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		// 开始提示通知

		showCzNotify(context);
	}

	/**
	 * 显示通知栏
	 */
	public void showCzNotify(Context context) {
		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context);
		Intent intents = new Intent();
		intents.setClassName("com.doov.secure",
				"com.doov.secure.ui.activity.LoadingActivity");
		// PendingIntent 跳转动作
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intents, 0);
		mBuilder.setSmallIcon(R.drawable.notify_icon)
				.setContentTitle(
						context.getResources().getString(R.string.notify_title))
				.setContentText(
						context.getResources().getString(
								R.string.notify_content))
				.setContentIntent(pendingIntent);
		Notification mNotification = mBuilder.build();
		// 在通知栏上点击此通知后自动清除此通知
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;//
		// 设置发出通知的时间
		mNotification.when = System.currentTimeMillis();
		// 在通知栏上点击此通知后自动清除此通知
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(notifyId, mNotification);

	}

	/* 定义一个倒计时的内部类 */
	class DelayCount extends CountDownTimer {
		Context context;

		public DelayCount(Context context, long millisInFuture,
				long countDownInterval) {
			super(millisInFuture, countDownInterval);
			this.context = context;
		}

		@Override
		public void onFinish() {
			getIsReg(context);
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}

	class DelayBuydCount extends CountDownTimer {
		Context context;

		public DelayBuydCount(Context context, long millisInFuture,
				long countDownInterval) {
			super(millisInFuture, countDownInterval);
			this.context = context;
		}

		@Override
		public void onFinish() {
			requestIP(context);
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}
	
	/**
	 * 获取服务器的IP
	 */
	private void requestServerIP(final Context context) {
		new NetClient(context, new INetClient() {

			@Override
			public void onSuccess(String url, Object obj) {
				IpPortModel ipm = (IpPortModel) obj;
				AppContext.portModel = ipm;
				//LogUtil.i("lijuan", "[requestServerIP] onSuccess");
				requestServerTime(context);
			}

			@Override
			public void onFailure(String url, int errorCode) {
				//LogUtil.i("lijuan", "[requestServerIP] onFailure");
				new DelayServerCount(context, 1000 * 60 * 5, 1000).start();
			}
		}).getIPandPort(Urls.IP_ADDRESS);
	}
	
	/**
	 * 请求服务器上系统时间和购买无忧险的过期时间
	 */
	private void requestServerTime(final Context context) {
		new NetClient(context, new INetClient() {

			@Override
			public void onSuccess(String url, Object obj) {
				//LogUtil.i("lijuan", "[requestServerTime] onSuccess");
				// 用服务器系统时间和激活时间比较通知栏及icon的显示情况
				checkServerTime(context, obj);
			}

			@Override
			public void onFailure(String url, int errorCode) {
					// 没有请求成功就继续请求
					//LogUtil.i("lijuan", "[requestServerTime] onFailure--- DelayBuydCount");
					new DelayServerCount(context, 1000 * 60 * 5, 1000).start();
			}
		}).getServerTime(Urls.GET_SERVER_TIME);
	}	

	/**
	 * 用从服务上获取的时间来进行比较
	 */
	private void checkServerTime(final Context context, Object obj) {
		try {
			ServerTimeModel m = (ServerTimeModel) obj;
			String curr_time = m.getCurrentTime();
			String show_days = m.getShowDays();

			long currTime = Long.parseLong(curr_time)*1000;
			int showDays = Integer.parseInt(show_days);
			
			long activedTime = PreferencesUtils.getTime(context);
			
			// 判断是否超过3天，如果没有 就设置闹铃 并在通知栏显示
			checkOverTime(context, activedTime, currTime, hideNotificationDays);
			// 判断如果超过5天， 需要隐藏应用
			checkOverTime(context, activedTime, currTime, showDays);
			// 将icon可显示的时间保存在本地		
			PreferencesUtils.setShowDays(context, showDays);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * 定义一个请求服务器时间的倒计时类
	 */	 
	class DelayServerCount extends CountDownTimer {
		Context context;

		public DelayServerCount(Context context, long millisInFuture,
				long countDownInterval) {
			super(millisInFuture, countDownInterval);
			this.context = context;
		}

		@Override
		public void onFinish() {
			requestServerIP(context);
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}	
}
