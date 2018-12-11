package com.yanglb.cordova;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.qiniu.pili.droid.rtcstreaming.RTCMediaStreamingManager;
import com.qiniu.pili.droid.rtcstreaming.RTCServerRegion;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Update extends CordovaPlugin {
    private static final String TAG = Update.class.getName();

    // js接口相关
    private static final String ACTION_INIT = "init";
    private static final String ACTION_CHECK = "check";

    private static boolean isInited = false;
    private CallbackContext callbackContext;
    
    public boolean execute(String action, final JSONArray args, final CallbackContext callback) throws JSONException {
        this.callbackContext = callback;
        if (ACTION_INIT.equals(action)) {
            Log.d(TAG, "初始化，之前是否已经初始化过: " + isInited);
            if (!isInited) {
                isInited = true;
                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RTCMediaStreamingManager.init(cordova.getActivity().getApplicationContext(), RTCServerRegion.RTC_CN_SERVER);
                    }
                });
            }
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
            return true;

        } else if (ACTION_JOIN_CONFERENCE.equals(action)) {
            if (!hasPermission()) {
                Log.d(TAG, "无权限访问");
                requestPermission();
                erroResponse(401, "无权访问");
                return true;
            }

            final JSONObject params = args.getJSONObject(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        joinConference(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                        erroResponse(500, e.getMessage());
                    }
                }
            });

            return true;
        } else if (ACTION_STOP_CONFERENCE.equals(action)) {
            PiliRtcAgent.getInstance().stopConference();
            callbackContext.success();
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

    private void joinConference(final JSONObject params) throws JSONException {
        String userId = params.getString("userId");
        String roomName = params.getString("roomName");
        String roomToken = params.getString("roomToken");
        int timeout = 30 * 1000;
        if (params.has("timeout")) {
            timeout = params.getInt("timeout");
        }

        Log.d(TAG, "====== 开始加入会议 ======");
        PiliRtcAgent.getInstance().joinConference(cordova.getActivity().getApplication(), userId, roomName, roomToken, timeout, new IPiliRtcAgentCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "PiliRtcAgent.onSuccess");
                if (callbackContext != null) {
                    PluginResult result = new PluginResult(PluginResult.Status.OK);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                throwable.printStackTrace();
                erroResponse(500, throwable.getMessage());
            }

            @Override
            public void onEnd() {
                Log.d(TAG, "====== 会议结束 ======");
                erroResponse(200, "通话结束");
            }
        });
    }

    @SuppressLint("InlinedApi")
    private boolean hasPermission() {
        return Build.VERSION.SDK_INT < 23 ||
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this.cordova.getActivity(), Manifest.permission.RECORD_AUDIO);
    }

    @SuppressLint("InlinedApi")
    private void requestPermission() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(
                this.cordova.getActivity(),
                new String[] {Manifest.permission.RECORD_AUDIO},
                PERMISSION_REQUEST_CODE);
        }
    }
}
