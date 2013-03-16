package com.yenhsun.floatingshortcut;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class FloatingShortcutService extends Service {
	private static final String TAG = "yen.hsun.fakeiphonedot"
			+ FloatingShortcutService.class.getName();

	private static final boolean DEBUG = true;

	private FloatingShortcut mFloatingShortcut;

	@Override
	public void onCreate() {
		super.onCreate();
		if (DEBUG)
			Log.i(TAG, "start Floating shortcut service");
		mFloatingShortcut = new FloatingShortcut(this);
		this.startForeground(FloatingShortcutService.class.hashCode(),
				createNotification());
	}

	private Notification createNotification() {
		Notification noti = new Notification.Builder(this)
				.setContentTitle("Floating Shortcut")
				.setContentText("Floating Shortcut Manager")
				.setSmallIcon(R.drawable.ic_launcher).build();
		return noti;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
