package com.doov.secure.ui.view;

import com.doos.secure.R;
import com.doov.secure.comm.utils.FormatUtils;
import com.doov.secure.comm.utils.IdcardUtils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class DoovEditText extends RelativeLayout {
	/** 输入框 */
	private EditText mEditText;

	/** 输入框前的提示组件 */
	private TextView tip_txt;

	/** 输入格式错误提示组件 */
	private TextView error_tips;

	/** 清除信息按钮 */
	private ImageView clean_btn;

	/** title:提示 errortips：错误提示 editHint：编辑框默认提示 */
	private String title, errortips, editHint;

	/** 编辑框是否可编辑 */
	private boolean textCanWrite;

	/** 姓名 */
	public static final int NAME = 0;

	/** 身份证号码 */
	public static final int IDCARD = 1;

	/** 手机号码 */
	public static final int PHONE = 2;
	
	/**支付卡密码*/
	public static final int CARDPAY_PWD = 3;
	
	/** 输入框类型 */
	private int mType;

	public DoovEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_doov_edittext, this, true);
		initView();
		initStyle(context, attrs);
		initData();
	}

	/**
	 * 设置输入框文字变动监听
	 * 
	 * @param watcher
	 */
	public void addTextChangedListener() {
		mEditText.addTextChangedListener(watcher);
	}

	/**
	 * 设置输入框焦点变动监听
	 */
	public void setOnFocusChangeListener(final int Type) {
		mType = Type;
		mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					clean_btn.setVisibility(INVISIBLE);
					clean_btn.setEnabled(false);
					checkinput();
				} else {
					setVisible(error_tips, false);
					if (!TextUtils.isEmpty(mEditText.getText().toString().trim())) {
						clean_btn.setVisibility(VISIBLE);
						clean_btn.setEnabled(true);
					} else {
						clean_btn.setVisibility(INVISIBLE);
						clean_btn.setEnabled(false);
					}
				}
			}
		});
	}

	/**
	 * @return 编辑栏是否为空
	 */
	public boolean isEmpty() {
		return TextUtils.isEmpty(getText().trim());
	}

	/**
	 * 检查输入内容
	 */
	public boolean checkinput() {
		String msg = mEditText.getText().toString().trim();
		mEditText.clearFocus();
		if (TextUtils.isEmpty(msg)) {
			return false;
		}
		boolean isRight = false;
		switch (mType) {
		case NAME:
			isRight = FormatUtils.isName(msg);
			break;
		case IDCARD:
			isRight = IdcardUtils.validateCard(msg);
			break;
		case PHONE:
			isRight = FormatUtils.isMobileNO(msg);
			break;
		default:
			break;
		}
		setVisible(error_tips, !isRight);
		return isRight;
	}

	/**
	 * 设置输入类型
	 */
	public void setInputType(int type) {
		mEditText.setInputType(type);
	}

	/**
	 * 设置输入框点击事件
	 */
	public void setOnClickListener(OnClickListener l) {
		mEditText.setOnClickListener(l);
	}

	/**
	 * 设置编辑框里面的文字
	 * 
	 * @param string
	 */
	public void setText(String string) {
		mEditText.setText(string);
	}

	/**
	 * @return 返回编辑框里面的值
	 */
	public String getText() {
		return mEditText.getText().toString();
	}

	/**
	 * 对组件进行赋值
	 */
	private void initData() {
		mEditText.setHint(editHint);
		tip_txt.setText(title);
		error_tips.setText(errortips);
		mEditText.setFocusable(textCanWrite);
		if (textCanWrite) {
			addTextChangedListener();
		}
	}

	/**
	 * 获得我们所定义的自定义样式属性
	 */
	private void initStyle(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.doov_edittext);
		title = a.getString(R.styleable.doov_edittext_titleText);
		errortips = a.getString(R.styleable.doov_edittext_errorText);
		editHint = a.getString(R.styleable.doov_edittext_editHint);
		textCanWrite = a.getBoolean(R.styleable.doov_edittext_textCanWrite, true);
		a.recycle();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		mEditText = (EditText) findViewById(R.id.edittext);
		tip_txt = (TextView) findViewById(R.id.tip_txt);
		error_tips = (TextView) findViewById(R.id.error_tips);
		clean_btn = (ImageView) findViewById(R.id.clean_btn);
		clean_btn.setOnClickListener(clickListener);
	}

	private void setVisible(final View v, final boolean isVisible) {
		if ((v.getVisibility() == GONE && !isVisible) || (v.getVisibility() == VISIBLE && isVisible)) {
			return;
		}
		ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha", isVisible ? 0f : 1f, isVisible ? 1f : 0f);
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				if (isVisible) {
					v.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (!isVisible) {
					v.setVisibility(View.GONE);
				}

			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		anim.setDuration(500);
		anim.start();

	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mEditText.setText("");
		}
	};
	TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (TextUtils.isEmpty(s.toString().trim())) {
				clean_btn.setVisibility(INVISIBLE);
				clean_btn.setEnabled(false);
				error_tips.setVisibility(INVISIBLE);
			} else {
				if (mEditText.isFocused()) {
					clean_btn.setVisibility(VISIBLE);
					clean_btn.setEnabled(true);
				}
			}

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};
}
