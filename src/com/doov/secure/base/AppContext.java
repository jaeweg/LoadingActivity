package com.doov.secure.base;

import com.doov.secure.model.IpPortModel;
import com.doov.secure.model.SecureModel;

import android.app.Application;

public class AppContext extends Application {
	public static IpPortModel portModel;
	public static SecureModel secureModel;
	public static int isActivate = 0;//是否激活销量统计   0为未激活   1为激活
}
