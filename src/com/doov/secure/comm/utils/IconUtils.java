package com.doov.secure.comm.utils;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.doov.secure.receiver.DoovSecureReceiver;
import com.doov.secure.ui.activity.LoadingActivity;

public class IconUtils {
	/**
	 * 设置显示和隐藏icon
	 * 
	 * @param b
	 * @param context
	 */
	public static void showIcon(boolean b, Context context) {
		PackageManager packageManager = context.getPackageManager();
		ComponentName componentName = new ComponentName(context,
				LoadingActivity.class);
		if (!b) {
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(DoovSecureReceiver.notifyId);// 删除一个特定的通知ID对应的通知
			// 隐藏应用图标
			packageManager.setComponentEnabledSetting(componentName,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
		} else {
			// 显示应用图标
			packageManager.setComponentEnabledSetting(componentName,
					PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
					PackageManager.DONT_KILL_APP);
		}

	}
}
