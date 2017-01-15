package com.tan.mymemo;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.ColorDialog;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, colorDialog.ColorSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler_view_memo)
    EasyRecyclerView rvMemo;

    @BindView(R.id.fab_add_new_memo)
    FloatingActionButton fabAddNewMemo;

    private int color;

    private boolean isTitleEmpty = true;
    private boolean isDescEmpty = true;

    private List<Memo> memos;
    private MemoAdapter adapter;

    private MaterialDialog materialDialog;

    private EditText etTitleMemo;
    private EditText etDescMemo;

    private MemoMonitor memoMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        memoMonitor = new MemoMonitor(this);

        initNewMemo();
        initMemoList();
    }

    public void initNewMemo() {

        materialDialog = new MaterialDialog.Builder(this).build();

        color = colorDialog.getPickerColor(MainActivity.this, 1);
        setViewColor(color);
    }

    public void initMemoList() {
        memos = memoMonitor.getMemos();
        adapter = new MemoAdapter(this, memos, new MemoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Memo memo) {
/*                Intent intent = new Intent(MainActivity.this, MonitorMemoActivity.class);
                intent.putExtra(getString(R.string.memo), Parcels.wrap(memo));
                startActivityForResult(intent, SAVE_MODIFIED_MEMO_REQUEST);*/
            }
        });
        rvMemo.setAdapter(adapter);
        rvMemo.setLayoutManager(new GridLayoutManager(this, 2));
        if (memos.isEmpty())
           rvMemo.showEmpty();
    }

    @Override
    public void onColorSelection(DialogFragment dialogFragment, @ColorInt int selectedColor) {

        int tag = Integer.valueOf(dialogFragment.getTag());

        switch (tag) {
            case 1:
                setViewColor(selectedColor);
                colorDialog.setPickerColor(MainActivity.this, 1, selectedColor);
                break;
        }
    }

    private void setViewColor(int color) {
        this.color = color;
        materialDialog.getView().setBackgroundColor(color);
    }

    @OnClick(R.id.fab_add_new_memo)
    public void addNewMemo() {
        materialDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_new_memo, true)
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        materialDialog.dismiss();
                    }
                })
                .positiveText(R.string.confirm)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (!isTitleEmpty || !isDescEmpty) {
                            MemoMonitor memoMonitor = new MemoMonitor(getApplicationContext());

                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                            String date = format.format(c.getTime());

                            Memo memo = new Memo(color,
                                    etTitleMemo.getText().toString(),
                                    etDescMemo.getText().toString(),
                                    date);

                            Gson gson = new GsonBuilder().create();
                            System.out.println("date " + gson.toJson(memo));
                            if (memoMonitor.writeToFile(MemoMonitor.MEMOS_FOLDER + date, gson.toJson(memo))) {
                                System.out.println("size  " + memos.size());
                                memos = memoMonitor.getMemos();
                                adapter.setMemos(memos);
                                Toast.makeText(getApplicationContext(), getString(R.string.memo_saved), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_put_title), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();

        ImageButton imgBtnColorPalette = (ImageButton) materialDialog.getCustomView().findViewById(R.id.image_button_color_palette);

        imgBtnColorPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("test");
                colorDialog.showColorPicker(MainActivity.this, 1);
            }
        });

        etTitleMemo = (EditText) materialDialog.getCustomView().findViewById(R.id.edittext_title_memo);
        etTitleMemo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    isTitleEmpty = false;
                else
                    isTitleEmpty = true;
            }
        });

        etDescMemo = (EditText) materialDialog.getCustomView().findViewById(R.id.edittext_desc_memo);
        etDescMemo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    isDescEmpty = false;
                else
                    isDescEmpty = true;
            }
        });


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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
