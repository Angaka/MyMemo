package com.tan.mymemo;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
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

import com.enrico.colorpicker.colorDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jude.easyrecyclerview.EasyRecyclerView;

import org.json.JSONObject;
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

    @BindView(R.id.cardview_memo)
    CardView cvMemo;
    @BindView(R.id.edittext_title_memo)
    EditText etTitleMemo;
    @BindView(R.id.edittext_desc_memo)
    EditText etDescMemo;
    @BindView(R.id.image_button_color_palette)
    ImageButton ibColorPalette;
    @BindView(R.id.image_button_save)
    ImageButton ibSave;

    @BindView(R.id.recycler_view_memo)
    EasyRecyclerView rvMemo;

    static final int SAVE_MODIFIED_MEMO_REQUEST = 0;
    static final int SAVE_NEW_MEMO_REQUEST = 1;

    private int color;

    private boolean isTitleEmpty = true;
    private boolean isDescEmpty = true;

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

    @OnClick(R.id.image_button_color_palette)
    public void buttonColorPalette() {
        colorDialog.showColorPicker(MainActivity.this, 1);
    }

    @OnClick(R.id.image_button_save)
    public void buttonSaveMemo() {
        MemoMonitor memoMonitor = new MemoMonitor(this);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String date = format.format(c.getTime());

        Memo memo = new Memo(color,
                etTitleMemo.getText().toString(),
                etDescMemo.getText().toString(),
                date);

        Gson gson = new GsonBuilder().create();
        System.out.println("date " + gson.toJson(memo));
        if (memoMonitor.writeToFile(MemoMonitor.MEMOS_FOLDER + date, gson.toJson(memo)))
            Toast.makeText(this, getString(R.string.memo_saved), Toast.LENGTH_LONG).show();
    }

    public void initNewMemo() {

        color = colorDialog.getPickerColor(MainActivity.this, 1);
        setViewColor(color);

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

                if (!isTitleEmpty && !isDescEmpty)
                    ibSave.setVisibility(View.VISIBLE);
                else
                    ibSave.setVisibility(View.GONE);
            }
        });

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

                if (!isTitleEmpty && !isDescEmpty)
                    ibSave.setVisibility(View.VISIBLE);
                else
                    ibSave.setVisibility(View.GONE);
            }
        });
    }

    public void initMemoList() {
        List<Memo> memos = memoMonitor.getMemos();
        if (memos.isEmpty())
            rvMemo.showEmpty();
        else {
            MemoAdapter adapter = new MemoAdapter(this, memos, new MemoAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Memo memo) {
                    Intent intent = new Intent(MainActivity.this, MonitorMemoActivity.class);
                    intent.putExtra(getString(R.string.memo), Parcels.wrap(memo));
                    startActivityForResult(intent, SAVE_MODIFIED_MEMO_REQUEST);
                }
            });
            rvMemo.setAdapter(adapter);
            rvMemo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
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
        cvMemo.setCardBackgroundColor(color);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SAVE_NEW_MEMO_REQUEST:
                if (resultCode == Activity.RESULT_OK) {

                }
                break;
            case SAVE_MODIFIED_MEMO_REQUEST:
                if (resultCode == Activity.RESULT_OK) {

                } else {

                }
                break;
            default:
                break;
        }
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
                        dialog.dismiss();
                    }
                })
                .setNegativeListener(getString(R.string.cancel), new ColorDialog.OnNegativeListener() {
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
