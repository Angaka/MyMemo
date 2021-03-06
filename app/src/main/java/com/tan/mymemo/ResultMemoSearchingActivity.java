package com.tan.mymemo;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.refactor.lib.colordialog.ColorDialog;

import static com.tan.mymemo.MemoMonitor.MEMO;
import static com.tan.mymemo.MemoMonitor.MEMO_CANCELED;
import static com.tan.mymemo.MemoMonitor.MEMO_DELETED;
import static com.tan.mymemo.MemoMonitor.MEMO_SAVED;

/**
 * Created by oudong on 22/01/2017.
 */

public class ResultMemoSearchingActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view_memo)
    RecyclerView rvMemo;

    static final String QUERY = "query";
    static final int MEMO_DETAIL = 0;

    private List<Memo> memos;
    private MemoAdapter adapter;

    private MemoMonitor memoMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_memo_searching);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        memoMonitor = new MemoMonitor(this);

        getSupportActionBar().setTitle("Results for : " + getIntent().getStringExtra(QUERY));
        initMemoList();
    }

    public void initMemoList() {
        memos = Parcels.unwrap(getIntent().getParcelableExtra(MemoMonitor.MEMO));
        RecyclerItemClickSupport.addTo(rvMemo).setOnItemClickListener(new RecyclerItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                final Memo memo = memos.get(position);
                Intent intent = new Intent(ResultMemoSearchingActivity.this, MemoDetailActivity.class);

                intent.putExtra(MEMO, Parcels.wrap(memo));
                startActivityForResult(intent, MEMO_DETAIL);
            }
        });

        adapter = new MemoAdapter(this, memos);
        rvMemo.setAdapter(adapter);
        rvMemo.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.home:
                onBackPressed();
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
                case MEMO_SAVED:
                    memo = Parcels.unwrap(data.getParcelableExtra(MemoMonitor.MEMO));
                    if (memoMonitor.writeToFile(memo.getFileName(), gson.toJson(memo))) {
                        adapter.updateMemo(memo);
                        Toast.makeText(getApplicationContext(), getString(R.string.memo_saved), Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getApplicationContext(), getString(R.string.memo_failed), Toast.LENGTH_LONG).show();
                    break;
                case MEMO_CANCELED:
                    break;
                case MEMO_DELETED:
                    Memo deletedMemo = Parcels.unwrap(data.getParcelableExtra(MEMO));
                    adapter.removeMemoByFileName(deletedMemo.getFileName());
                    memoMonitor.deleteMemoByFilename(deletedMemo.getFileName());
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(MEMO_CANCELED);
        super.onBackPressed();
    }
}
