package com.yenhsun.floatingshortcut;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class ShortcutsSettingPanel extends LinearLayout {

	private Context mContext;

	private ShortcutsSettingPanel mSettingPanel;

	private FloatingShortcut mFloatingShortcut;

	private WindowManager wm;

	private WindowManager.LayoutParams wmParams;

	private Button mSettingButton;

	private Button mCloseButton;

	private int mScreenWidth, mScreenHeight;

	private static final int PANEL_HEIGHT = 200;

	private static final int PANEL_WIDTH = 400;

	public ShortcutsSettingPanel(Context context) {
		super(context);
		mContext = context;
		mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		mScreenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
		wm = (WindowManager) mContext.getSystemService("window");
		wmParams = getDefaultWindowManagerParamsSettings();
		this.setOrientation(LinearLayout.VERTICAL);
		mCloseButton = new Button(mContext);
		mCloseButton.setText("Close FloatingShortCut");
		mSettingPanel = this;
		mCloseButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				closeFloatingShortCut();
			}
		});
		addView(mCloseButton);

		mSettingButton = new Button(mContext);
		mSettingButton.setText("Settings");
		mSettingButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				close();
				Intent startIntent = new Intent();
				startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startIntent.setComponent(new ComponentName(
						"com.yenhsun.floatingshortcut",
						"com.yenhsun.floatingshortcut.ShortcutsConfigurationActivity"));
				mContext.startActivity(startIntent);
			}
		});
		addView(mSettingButton);
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
					close();
				return false;
			}
		});
	}

	public void setFloatingShortcut(FloatingShortcut fd) {
		this.mFloatingShortcut = fd;
	}

	private WindowManager.LayoutParams getDefaultWindowManagerParamsSettings() {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.x = (mScreenWidth - PANEL_WIDTH) / 2;
		params.y = (mScreenHeight - PANEL_HEIGHT) / 2;
		params.height = PANEL_HEIGHT;
		params.width = PANEL_WIDTH;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.format = PixelFormat.RGBA_8888;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		params.gravity = Gravity.LEFT | Gravity.TOP;
		return params;
	}

	public WindowManager.LayoutParams getWMParams() {
		return wmParams;
	}

	public void show() {
		wm.addView(this, wmParams);
	}

	public void close() {
		wm.removeView(this);
	}

	public void closeFloatingShortCut() {
		if (FloatingShortcut.isShown == false)
			return;
		else {
			wm.removeView(mFloatingShortcut);
			wm.removeView(mSettingPanel);
			FloatingShortcut.isShown = false;
			mContext.stopService(new Intent(mContext,
					FloatingShortcutService.class));
		}
	}

}
