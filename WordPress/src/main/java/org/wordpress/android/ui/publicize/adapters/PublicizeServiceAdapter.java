package org.wordpress.android.ui.publicize.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wordpress.android.R;
import org.wordpress.android.datasets.PublicizeTable;
import org.wordpress.android.models.AccountHelper;
import org.wordpress.android.models.PublicizeConnection;
import org.wordpress.android.models.PublicizeConnectionList;
import org.wordpress.android.models.PublicizeService;
import org.wordpress.android.models.PublicizeServiceList;
import org.wordpress.android.ui.publicize.ConnectButton;
import org.wordpress.android.ui.publicize.ConnectButton.ConnectAction;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.AppLog.T;
import org.wordpress.android.util.PhotonUtils;
import org.wordpress.android.util.ToastUtils;
import org.wordpress.android.widgets.WPNetworkImageView;

import java.util.Collections;
import java.util.Comparator;

public class PublicizeServiceAdapter extends RecyclerView.Adapter<PublicizeServiceAdapter.SharingViewHolder> {
    private final PublicizeServiceList mServices = new PublicizeServiceList();
    private final PublicizeConnectionList mConnections = new PublicizeConnectionList();
    private final int mSiteId;
    private final int mAvatarSz;
    private final long mCurrentUserId;

    public PublicizeServiceAdapter(Context context, int siteId) {
        super();
        mSiteId = siteId;
        mAvatarSz = context.getResources().getDimensionPixelSize(R.dimen.avatar_sz_extra_small);
        mCurrentUserId = AccountHelper.getDefaultAccount().getUserId();
        setHasStableIds(true);
    }

    public void refresh() {
        if (mIsTaskRunning) {
            AppLog.w(T.SHARING, "sharing task is already running");
            return;
        }
        new LoadTagsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int getItemCount() {
        return mServices.size();
    }

    public boolean isEmpty() {
        return (getItemCount() == 0);
    }

    @Override
    public long getItemId(int position) {
        return mServices.get(position).getId().hashCode();
    }

    @Override
    public SharingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.publicize_listitem, parent, false);
        return new SharingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SharingViewHolder holder, int position) {
        final PublicizeService service = mServices.get(position);
        final PublicizeConnection connection = mConnections.getServiceConnectionForUser(mCurrentUserId, service);

        String iconUrl = PhotonUtils.getPhotonImageUrl(service.getIconUrl(), mAvatarSz, mAvatarSz);
        holder.imgIcon.setImageUrl(iconUrl, WPNetworkImageView.ImageType.BLAVATAR);

        holder.txtConnection.setText(service.getLabel());
        if (connection != null && connection.hasExternalDisplayName()) {
            holder.txtUser.setText(connection.getExternalDisplayName());
            holder.txtUser.setVisibility(View.VISIBLE);
        } else {
            holder.txtUser.setVisibility(View.GONE);
        }

        ConnectAction action = ConnectAction.CONNECT;
        if (connection == null) {
            action = ConnectAction.CONNECT;
        } else if (connection.getStatusEnum() == PublicizeConnection.ConnectStatus.BROKEN) {
            action = ConnectAction.RECONNECT;
        } else {
            action = ConnectAction.DISCONNECT;
        }
        holder.btnConnect.setConnectAction(action);
        holder.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                ToastUtils.showToast(v.getContext(), "not implemented yet");
            }
        });
    }

    class SharingViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtConnection;
        private final TextView txtUser;
        private final ConnectButton btnConnect;
        private final WPNetworkImageView imgIcon;

        public SharingViewHolder(View view) {
            super(view);
            txtConnection = (TextView) view.findViewById(R.id.text_connection);
            txtUser = (TextView) view.findViewById(R.id.text_user);
            btnConnect = (ConnectButton) view.findViewById(R.id.button_connect);
            imgIcon = (WPNetworkImageView) view.findViewById(R.id.image_icon);
        }
    }

    /*
     * AsyncTask to load services
     */
    private boolean mIsTaskRunning = false;
    private class LoadTagsTask extends AsyncTask<Void, Void, Boolean> {
        private PublicizeServiceList tmpServices;
        private PublicizeConnectionList tmpConnections;

        @Override
        protected void onPreExecute() {
            mIsTaskRunning = true;
        }
        @Override
        protected void onCancelled() {
            mIsTaskRunning = false;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            tmpServices = PublicizeTable.getServiceList();
            tmpConnections = PublicizeTable.getConnectionsForSite(mSiteId);
            return !(tmpServices.isSameAs(mServices) && tmpConnections.isSameAs(mConnections));
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                mServices.clear();
                mServices.addAll(tmpServices);

                mConnections.clear();
                mConnections.addAll(tmpConnections);
                sortConnections();

                notifyDataSetChanged();
            }
            mIsTaskRunning = false;
        }

        /*
         * sort connected services to the top
         */
        private void sortConnections() {
            Collections.sort(mServices, new Comparator<PublicizeService>() {
                @Override
                public int compare(PublicizeService lhs, PublicizeService rhs) {
                    boolean isLhsConnected = mConnections.isServiceConnectedForUser(mCurrentUserId, lhs);
                    boolean isRhsConnected = mConnections.isServiceConnectedForUser(mCurrentUserId, rhs);
                    if (isLhsConnected && !isRhsConnected) {
                        return -1;
                    } else if (isRhsConnected && !isLhsConnected) {
                        return 1;
                    } else {
                        return lhs.getLabel().compareToIgnoreCase(rhs.getLabel());
                    }
                }
            });
        }
    }

}
