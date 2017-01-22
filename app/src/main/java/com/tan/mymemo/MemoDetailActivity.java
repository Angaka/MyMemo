package com.tan.mymemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enrico.colorpicker.colorDialog;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.ColorDialog;

import static com.tan.mymemo.MemoMonitor.MEMO;
import static com.tan.mymemo.MemoMonitor.MEMO_CANCELED;
import static com.tan.mymemo.MemoMonitor.MEMO_DELETED;
import static com.tan.mymemo.MemoMonitor.MEMO_SAVED;

/**
 * Created by oudong on 20/01/2017.
 */

public class MemoDetailActivity extends AppCompatActivity implements colorDialog.ColorSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_memo)
    RelativeLayout layoutMemo;
    @BindView(R.id.edittext_title_memo)
    EditText etTitleMemo;
    @BindView(R.id.edittext_desc_memo)
    EditText etDescMemo;
    @BindView(R.id.textview_modified_date_memo)
    TextView tvModifiedDateMemo;

    private int color;
    private MemoMonitor memoMonitor;
    private Memo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        color = colorDialog.getPickerColor(MemoDetailActivity.this, 1);
        setViewColor(color, 1);

        memoMonitor = new MemoMonitor(this);
        memo = Parcels.unwrap(getIntent().getParcelableExtra(MemoMonitor.MEMO));
        if (memo != null) {
            color = memo.getColor();
            layoutMemo.setBackgroundColor(color);
            etTitleMemo.setText(memo.getTitle());
            etDescMemo.setText(memo.getDescription());
            tvModifiedDateMemo.setText("Modified date : " + memo.getLastModified());
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
                Intent returnIntent = new Intent();

                Calendar c = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = format.format(c.getTime());

                if (memo == null) {
                    memo = new Memo(
                            date,
                            color,
                            etTitleMemo.getText().toString(),
                            etDescMemo.getText().toString(),
                            date);
                } else {
                    memo.setColor(color);
                    memo.setTitle(etTitleMemo.getText().toString());
                    memo.setDescription(etDescMemo.getText().toString());
                    memo.setLastModified(date);
                }
                returnIntent.putExtra(MemoMonitor.MEMO, Parcels.wrap(memo));
                setResult(MEMO_SAVED, returnIntent);
                finish();
                break;
            case R.id.menu_delete:
                final ColorDialog dialog = new ColorDialog(this);
                dialog.setTitle(getString(R.string.delete));
                dialog.setContentText(getString(R.string.delete_this_memo));
                dialog.setPositiveListener(getString(R.string.delete), new ColorDialog.OnPositiveListener() {
                    @Override
                    public void onClick(ColorDialog colorDialog) {
                        if (memo != null) {
                            Intent returnIntent = new Intent();

                            returnIntent.putExtra(MEMO, Parcels.wrap(memo));
                            setResult(MEMO_DELETED, returnIntent);
                        }
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.setNegativeListener(getString(R.string.cancel), new ColorDialog.OnNegativeListener() {
                    @Override
                    public void onClick(ColorDialog colorDialog) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.image_button_color_palette)
    public void selectColor() {
        colorDialog.showColorPicker(this, 1);
    }

    @Override
    public void onBackPressed() {
        if (memo != null) {
            final ColorDialog dialog = new ColorDialog(this);
            dialog.setTitle(getString(R.string.save_memo));
            dialog.setContentText(getString(R.string.save_this_memo_and_quit));
            dialog.setPositiveListener(getString(R.string.save_and_quit), new ColorDialog.OnPositiveListener() {
                @Override
                public void onClick(ColorDialog colorDialog) {
                    Intent returnIntent = new Intent();

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = format.format(c.getTime());

                    memo.setColor(color);
                    memo.setTitle(etTitleMemo.getText().toString());
                    memo.setDescription(etDescMemo.getText().toString());
                    memo.setLastModified(date);
                    returnIntent.putExtra(MemoMonitor.MEMO, Parcels.wrap(memo));
                    setResult(MEMO_SAVED, returnIntent);

                    dialog.dismiss();
                    MemoDetailActivity.super.onBackPressed();
                }
            });
            dialog.setNegativeListener(getString(R.string.just_quit), new ColorDialog.OnNegativeListener() {
                @Override
                public void onClick(ColorDialog colorDialog) {
                    dialog.dismiss();
                    setResult(MEMO_CANCELED);
                    MemoDetailActivity.super.onBackPressed();
                }
            }).show();
        } else {
            final ColorDialog dialog = new ColorDialog(this);
            dialog.setTitle(getString(R.string.discard_memo));
            dialog.setContentText(getString(R.string.discard_this_memo));
            dialog.setPositiveListener(getString(R.string.confirm), new ColorDialog.OnPositiveListener() {
                @Override
                public void onClick(ColorDialog colorDialog) {
                    dialog.dismiss();
                    setResult(MEMO_CANCELED);
                    MemoDetailActivity.super.onBackPressed();
                }
            });
            dialog.setNegativeListener(getString(R.string.cancel), new ColorDialog.OnNegativeListener() {
                @Override
                public void onClick(ColorDialog colorDialog) {
                    dialog.dismiss();
                }
            }).show();
        }
    }


    @Override
    public void onColorSelection(DialogFragment dialogFragment, @ColorInt int selectedColor) {
        int tag = Integer.valueOf(dialogFragment.getTag());

        switch (tag) {
            case 1:
                setViewColor(selectedColor, 1);
                colorDialog.setPickerColor(MemoDetailActivity.this, 1, selectedColor);
                break;

            default:
                break;
        }
    }

    private void setViewColor(int color, int tag) {
        this.color = color;
        layoutMemo.setBackgroundColor(color);
    }
}
