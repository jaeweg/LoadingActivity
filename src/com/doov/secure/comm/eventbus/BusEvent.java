package com.doov.secure.comm.eventbus;

public class BusEvent {
	/** 关闭所有已经打开的activity */
	public static final int CLOSE_ALL_ACTIVITY = 0x0000001;

	/** 使用支付宝支付 */
	public static final int USE_ALIPAY = 0x0000002;
	
	/** 使用微信支付支付 */
	public static final int USE_WXPAY = 0x0000003;
	
	/** 使用支付卡支付 */
	public static final int USE_PAYCARD = 0x0000004;
	
	/** 需要执行的事件 */
	private int mAction;

	public BusEvent(int action) {
		mAction = action;
	}

	public int getAction() {
		return mAction;
	}
}
