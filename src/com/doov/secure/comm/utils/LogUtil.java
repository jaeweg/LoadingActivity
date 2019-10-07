package com.doov.secure.comm.utils;
import android.util.Log;
/* 
 * by yhw
 * */
public class LogUtil {

	private static final boolean DEBUG = true;
    private LogUtil() {}

    public static void v(final String tag, final String msg) {
        if (DEBUG) {
            final boolean nullMsg = (msg == null);
            final int callerStack = (nullMsg) ? 4 : 3;
            final StackTraceElement caller = Thread.currentThread().getStackTrace()[callerStack];
            if (caller == null) {
                Log.v(tag, msg);
            } else {
                if (nullMsg) {
                    Log.v(tag, caller.getMethodName() + "(" + caller.getFileName() + ":" + caller.getLineNumber() + ")");
                } else {
                    Log.v(tag, caller.getMethodName() + ", " + msg + " (" + caller.getFileName() + ":" +
                            caller.getLineNumber() + ")");
                }
            }
        }
    }

    public static void v(final String tag) {
        LogUtil.v(tag, null);
    }

    public static void i(final String tag, final String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void w(final String tag, final String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }
    
    public static void e(final String tag, final String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }
    public static void d(final String tag, final String msg) {
    	if (DEBUG) {
    		Log.d(tag, msg);
    	}
    }

}
