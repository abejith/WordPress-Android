package org.wordpress.android.ui.sharing.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.VolleyError;
import com.wordpress.rest.RestRequest;

import org.json.JSONObject;
import org.wordpress.android.WordPress;
import org.wordpress.android.datasets.SharingTable;
import org.wordpress.android.models.SharingServiceList;
import org.wordpress.android.ui.reader.SharingEvents;
import org.wordpress.android.util.AppLog;

import de.greenrobot.event.EventBus;

/**
 * service which requests the user's available sharing services
 */

public class SharingUpdateService extends Service {

    public static void startService(Context context) {
        Intent intent = new Intent(context, SharingUpdateService.class);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppLog.i(AppLog.T.SHARING, "sharing update service > created");
    }

    @Override
    public void onDestroy() {
        AppLog.i(AppLog.T.SHARING, "sharing update service > destroyed");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        updateServices();

        return START_NOT_STICKY;
    }

    private void updateServices() {
        com.wordpress.rest.RestRequest.Listener listener = new RestRequest.Listener() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                handleResponse(jsonObject);
            }
        };
        RestRequest.ErrorListener errorListener = new RestRequest.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                AppLog.e(AppLog.T.SHARING, volleyError);
            }
        };

        WordPress.getRestClientUtilsV1_1().get("meta/publicize/", null, null, listener, errorListener);
    }

    private void handleResponse(final JSONObject json) {
        new Thread() {
            @Override
            public void run() {
                SharingServiceList serverList = SharingServiceList.fromJson(json);
                SharingServiceList localList = SharingTable.getServiceList();
                if (!serverList.isSameList(localList)) {
                    AppLog.d(AppLog.T.SHARING, "sharing update service > services changed");
                    SharingTable.setServiceList(serverList);
                    EventBus.getDefault().post(new SharingEvents.SharingServicesChanged());
                }
            }
        }.start();
    }

}
