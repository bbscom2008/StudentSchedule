package zz.itcast.studentschedule.task;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import zz.itcast.studentschedule.activity.MainActivity;
import zz.itcast.studentschedule.bean.Version;
import zz.itcast.studentschedule.utils.HttpUtils;
import zz.itcast.studentschedule.utils.LogUtils;
import zz.itcast.studentschedule.utils.MyFinal;

public class AutoCheckUpdateTask  extends AsyncTask {

	private Version version;
	private MainActivity act;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Object doInBackground(Object[] params) {
		act = (MainActivity) params[0];

		String response = HttpUtils.getInstance().getSyn(MyFinal.versionUrl);
		LogUtils.logleo("json:" + response);
		version = new Version();

		try {
			JSONObject jObj = new JSONObject(response);
			version.apk_version = jObj.getInt("apk_version");
			version.data_version = jObj.getInt("data_version");
			version.desc = jObj.getString("apk_desc");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return version;
	}

	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);

		try {
			PackageInfo info = null;
			info = act.getPackageManager().getPackageInfo(act.getPackageName(), 0);
			// 服务器版本大于本地版本，或，数据版本不一致，就提示更新
			if (version.apk_version > info.versionCode ) {
				version.isHaveNewApk = true;
			}else if(!checkDataVersion()){
				version.isHaveNewKeBiao = true;
			}
			act.versionResult(version);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查数据课表版本是否是最新
	 * @return true 和服务器一致
	 */
	private boolean checkDataVersion() {
		SharedPreferences sp = act.sp;
		int dataV = sp.getInt(MyFinal.key_data_version, -1);
		if (version.data_version == dataV) {
			return true;
		}
		return false;
	}

}
