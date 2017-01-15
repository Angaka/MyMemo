package com.tan.mymemo;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.enrico.colorpicker.colorDialog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by beau- on 14/01/2017.
 */

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    private OnItemClickListener listener;

    private Context     context;
    private List<Memo>  memos;

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cardview_memo)
        CardView cvMemo;
        @BindView(R.id.textview_title_memo)
        TextView tvTitleMemo;
        @BindView(R.id.textview_desc_memo)
        TextView tvDescMemo;
        @BindView(R.id.textview_modified_date_memo)
        TextView tvModifiedDateMemo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Context context, final Memo memo, final OnItemClickListener listener) {
            cvMemo.setBackgroundColor(memo.getColor());

            tvTitleMemo.setText(memo.getTitle());
            tvDescMemo.setText(memo.getDescription());

            String lastModified = memo.getLastModified();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            try {
                Date date = dateFormat.parse(lastModified);
                tvModifiedDateMemo.setText("Last modified " + date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(memo);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Memo memo);
    }

    public MemoAdapter(Context context) {
        this.memos = new ArrayList<>();
        this.context = context;
    }

    public MemoAdapter(Context context, List<Memo> memos, OnItemClickListener listener) {
        this.context = context;
        this.memos = memos;
        this.listener = listener;
    }

    public void setMemos(List<Memo> memos) {
        this.memos.clear();
        this.memos.addAll(memos);
        System.out.println(" memeo " + memos.size());
        this.notifyItemRangeChanged(0, memos.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(context, memos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    public void addMemo(int position, Memo memo) {
        memos.add(position, memo);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, memos.size());
    }

    public void removeMemos() {
        memos.clear();
        notifyDataSetChanged();
    }
}
