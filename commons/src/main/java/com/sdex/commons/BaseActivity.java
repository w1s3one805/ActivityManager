package com.sdex.commons;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.sdex.commons.apps.AppsListActivity;
import com.sdex.commons.util.AppUtils;

public class BaseActivity extends AppCompatActivity {

  private static final String TAG = "BaseActivity";

  protected void enableBackButton() {
    ActionBar supportActionBar = getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.sdex_commons_base_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      onBackPressed();
    } else if (itemId == R.id.rate) {
      AppUtils.openPlayStore(this);
      return true;
    } else if (itemId == R.id.more_apps) {
      startActivity(new Intent(this, AppsListActivity.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
