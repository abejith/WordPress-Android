package org.wordpress.android.ui.sharing;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.wordpress.android.R;
import org.wordpress.android.ui.reader.SharingEvents;
import org.wordpress.android.ui.sharing.adapters.SharingServiceAdapter;
import org.wordpress.android.ui.sharing.services.SharingUpdateService;

/**
 *
 */
public class SharingListActivity extends AppCompatActivity {

    private SharingServiceAdapter mAdapter;
    private RecyclerView mRecycler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharing_list_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new SharingServiceAdapter(this);
        mRecycler.setAdapter(mAdapter);
        mAdapter.refresh();

        if (savedInstanceState == null) {
            SharingUpdateService.startService(this);
        }
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
    public void onEventMainThread(SharingEvents.SharingServicesChanged event) {
        if (!isFinishing()) {
            mAdapter.refresh();
        }
    }
}
