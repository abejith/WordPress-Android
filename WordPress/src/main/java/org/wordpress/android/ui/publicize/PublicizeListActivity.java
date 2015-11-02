package org.wordpress.android.ui.publicize;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.wordpress.android.R;
import org.wordpress.android.ui.publicize.adapters.PublicizeServiceAdapter;
import org.wordpress.android.ui.publicize.services.PublicizeUpdateService;
import org.wordpress.android.ui.reader.PublicizeEvents;

/**
 *
 */
public class PublicizeListActivity extends AppCompatActivity {

    private PublicizeServiceAdapter mAdapter;
    private RecyclerView mRecycler;

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

        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new PublicizeServiceAdapter(this);
        mRecycler.setAdapter(mAdapter);
        mAdapter.refresh();

        if (savedInstanceState == null) {
            PublicizeUpdateService.updatePublicizeServices(this);
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
    public void onEventMainThread(PublicizeEvents.PublicizeServicesChanged event) {
        if (!isFinishing()) {
            mAdapter.refresh();
        }
    }
}
