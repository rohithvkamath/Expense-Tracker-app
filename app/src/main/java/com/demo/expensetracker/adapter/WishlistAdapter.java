package com.demo.expensetracker.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.demo.expensetracker.R;
import com.demo.expensetracker.db.WishlistHelper;
import com.demo.expensetracker.entity.WishlistItem;

import java.util.ArrayList;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistItemViewholder>{
    private ArrayList<WishlistItem> items;
    private Activity activity;
    private Context context;
    private TextView tv_item, tv_price;
    private WishlistHelper wishlistHelper;
    private OnItemClickListener onItemClickListener;

    public WishlistAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        wishlistHelper = new WishlistHelper(context);
    }

    public ArrayList<WishlistItem> getWishlistItems() {
        return items;
    }

    public void setWishlistItems(ArrayList<WishlistItem> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, WishlistItem item);
    }

    @Override
    public WishlistItemViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wishlistitem, parent, false);
        return new WishlistItemViewholder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(WishlistItemViewholder holder, final int position) {

        if (getItemCount() == 0) {

        } else {
            wishlistHelper.open();
            holder.wishlistItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view, getItem(position));
                }
            });

            holder.tvItem.setText(getWishlistItems().get(position).getItem());

            // use integer value if price is integer
            Float price = getWishlistItems().get(position).getPrice();
            int integerPrice = price.intValue();
            String priceToShow = (integerPrice == price) ? String.valueOf(integerPrice) : String.valueOf(price);
            holder.tvPrice.setText("Rs." + priceToShow);

            if (getWishlistItems().get(position).isNeed()){
                holder.tvNeed.setVisibility(View.VISIBLE);
                holder.tvNeed.setText(R.string.tick_need);
            }
            if (getWishlistItems().get(position).isWant()) {
                holder.tvNeed.setVisibility(View.VISIBLE);
                holder.tvNeed.setText(R.string.tick_want);
            }
            if (getWishlistItems().get(position).isNeed() && getWishlistItems().get(position).isWant()) {
                holder.tvNeed.setVisibility(View.VISIBLE);
                holder.tvNeed.setText(R.string.tick_need_want);
            }
        }

    }

    @Override
    public int getItemCount() {
        return getWishlistItems().size();
    }

    public WishlistItem getItem(int position) {
        return getWishlistItems().get(position);
    }

    public void removeItem(int position) {
        getWishlistItems().remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(WishlistItem item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public class WishlistItemViewholder extends RecyclerView.ViewHolder{
        TextView tvItem, tvPrice, tvNeed;
        LinearLayout wishlistItemLayout;
        OnItemClickListener onItemClickListener;

        public WishlistItemViewholder(View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_item);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvNeed = itemView.findViewById(R.id.tv_need);
            wishlistItemLayout = itemView.findViewById(R.id.item_wishlist_linear_layout);
        }
    }
}