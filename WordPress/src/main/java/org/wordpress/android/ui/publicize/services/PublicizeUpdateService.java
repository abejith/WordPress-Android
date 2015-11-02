package org.wordpress.android.ui.publicize.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.VolleyError;
import com.wordpress.rest.RestRequest;

import org.json.JSONObject;
import org.wordpress.android.WordPress;
import org.wordpress.android.datasets.PublicizeTable;
import org.wordpress.android.models.PublicizeConnectionList;
import org.wordpress.android.models.PublicizeServiceList;
import org.wordpress.android.ui.reader.PublicizeEvents;
import org.wordpress.android.util.AppLog;

import de.greenrobot.event.EventBus;

/**
 * service which requests the user's available sharing services
 */

public class PublicizeUpdateService extends Service {

    private static final String ARG_REMOTE_BLOG_ID = "blog_id";

    public static void updatePublicizeServices(Context context) {
        Intent intent = new Intent(context, PublicizeUpdateService.class);
        context.startService(intent);
    }

    public static void updateConnectionsForBlog(Context context, int remoteBlogId) {
        Intent intent = new Intent(context, PublicizeUpdateService.class);
        intent.putExtra(ARG_REMOTE_BLOG_ID, remoteBlogId);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppLog.i(AppLog.T.SHARING, "publicize update service > created");
    }

    @Override
    public void onDestroy() {
        AppLog.i(AppLog.T.SHARING, "publicize update service > destroyed");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        // if a blogId is passed we're updating connections for that blog, otherwise we're updating
        // the list of known publicize services
        if (intent.hasExtra(ARG_REMOTE_BLOG_ID)) {
            int remoteBlogId = intent.getIntExtra(ARG_REMOTE_BLOG_ID, 0);
            updateConnections(remoteBlogId);
        } else {
            updateServices();
        }

        return START_NOT_STICKY;
    }

    /*
     * update the list of publicize services
     */
    private void updateServices() {
        com.wordpress.rest.RestRequest.Listener listener = new RestRequest.Listener() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                handleUpdateServicesResponse(jsonObject);
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

    private void handleUpdateServicesResponse(final JSONObject json) {
        if (json == null) return;

        new Thread() {
            @Override
            public void run() {
                PublicizeServiceList serverList = PublicizeServiceList.fromJson(json);
                PublicizeServiceList localList = PublicizeTable.getServiceList();
                if (!serverList.isSameAs(localList)) {
                    PublicizeTable.setServiceList(serverList);
                    EventBus.getDefault().post(new PublicizeEvents.PublicizeServicesChanged());
                }
            }
        }.start();
    }

    /*
     * update the connections for the passed blog
     */
    private void updateConnections(final int remoteBlogId) {
        com.wordpress.rest.RestRequest.Listener listener = new RestRequest.Listener() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                handleUpdateConnectionsResponse(remoteBlogId, jsonObject);
            }
        };
        RestRequest.ErrorListener errorListener = new RestRequest.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                AppLog.e(AppLog.T.SHARING, volleyError);
            }
        };

        String path = String.format("sites/%d/publicize-connections", remoteBlogId);
        WordPress.getRestClientUtilsV1_1().get(path, null, null, listener, errorListener);
    }

    private void handleUpdateConnectionsResponse(final int remoteBlogId, final JSONObject json) {
        if (json == null) return;

        new Thread() {
            @Override
            public void run() {
                PublicizeConnectionList serverList = PublicizeConnectionList.fromJson(json);
                PublicizeConnectionList localList = PublicizeTable.getConnectionsForSite(remoteBlogId);
                if (!serverList.isSameAs(localList)) {
                    PublicizeTable.setConnectionsForSite(remoteBlogId, serverList);
                    EventBus.getDefault().post(new PublicizeEvents.PublicizeConnectionsChanged());
                }
            }
        }.start();
    }

}
