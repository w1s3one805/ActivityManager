package com.sdex.activityrunner.intent.history;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.intent.LaunchParams;
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AdsHandler;

public class HistoryActivity extends BaseActivity {

  public static final String RESULT = "result";

  public static final int REQUEST_CODE = 111;

  @BindView(R.id.list)
  RecyclerView recyclerView;

  private HistoryListAdapter adapter;
  private HistoryViewModel viewModel;

  public static Intent getLaunchIntent(Context context) {
    return new Intent(context, HistoryActivity.class);
  }

  @Override
  protected int getLayout() {
    return R.layout.activity_history;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    viewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);

    FrameLayout adsContainer = findViewById(R.id.ads_container);
    AdsHandler adsHandler = new AdsHandler(this, adsContainer);
    adsHandler.init(this, R.string.ad_banner_unit_id);

    enableBackButton();

    adapter = new HistoryListAdapter((item, position) -> {
      HistoryToLaunchParamsConverter historyToLaunchParamsConverter =
        new HistoryToLaunchParamsConverter(item);
      LaunchParams launchParams = historyToLaunchParamsConverter.convert();
      Intent data = new Intent();
      data.putExtra(RESULT, launchParams);
      setResult(RESULT_OK, data);
      finish();
    });
    adapter.setHasStableIds(true);
    final Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.list_divider);
    if (dividerDrawable != null) {
      DividerItemDecoration dividerItemDecoration =
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
      dividerItemDecoration.setDrawable(dividerDrawable);
      recyclerView.addItemDecoration(dividerItemDecoration);
    }
    recyclerView.setAdapter(adapter);
    registerForContextMenu(recyclerView);

    viewModel.getHistory().observe(this,
      historyModels -> adapter.setItems(historyModels));
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    if (item.getItemId() == HistoryListAdapter.MENU_ITEM_REMOVE) {
      final int position = adapter.getContextMenuItemPosition();
      final HistoryModel historyModel = adapter.getItem(position);
      viewModel.deleteItem(historyModel);
    }
    return super.onContextItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.history, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_clear_history: {
        new AlertDialog.Builder(this)
          .setTitle("Clear history")
          .setMessage("Are you sure?")
          .setPositiveButton(android.R.string.yes, (dialog, which) -> {
            viewModel.clear();
            finish();
          })
          .setNegativeButton(android.R.string.cancel, null)
          .show();
        return true;
      }
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
