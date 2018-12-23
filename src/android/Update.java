package com.yanglb.cordova;

import android.content.pm.PackageManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import ezy.boost.update.UpdateManager;

public class Update extends CordovaPlugin {
    private static final String TAG = Update.class.getName();

    // js接口相关
    private static final String ACTION_CHECK = "check";
    private static final String ACTION_MANUAL_CHECK = "manual-check";

    public boolean execute(String action, final JSONArray args, final CallbackContext callback) throws JSONException {
        if (ACTION_CHECK.equals(action)) {
            checkUpdate(false, args.getString(0), args.getBoolean(1));
            return true;
        } else if (ACTION_MANUAL_CHECK.equals(action)) {
            checkUpdate(true, args.getString(0), args.getBoolean(1));
            return true;
        }
        return false;
    }

    private void checkUpdate(boolean isManual, String apiAddress, boolean wifiOnly) {
        String url = String.format("%s?versionName=%s&from=cordova-update",
                apiAddress,
                getVersionName());

        Log.d(TAG, "检查更新: " + url);

        UpdateManager.setUrl(url, "cordova-update");
        UpdateManager.setWifiOnly(wifiOnly);
        if (isManual) {
            UpdateManager.checkManual(cordova.getActivity());
        } else {
            UpdateManager.check(cordova.getActivity());
        }
    }

    private String getVersionName() {
        PackageManager packageManager = this.cordova.getActivity().getPackageManager();
        try {
            String vn = packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionName;
            return vn;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0.0.0";
        }
    }
}
