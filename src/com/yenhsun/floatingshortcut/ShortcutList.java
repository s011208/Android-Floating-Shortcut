package com.yenhsun.floatingshortcut;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShortcutList extends ListActivity {
	public static final String EXTRA_PKG = "PACKAGENAME";

	public static final String EXTRA_CLZ = "CLASSNAME";

	private ArrayList<DataHolder> mAppList = new ArrayList<DataHolder>();

	private AppListAdapter mAppListAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initAppList();
		mAppListAdapter = new AppListAdapter(this);

		this.setListAdapter(mAppListAdapter);
	}

	protected void onListItemClick(ListView l, View view, int position, long id) {
		ViewHolder temp = ((ViewHolder) view.getTag());

		Intent rtn = new Intent();
		rtn.putExtra(EXTRA_PKG, temp.pkgName);
		rtn.putExtra(EXTRA_CLZ, temp.clzName);
		this.setResult(RESULT_OK, rtn);
		finish();
	}

	private void initAppList() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> pkgAppsList = getPackageManager()
				.queryIntentActivities(mainIntent, 0);
		for (ResolveInfo info : pkgAppsList) {
			DataHolder dh = new DataHolder();
			dh.appIconDrawable = info.loadIcon(getPackageManager());
			dh.appLabel = info.loadLabel(getPackageManager());
			dh.clzName = info.activityInfo.name;
			dh.pkgName = info.activityInfo.packageName;
			mAppList.add(dh);
		}
	}



	public final class ViewHolder {

		public ImageView appIcon;

		public String clzName;

		public String pkgName;

		public TextView appLabel;

	}

	class AppListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public AppListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mAppList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.configure_app_list,
						null);
				holder.appIcon = (ImageView) convertView
						.findViewById(R.id.icon);
				holder.appLabel = (TextView) convertView
						.findViewById(R.id.label);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.appIcon
					.setBackgroundDrawable(mAppList.get(position).appIconDrawable);
			holder.clzName = mAppList.get(position).clzName;
			holder.pkgName = mAppList.get(position).pkgName;
			holder.appLabel.setText(mAppList.get(position).appLabel);

			return convertView;
		}

	}
}
