package com.doov.secure.comm.net;

public class Urls {
	/** 初始域名 */
	public static String BASE_HEAD = "http://ha.doov.com.cn:9066/";
	/** 查询销量统计接口 */
	public static String CHECK_REG = "http://activitite.doov.cn:9090/api/CheckActivate";
	/** 获取ip 和端口号 */
	public static String IP_ADDRESS = BASE_HEAD + "/address/query";
	/** 获取无忧险商品信息 */
	public static String GOODS_INFO = "/goods/detailedinformationrequest";
	/** 提交用户信息 */
	public static String PUSH_USER_INFO = "/insurance/userdetail";
	/** 获取支付宝订单 */
	public static String ALIPAY_ORDER = "/insurance/query";
	/** 获取微信支付订单 */
	public static String WXPAY_ORDER = "/theme/weixinpay";
	/** 获取支付卡支付订单*/
	public static String CARD_ORDER = "/insurance/paymentcode";
	/** 获取是否购买无忧险 */
	public static String CONFIRM_BUY = "/order/buyrecord";
	/** 获取是否在商城购买无忧险 */
	public static String CONFIRM_BUY_FROM_SHOP = "/mall/buyquery";
	/** 获取微信支付结果 */
	public static String WXPAY_RESULT = "/theme/weixinquery";
	/** 获取服务器系统时间和icon显示天数 */
	public static String GET_SERVER_TIME = "/insurance/gettime";
}
