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
import org.wordpress.android.models.PublicizeConnection;
import org.wordpress.android.models.PublicizeConnectionList;
import org.wordpress.android.models.PublicizeService;
import org.wordpress.android.models.PublicizeServiceList;
import org.wordpress.android.ui.publicize.ConnectButton;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.AppLog.T;
import org.wordpress.android.widgets.WPNetworkImageView;

public class PublicizeServiceAdapter extends RecyclerView.Adapter<PublicizeServiceAdapter.SharingViewHolder> {
    private final PublicizeServiceList mServices = new PublicizeServiceList();
    private final PublicizeConnectionList mConnections = new PublicizeConnectionList();
    private final int mRemoteBlogId;

    public PublicizeServiceAdapter(Context context, int remoteBlogId) {
        super();
        mRemoteBlogId = remoteBlogId;
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
        return mServices.get(position).getName().hashCode();
    }

    @Override
    public SharingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.publicize_listitem, parent, false);
        return new SharingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SharingViewHolder holder, int position) {
        final PublicizeService service = mServices.get(position);

        holder.txtLabel.setText(service.getLabel());
        holder.txtDescription.setText(service.getDescription());
        holder.imgIcon.setImageUrl(service.getIconUrl(), WPNetworkImageView.ImageType.BLAVATAR);

        holder.btnConnect.setConnectState(isServiceConnected(service) ? ConnectButton.ConnectState.DISCONNECT : ConnectButton.ConnectState.CONNECT);
        holder.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }

    private boolean isServiceConnected(PublicizeService service) {
        for (PublicizeConnection connection: mConnections) {
            if (connection.getService().equals(service.getName())) {
                return true;
            }
        }
        return false;
    }

    class SharingViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtLabel;
        private final TextView txtDescription;
        private final ConnectButton btnConnect;
        private final WPNetworkImageView imgIcon;

        public SharingViewHolder(View view) {
            super(view);
            txtLabel = (TextView) view.findViewById(R.id.text_label);
            txtDescription = (TextView) view.findViewById(R.id.text_description);
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
            tmpConnections = PublicizeTable.getConnectionsForSite(mRemoteBlogId);
            return !(tmpServices.isSameAs(mServices) && tmpConnections.isSameAs(mConnections));
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                mServices.clear();
                mServices.addAll(tmpServices);
                mConnections.clear();
                mConnections.addAll(tmpConnections);
                notifyDataSetChanged();
            }
            mIsTaskRunning = false;
        }
    }

}
