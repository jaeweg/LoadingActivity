package com.doov.secure.ui.dialog;



import com.doos.secure.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

public class LoadingDialog extends Dialog {

	public LoadingDialog(Context context) {
		super(context, R.style.dialog);
		
	}
	
	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.load_dialog);
		setCancelable(false);
		
	}

}
