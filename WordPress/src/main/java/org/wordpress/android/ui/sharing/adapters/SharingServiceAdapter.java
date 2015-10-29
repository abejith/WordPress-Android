package org.wordpress.android.ui.sharing.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wordpress.android.R;
import org.wordpress.android.datasets.SharingTable;
import org.wordpress.android.models.SharingService;
import org.wordpress.android.models.SharingServiceList;
import org.wordpress.android.ui.sharing.ShareConnectButton;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.AppLog.T;
import org.wordpress.android.widgets.WPNetworkImageView;

public class SharingServiceAdapter extends RecyclerView.Adapter<SharingServiceAdapter.SharingViewHolder> {
    private final SharingServiceList mServices = new SharingServiceList();

    public SharingServiceAdapter(Context context) {
        super();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sharing_listitem_service, parent, false);
        return new SharingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SharingViewHolder holder, int position) {
        final SharingService service = mServices.get(position);
        holder.txtLabel.setText(service.getLabel());
        holder.txtDescription.setText(service.getDescription());
        holder.imgIcon.setImageUrl(service.getIconUrl(), WPNetworkImageView.ImageType.BLAVATAR);

        holder.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }

    class SharingViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtLabel;
        private final TextView txtDescription;
        private final ShareConnectButton btnConnect;
        private final WPNetworkImageView imgIcon;

        public SharingViewHolder(View view) {
            super(view);
            txtLabel = (TextView) view.findViewById(R.id.text_label);
            txtDescription = (TextView) view.findViewById(R.id.text_description);
            btnConnect = (ShareConnectButton) view.findViewById(R.id.button_connect);
            imgIcon = (WPNetworkImageView) view.findViewById(R.id.image_icon);
        }
    }

    /*
     * AsyncTask to load services
     */
    private boolean mIsTaskRunning = false;
    private class LoadTagsTask extends AsyncTask<Void, Void, SharingServiceList> {
        @Override
        protected void onPreExecute() {
            mIsTaskRunning = true;
        }
        @Override
        protected void onCancelled() {
            mIsTaskRunning = false;
        }
        @Override
        protected SharingServiceList doInBackground(Void... params) {
            return SharingTable.getServiceList();
        }
        @Override
        protected void onPostExecute(SharingServiceList serviceList) {
            if (serviceList != null && !serviceList.isSameList(mServices)) {
                mServices.clear();
                mServices.addAll(serviceList);
                notifyDataSetChanged();
            }
            mIsTaskRunning = false;
        }
    }

}
