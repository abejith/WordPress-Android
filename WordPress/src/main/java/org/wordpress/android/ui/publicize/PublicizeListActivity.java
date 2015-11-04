package org.wordpress.android.ui.publicize;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.wordpress.android.R;
import org.wordpress.android.ui.ActivityLauncher;
import org.wordpress.android.ui.publicize.adapters.PublicizeServiceAdapter;
import org.wordpress.android.ui.publicize.services.PublicizeUpdateService;
import org.wordpress.android.ui.reader.PublicizeEvents;
import org.wordpress.android.util.DisplayUtils;
import org.wordpress.android.util.NetworkUtils;
import org.wordpress.android.widgets.RecyclerItemDecoration;

import de.greenrobot.event.EventBus;

/**
 * Enables connecting/disconnecting a specific blog from various publicize services
 */
public class PublicizeListActivity extends AppCompatActivity {

    public static final String ARG_SITE_ID = "site_id";

    private PublicizeServiceAdapter mAdapter;
    private RecyclerView mRecycler;
    private int mSiteId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publicize_list_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mSiteId = getIntent().getIntExtra(ARG_SITE_ID, 0);
        } else {
            mSiteId = savedInstanceState.getInt(ARG_SITE_ID);
        }

        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        int spacingHorizontal = 0;
        int spacingVertical = DisplayUtils.dpToPx(this, 1);
        mRecycler.addItemDecoration(new RecyclerItemDecoration(spacingHorizontal, spacingVertical));

        mAdapter = new PublicizeServiceAdapter(this, mSiteId);
        mRecycler.setAdapter(mAdapter);
        mAdapter.refresh();

        if (savedInstanceState == null && NetworkUtils.isNetworkAvailable(this)) {
            PublicizeUpdateService.updateConnectionsForSite(this, mSiteId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        ActivityLauncher.slideOutToRight(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_SITE_ID, mSiteId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(PublicizeEvents.PublicizeServicesChanged event) {
        if (!isFinishing()) {
            mAdapter.refresh();
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(PublicizeEvents.PublicizeConnectionsChanged event) {
        if (!isFinishing()) {
            mAdapter.refresh();
        }
    }
}
