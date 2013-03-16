package com.yenhsun.floatingshortcut;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShortcutsControlPanel extends LinearLayout {

	private ShortcutsControlPanel mQuickPanel;

	private Context mContext;

	private WindowManager.LayoutParams wmParams;

	private PackageManager pm;

	private WindowManager wm;

	private int mPanelHeight;

	private int mPanelWidth;

	private static final int ICON_MARGIN = 20;

	private static final int AMOUNT_OF_ICONS = 6;

	private int x, y;

	private void initScreenPosition() {
		mPanelHeight = FloatingShortcut.sComponentHeight + ICON_MARGIN * 2;
		mPanelWidth = mPanelHeight * AMOUNT_OF_ICONS;

		x = (mContext.getResources().getDisplayMetrics().widthPixels - mPanelWidth) / 2;
		y = (mContext.getResources().getDisplayMetrics().heightPixels - mPanelHeight) / 2;
	}

	public ShortcutsControlPanel(Context context) {
		super(context);
		this.mContext = context;
		initScreenPosition();
		mQuickPanel = this;
		wm = (WindowManager) mContext.getSystemService("window");
		pm = mContext.getPackageManager();
		wmParams = getDefaultWindowManagerParamsSettings();
		this.setBackgroundColor(Color.argb(40, 240, 255, 240));
		createIcon();
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
					wm.removeView(mQuickPanel);
				return false;
			}
		});
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

	private void createIcon() {
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> packages = pm.queryIntentActivities(intent,
				PackageManager.PERMISSION_GRANTED);

		LayoutParams l = new LayoutParams(FloatingShortcut.sComponentHeight,
				FloatingShortcut.sComponentHeight);
		l.setMargins(ICON_MARGIN, ICON_MARGIN, ICON_MARGIN, ICON_MARGIN);
		for (int i = 5; i < packages.size() && i < 5 + AMOUNT_OF_ICONS; i++) {
			QTextView qt = new QTextView(mContext);
			qt.packageName = packages.get(i).activityInfo.packageName;
			qt.className = packages.get(i).activityInfo.name;
			qt.label = packages.get(i).loadLabel(pm).toString();
			qt.setImageDrawable(packages.get(i).loadIcon(pm));

			qt.setLayoutParams(l);
			addView(qt);
		}
	}

	private WindowManager.LayoutParams getDefaultWindowManagerParamsSettings() {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.x = x;
		params.y = y;
		params.height = mPanelHeight;
		params.width = mPanelWidth;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.format = PixelFormat.RGBA_8888;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		params.gravity = Gravity.LEFT | Gravity.TOP;
		return params;
	}

	public class QTextView extends ImageView {

		private Context mContext;

		public String packageName;

		public String className;

		public String label;

		public Bitmap icon;

		public QTextView(Context context) {
			super(context);
			mContext = context;
			this.setOnClickListener(new TextView.OnClickListener() {

				@Override
				public void onClick(View v) {
					close();
					Intent startIntent = pm
							.getLaunchIntentForPackage(packageName);
					startIntent.setComponent(new ComponentName(packageName,
							className));
					mContext.startActivity(startIntent);
				}
			});
		}
	}
}
