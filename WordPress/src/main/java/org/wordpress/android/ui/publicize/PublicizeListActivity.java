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

import de.greenrobot.event.EventBus;

/**
 *
 */
public class PublicizeListActivity extends AppCompatActivity {

    public static final String ARG_REMOTE_BLOG_ID = "blog_id";

    private PublicizeServiceAdapter mAdapter;
    private RecyclerView mRecycler;
    private int mRemoteBlogId;

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
            mRemoteBlogId = getIntent().getIntExtra(ARG_REMOTE_BLOG_ID, 0);
        } else {
            mRemoteBlogId = savedInstanceState.getInt(ARG_REMOTE_BLOG_ID);
        }

        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new PublicizeServiceAdapter(this, mRemoteBlogId);
        mRecycler.setAdapter(mAdapter);
        mAdapter.refresh();

        if (savedInstanceState == null) {
            PublicizeUpdateService.updatePublicizeServices(this);
            PublicizeUpdateService.updateConnectionsForBlog(this, mRemoteBlogId);
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
        outState.putInt(ARG_REMOTE_BLOG_ID, mRemoteBlogId);
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
