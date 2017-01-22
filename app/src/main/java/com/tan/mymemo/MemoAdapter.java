package com.tan.mymemo;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by beau- on 14/01/2017.
 */

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    private Context     context;
    private List<Memo>  memos;

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cardview_memo)
        CardView cvMemo;
        @BindView(R.id.textview_title_memo)
        TextView tvTitleMemo;
        @BindView(R.id.textview_desc_memo)
        TextView tvDescMemo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Memo memo);
    }

    public MemoAdapter(Context context) {
        this.memos = new ArrayList<>();
        this.context = context;
    }

    public MemoAdapter(Context context, List<Memo> memos) {
        this.context = context;
        this.memos = memos;
    }

    public void setMemos(List<Memo> memos) {
        this.memos.clear();
        this.memos.addAll(memos);
        System.out.println(" memeo " + memos.size());
        this.notifyItemRangeChanged(0, memos.size());
        this.notifyDataSetChanged();
    }

    public void removeMemoByFileName(String fileName) {
        for (int i = 0; i < this.memos.size(); i++) {
            Memo memo = memos.get(i);
            if (memo.getFileName().equals(fileName)) {
                memos.remove(i);
            }
        }
        this.notifyItemRangeChanged(0, memos.size());
        this.notifyDataSetChanged();
    }

    public void updateMemo(Memo updatedMemo) {
        for (int i = 0; i < this.memos.size(); i++) {
            Memo memo = memos.get(i);
            if (memo.getFileName().equals(updatedMemo.getFileName())) {
                memo.setTitle(updatedMemo.getTitle());
                memo.setDescription(updatedMemo.getDescription());
                memo.setColor(updatedMemo.getColor());
                memo.setLastModified(updatedMemo.getLastModified());
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Memo memo = memos.get(position);

        holder.cvMemo.setBackgroundColor(memo.getColor());
        holder.tvTitleMemo.setText(memo.getTitle());
        holder.tvDescMemo.setText(memo.getDescription());
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    public void removeMemos() {
        memos.clear();
        notifyDataSetChanged();
    }
}
