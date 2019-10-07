package com.doov.secure.ui.activity;

import com.doos.secure.R;
import com.doos.secure.R.layout;
import com.doov.secure.comm.utils.IconUtils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class TransparentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transparent);
		IconUtils.showIcon(true, this);
		finish();
	}
}
