package com.tan.mymemo;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.parceler.Parcels;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by beau- on 14/01/2017.
 */

public class MonitorMemoActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.view_color_line)
    View vColorLine;
    @BindView(R.id.edittext_title_memo)
    EditText etTitleMemo;
    @BindView(R.id.edittext_desc_memo)
    EditText etDescMemo;
    @BindView(R.id.textview_modified_date_memo)
    TextView tvModifiedDateMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_memo);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Memo memo = (Memo) Parcels.unwrap(getIntent().getParcelableExtra(getString(R.string.memo)));
        if (memo != null) {
            vColorLine.setBackgroundColor(memo.getColor());
            etTitleMemo.setText(memo.getTitle());
            etDescMemo.setText(memo.getDescription());
            tvModifiedDateMemo.setText(memo.getLastModified());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_memo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_save:
                break;
            case R.id.menu_delete:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
