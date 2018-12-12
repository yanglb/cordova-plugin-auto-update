package com.yanglb.cordova;

import android.content.pm.PackageManager;
import android.util.Log;

import org.apache.cordova.BuildConfig;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ezy.boost.update.UpdateManager;

public class Update extends CordovaPlugin {
    private static final String TAG = Update.class.getName();

    // js接口相关
    private static final String ACTION_CHECK = "check";
    private static final String ACTION_MANUAL_CHECK = "manual-check";

    private CallbackContext callbackContext;
    
    public boolean execute(String action, final JSONArray args, final CallbackContext callback) throws JSONException {
        this.callbackContext = callback;
        if (ACTION_CHECK.equals(action)) {
            checkUpdate(false, args.getString(0));
            return true;
        } else if (ACTION_MANUAL_CHECK.equals(action)) {
            checkUpdate(true, args.getString(0));
            return true;
        }
        return false;
    }

    private void erroResponse(int code, String message) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", code);
            jsonObject.put("message", message);
            if (callbackContext != null) {
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, jsonObject);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkUpdate(boolean isManual, String apiAddress) {
        String url = String.format("%s?versionName=%s&from=cordova-auto-update&v=",
                apiAddress,
                getVersionName(),
                BuildConfig.VERSION_NAME);

        Log.d(TAG, "检查更新: " + url);

        UpdateManager.create(this.cordova.getActivity())
                .setUrl(url)
                .setNotifyId(100)
                .setWifiOnly(false)
                .setManual(isManual)
                .check();
    }

    private String getVersionName() {
        PackageManager packageManager = this.cordova.getActivity().getPackageManager();
        try {
            String vn = packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionName;
            return "1.2.0";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
