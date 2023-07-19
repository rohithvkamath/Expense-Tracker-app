package com.demo.expensetracker.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.expensetracker.R;
import com.demo.expensetracker.adapter.WishlistAdapter;
import com.demo.expensetracker.db.WishlistHelper;
import com.demo.expensetracker.entity.WishlistItem;
import com.demo.expensetracker.utils.SwipeToDeleteCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class WishlistFragment extends Fragment {

    private WishlistHelper wishlistHelper;
    private ArrayList<WishlistItem> list;
    private WishlistAdapter adapter;
    RecyclerView rvWishlistItems;
    private FloatingActionButton fabAddWishlist;
    private LinearLayout wishlistLinearlayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wishlist, container, false);
        setHasOptionsMenu(true);

        wishlistHelper = new WishlistHelper(getActivity());
        wishlistHelper.open();

        wishlistLinearlayout = root.findViewById(R.id.wishlist_linear_layout);
        setAnimation(wishlistLinearlayout);

        rvWishlistItems = root.findViewById(R.id.rv_wishlistItems);
        rvWishlistItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWishlistItems.setHasFixedSize(true);

        fabAddWishlist = root.findViewById(R.id.fab_add_wishlist);
        fabAddWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateWishlistFragment fragment = new UpdateWishlistFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, fragment);
                ft.commit();
                fabAddWishlist.setVisibility(View.GONE);
                wishlistLinearlayout.setVisibility(View.GONE);
            }
        });

        list = new ArrayList<>();
        adapter = new WishlistAdapter(getContext(), getActivity());
        adapter.setWishlistItems(list);
        rvWishlistItems.setAdapter(adapter);

        new LoadWishlistAsync().execute();
        enableSwipeToDeleteAndUndo();
        adapter.setOnItemClickListener(new WishlistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, WishlistItem item) {
                UpdateWishlistFragment fragment = new UpdateWishlistFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id", item.getId());
                bundle.putString("item", item.getItem());
                bundle.putFloat("price", item.getPrice());
                bundle.putBoolean("need", item.isNeed());
                bundle.putBoolean("want", item.isWant());
                bundle.putBoolean("checked", item.isChecked());
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
                fabAddWishlist.setVisibility(View.GONE);
                wishlistLinearlayout.setVisibility(View.GONE);
            }
        });
        return root;
    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
    }

    private void enableSwipeToDeleteAndUndo() {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final WishlistItem item = adapter.getItem(position);

                if (wishlistHelper == null) {
                    wishlistHelper.open();
                }
                adapter.removeItem(position);
                wishlistHelper.delete(item.getId());
                Snackbar snackbar = Snackbar
                        .make(wishlistLinearlayout, getString(R.string.item_deleted, item.getItem()), Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.restoreItem(item, position);
                        rvWishlistItems.scrollToPosition(position);
                        wishlistHelper.insert(item);
                    }
                });

                snackbar.setActionTextColor(Color.WHITE);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rvWishlistItems);
    }

    private class LoadWishlistAsync extends AsyncTask<Void, Void, ArrayList<WishlistItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (list.size() > 0){
                list.clear();
            }
        }

        @Override
        protected ArrayList<WishlistItem> doInBackground(Void... voids) {
            return wishlistHelper.query();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(ArrayList<WishlistItem> wishlistItems) {
            super.onPostExecute(wishlistItems);

            list.addAll(wishlistItems);
            adapter.setWishlistItems(list);
            adapter.notifyDataSetChanged();

            adapter.setWishlistItems(list);
            adapter.notifyDataSetChanged();

            if (list.size() == 0){
                showSnackbarMessage(getString(R.string.no_items));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(rvWishlistItems, message, Snackbar.LENGTH_SHORT).show();
    }
}