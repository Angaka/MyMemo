package com.tan.mymemo;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.enrico.colorpicker.colorDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;
import com.tan.mymemo.utils.ItemClickSupport;

import org.parceler.Parcels;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.ColorDialog;

import static com.tan.mymemo.MemoMonitor.MEMO;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler_view_memo)
    RecyclerView rvMemo;

    @BindView(R.id.fab_add_new_memo)
    FloatingActionButton fabAddNewMemo;

    static final int MEMO_DETAIL = 0;
    static final int ADDED_MEMO = 0;
    static final int MODIFIED_MEMO = 1;

    private List<Memo> memos;
    private MemoAdapter adapter;

    private MemoMonitor memoMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        memoMonitor = new MemoMonitor(this);

        initMemoList();
    }

    public void initMemoList() {
        memos = memoMonitor.getMemos();
        RecyclerItemClickSupport.addTo(rvMemo).setOnItemClickListener(new RecyclerItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                final Memo memo = memos.get(position);
                Intent intent = new Intent(MainActivity.this, MemoDetailActivity.class);

                intent.putExtra(MEMO, Parcels.wrap(memo));
                startActivityForResult(intent, MEMO_DETAIL);
            }
        });

        adapter = new MemoAdapter(this, memos);
        rvMemo.setAdapter(adapter);
        rvMemo.setLayoutManager(new GridLayoutManager(this, 2));
/*        if (memos.isEmpty())
            rvMemo.showEmpty();*/
    }

    @OnClick(R.id.fab_add_new_memo)
    public void addNewMemo() {
        Intent intent = new Intent(MainActivity.this, MemoDetailActivity.class);
        startActivityForResult(intent, MEMO_DETAIL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_delete_all:
                final ColorDialog dialog = new ColorDialog(this);
                dialog.setTitle(getString(R.string.delete_all));
                dialog.setContentText(getString(R.string.confirm_delete_all));
                dialog.setPositiveListener(getString(R.string.delete), new ColorDialog.OnPositiveListener() {
                    @Override
                    public void onClick(ColorDialog colorDialog) {
                        memoMonitor.deleteAllMemos();
                        Toast.makeText(getApplicationContext(), getString(R.string.all_deleted), Toast.LENGTH_SHORT).show();
                        adapter.removeMemos();
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeListener(getString(R.string.cancel), new ColorDialog.OnNegativeListener() {
                    @Override
                    public void onClick(ColorDialog colorDialog) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.menu_search:
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Gson gson = new GsonBuilder().create();
        if (requestCode == MEMO_DETAIL) {

            Memo memo = null;
            switch (resultCode) {
                case RESULT_OK:
                    memo = Parcels.unwrap(data.getParcelableExtra(MemoMonitor.MEMO));
                    if (memoMonitor.writeToFile(memo.getFileName(), gson.toJson(memo))) {
                        memos = memoMonitor.getMemos();
                        adapter.setMemos(memos);
                        Toast.makeText(getApplicationContext(), getString(R.string.memo_saved), Toast.LENGTH_LONG).show();
                    }
                    break;
                case RESULT_CANCELED:
                    memos = memoMonitor.getMemos();
                    adapter.setMemos(memos);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
