package com.yenhsun.floatingshortcut;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ShortcutsConfigurationActivity extends Activity implements
		OnClickListener {

	private ShortcutDatabase mShortcutDatabase;

	private LinearLayout mItemListView;

	private ImageButton mAddBtn;

	private ArrayList<DataHolder> mDataList = new ArrayList<DataHolder>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_settings);
		mShortcutDatabase = new ShortcutDatabase(this);

		fillInDataList();

		mItemListView = (LinearLayout) findViewById(R.id.items_list);
		mAddBtn = new ImageButton(this);
		mAddBtn.setBackgroundResource(R.drawable.add_img);
		mAddBtn.setOnClickListener(this);

		mItemListView.addView(mAddBtn);
	}

	private void refreshDatabase() {
		mShortcutDatabase.clearTable();
		ContentValues[] cv = new ContentValues[6];
		for (int i = 0; i < mDataList.size() && mDataList.size() <= 6; i++) {
			cv[i] = new ContentValues();
			cv[i].put(ShortcutDatabase.COLUMN_PACKAGENAME,
					mDataList.get(i).pkgName);
			cv[i].put(ShortcutDatabase.COLUMN_CLASSNAME,
					mDataList.get(i).clzName);
		}
		mShortcutDatabase.insertShortcuts(cv);
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
		List<ResolveInfo> pkgAppsList = getPackageManager()
				.queryIntentActivities(mainIntent, 0);
		for (ResolveInfo info : pkgAppsList) {
			for (DataHolder dh : mDataList) {
				if (info.activityInfo.packageName.equals(dh.pkgName)) {
					if (info.activityInfo.name.equals(dh.clzName)) {
						dh.appIconDrawable = info.loadIcon(getPackageManager());
					}
				} else {
					break;
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String pkgName = data.getStringExtra(ShortcutList.EXTRA_PKG);
		String clzName = data.getStringExtra(ShortcutList.EXTRA_CLZ);
		ContentValues cv = new ContentValues();
		cv.put(ShortcutDatabase.COLUMN_PACKAGENAME, pkgName);
		cv.put(ShortcutDatabase.COLUMN_CLASSNAME, clzName);
		mShortcutDatabase.insertShortcuts(cv);

	}

	@Override
	public void onClick(View v) {
		Log.i("QQQQ", "click");
		Intent startIntent = new Intent();
		startIntent.setComponent(new ComponentName(
				"com.yenhsun.floatingshortcut",
				"com.yenhsun.floatingshortcut.ShortcutList"));
		this.startActivityForResult(startIntent, 0);
	}
}
