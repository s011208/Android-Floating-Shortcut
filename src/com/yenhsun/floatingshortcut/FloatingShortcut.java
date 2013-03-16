package com.yenhsun.floatingshortcut;

import com.yenhsun.floatingshortcut.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.widget.ImageView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager.LayoutParams;

public class FloatingShortcut extends ImageView implements OnTouchListener {

	private GestureListenerEvent mGestureListenerEvent;
	private GestureDetector mGDetector;

	private int mScreenWidth, mScreenHeight;

	class GestureListenerEvent implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			lastMotionEvent = MotionEvent.obtain(e);
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			mSettingPanel.show();
		}

		private MotionEvent lastMotionEvent;

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			wmParams.x += (int) (e2.getRawX() - lastMotionEvent.getRawX());
			wmParams.y += (int) (e2.getRawY() - lastMotionEvent.getRawY());

			wm.updateViewLayout(mFloatingShortcut, wmParams);
			lastMotionEvent = MotionEvent.obtain(e2);
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			mControlPanel.show();
			return false;
		}
	}

	private FloatingShortcut mFloatingShortcut;

	private ShortcutsControlPanel mControlPanel;

	private ShortcutsSettingPanel mSettingPanel;

	private static final String TAG = "com.yenhsun.floatingshortcut.floatingshortcut";

	private static final boolean DEBUG = true;

	public static final String PREFS_PRIVATE = "PREFS_PRIVATE";

	public static final String XKey = "X";

	public static final String YKey = "Y";

	private Context mContext;

	private WindowManager.LayoutParams wmParams;

	private WindowManager wm;

	public static boolean isShown = false;

	private SharedPreferences sharedPreferences;

	protected static int sComponentHeight;

	@SuppressWarnings("deprecation")
	public FloatingShortcut(Context context) {
		super(context);
		mContext = context;
		sharedPreferences = mContext.getSharedPreferences(PREFS_PRIVATE,
				Context.MODE_PRIVATE);
		setBackgroundResource(R.drawable.ic_launcher);
		mGestureListenerEvent = new GestureListenerEvent();
		mGDetector = new GestureDetector(mGestureListenerEvent);
		mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
		mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
		sComponentHeight = mScreenWidth / 10;
		this.setOnTouchListener(this);
		wm = (WindowManager) mContext.getSystemService("window");
		wmParams = getDefaultWindowManagerParamsSettings();
		wm.addView(this, wmParams);

		isShown = true;
		mFloatingShortcut = this;
		mControlPanel = new ShortcutsControlPanel(mContext);
		mSettingPanel = new ShortcutsSettingPanel(mContext);
		mSettingPanel.setFloatingShortcut(this);
	}

	public WindowManager.LayoutParams getDefaultWindowManagerParamsSettings() {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.x = sharedPreferences.getInt(XKey, 0);
		params.y = sharedPreferences.getInt(YKey, 200);
		params.height = sComponentHeight;
		params.width = sComponentHeight;
		params.type = LayoutParams.TYPE_PHONE;
		params.format = PixelFormat.RGBA_8888;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
				| WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;

		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.horizontalWeight = 0;
		params.verticalWeight = 0;
		params.windowAnimations = android.R.style.Animation_Toast;
		params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;

		return params;
	}

	public WindowManager.LayoutParams getWMParams() {
		return wmParams;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (MotionEvent.ACTION_UP == event.getAction()) {
			Editor prefsPrivateEditor = sharedPreferences.edit();
			prefsPrivateEditor.putInt(XKey, wmParams.x);
			prefsPrivateEditor.putInt(YKey, wmParams.y);
			prefsPrivateEditor.commit();
		}
		return mGDetector.onTouchEvent(event);
	}

	public void showFloatingShortcut() {
		if (isShown)
			wm.removeView(this);
		wm.addView(this, wmParams);
	}
}
