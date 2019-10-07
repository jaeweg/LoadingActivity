package com.doov.secure.ui.dialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.doos.secure.R;
import com.doov.secure.interfaces.ISelectTime;
import com.doov.secure.ui.dialog.widget.time.JudgeDate;
import com.doov.secure.ui.dialog.widget.time.ScreenInfo;
import com.doov.secure.ui.dialog.widget.time.WheelMain;
import com.doov.secure.comm.utils.LogUtil;

public class SelectTimeDialog extends Dialog implements View.OnClickListener {
	private WheelMain wheelMain;
	private Activity activity;
	private ISelectTime mISelectTime;
	private Button yes;
	private String mTime;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public SelectTimeDialog(Context context, ISelectTime iselecttime,String timeStr) {
		this(context, R.style.select_time_dialog);
		activity = (Activity) context;
		mISelectTime = iselecttime;
		mTime=timeStr;
	}

	public SelectTimeDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 取消dialog标题栏
		setContentView(R.layout.dialog_selcect_time);
		initView();
		initStyle();
		initTime();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		yes = (Button) findViewById(R.id.yes);
		yes.setOnClickListener(this);
	}

	/**
	 * 初始化显示时间
	 */
	private void initTime() {
		wheelMain = new WheelMain(getWindow().getDecorView());
		ScreenInfo screenInfo = new ScreenInfo(activity);
		wheelMain.screenheight = screenInfo.getHeight();
		Calendar calendar = Calendar.getInstance();
		if(JudgeDate.isDate(mTime, "yyyy-MM-dd")){
			try {
				calendar.setTime(dateFormat.parse(mTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year, month, day);
	}

	/**
	 * 设置dialog样式
	 */
	private void initStyle() {
		Window win = getWindow();
		win.setGravity(Gravity.BOTTOM);
		win.setWindowAnimations(R.style.activityAnimation);
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		win.setAttributes(lp);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.yes:
			//LogUtil.i("lijuan", "wheelMain.getTime() = " + wheelMain.getTime());
			if (wheelMain.getTime() == null) {
				Toast.makeText(activity, activity.getResources().getString(R.string.invalid_date),
						Toast.LENGTH_SHORT).show();
			} else {
				mISelectTime.setTime(wheelMain.getTime());
			}
			dismiss();
			break;
		default:
			break;
		}
	}

}
