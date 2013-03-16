package com.yenhsun.floatingshortcut;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
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

	private ShortcutDatabase mShortcutDatabase;

	private ArrayList<DataHolder> mDataList = new ArrayList<DataHolder>();

	private void initScreenPosition() {
		mPanelHeight = FloatingShortcut.sComponentHeight + ICON_MARGIN * 2;
		mPanelWidth = mPanelHeight * AMOUNT_OF_ICONS;

		x = (mContext.getResources().getDisplayMetrics().widthPixels - mPanelWidth) / 2;
		y = (mContext.getResources().getDisplayMetrics().heightPixels - mPanelHeight) / 2;
	}

	public ShortcutsControlPanel(Context context) {
		super(context);
		this.mContext = context;
		mShortcutDatabase = new ShortcutDatabase(mContext);
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
		createIcon();
		wm.addView(this, wmParams);
	}

	public void close() {
		wm.removeView(this);
	}

	private void createIcon() {
		fillInDataList();

		LayoutParams l = new LayoutParams(FloatingShortcut.sComponentHeight,
				FloatingShortcut.sComponentHeight);
		l.setMargins(ICON_MARGIN, ICON_MARGIN, ICON_MARGIN, ICON_MARGIN);
		for (int i = 0; i < mDataList.size()
				&& mDataList.size() <= AMOUNT_OF_ICONS; i++) {
			QTextView qt = new QTextView(mContext);
			qt.packageName = mDataList.get(i).pkgName;
			qt.className = mDataList.get(i).clzName;
			qt.label = mDataList.get(i).appLabel.toString();
			qt.setImageDrawable(mDataList.get(i).appIconDrawable);

			qt.setLayoutParams(l);
			addView(qt);
		}
	}

	private void fillInDataList() {
		mDataList.clear();
		Cursor apps = mShortcutDatabase.query();
		while (apps.moveToNext()) {
			DataHolder dh = new DataHolder();
			dh.pkgName = apps.getString(0);
			dh.clzName = apps.getString(1);
		}
		apps.close();
		fillInDataListIcon();
	}

	private void fillInDataListIcon() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> pkgAppsList = mContext.getPackageManager()
				.queryIntentActivities(mainIntent, 0);
		for (ResolveInfo info : pkgAppsList) {
			for (DataHolder dh : mDataList) {
				if (info.activityInfo.packageName.equals(dh.pkgName)) {
					if (info.activityInfo.name.equals(dh.clzName)) {
						dh.appIconDrawable = info.loadIcon(mContext
								.getPackageManager());
					}
				} else {
					break;
				}
			}
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
