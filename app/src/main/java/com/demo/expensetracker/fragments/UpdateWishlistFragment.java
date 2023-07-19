package com.demo.expensetracker.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.demo.expensetracker.R;
import com.demo.expensetracker.db.WishlistHelper;
import com.demo.expensetracker.entity.WishlistItem;
import com.google.android.material.snackbar.Snackbar;

public class UpdateWishlistFragment extends Fragment {

    private Button btnSubmit;
    private EditText etItem, etPrice;
    private CheckBox cbNeed, cbWant, cbChecked;
    private WishlistHelper wishlistHelper;
    private boolean isEdit = false;
    private FrameLayout layout;
    private int id;
    private String item;
    private Float price;
    private boolean need, want, checked;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // get bundle from WishlistFragment for updating wishlist
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isEdit = true;
            id = bundle.getInt("id");
            item = bundle.getString("item");
            price = bundle.getFloat("price");
            need = bundle.getBoolean("need");
            want = bundle.getBoolean("want");
            checked = bundle.getBoolean("checked");
        }

        View root = inflater.inflate(R.layout.fragment_update_wishlistitem, container, false);

        layout = root.findViewById(R.id.wishlistitem_update_form);
        etItem = root.findViewById(R.id.et_item);
        etPrice = root.findViewById(R.id.et_price);
        cbNeed = root.findViewById(R.id.cb_need);
        cbWant = root.findViewById(R.id.cb_want);
        cbChecked = root.findViewById(R.id.cb_checked);

        // load from bundle
        if (isEdit) {
            etItem.setText(item);
            etPrice.setText(String.valueOf(price));
            cbNeed.setChecked(need);
            cbWant.setChecked(want);
            cbChecked.setChecked(checked);
        }

        wishlistHelper = new WishlistHelper(getActivity());
        wishlistHelper.open();

        btnSubmit = root.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = etItem.getText().toString().trim();
                String price = etPrice.getText().toString().trim();

                boolean isEmpty = false;

                if (TextUtils.isEmpty(item)){
                    isEmpty = true;
                    etItem.setError(getText(R.string.not_blank));
                }
                if (TextUtils.isEmpty(price)){
                    isEmpty = true;
                    etPrice.setError(getText(R.string.not_blank));
                }

                if (!isEmpty){
                    WishlistItem newWishlistItem = new WishlistItem();
                    newWishlistItem.setItem(item);
                    newWishlistItem.setPrice(Float.parseFloat(price));
                    newWishlistItem.setNeed(cbNeed.isChecked());
                    newWishlistItem.setWant(cbWant.isChecked());
                    newWishlistItem.setChecked(cbChecked.isChecked());

                    if (isEdit) {
                        newWishlistItem.setId(id);
                        wishlistHelper.update(newWishlistItem);
                        WishlistFragment fragment = new WishlistFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.wishlistitem_update_form, fragment).commit();
                    } else {
                        wishlistHelper.insert(newWishlistItem);
                        Snackbar.make(layout, getString(R.string.success_create, item), Snackbar.LENGTH_SHORT).show();
                        WishlistFragment fragment = new WishlistFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.wishlistitem_update_form, fragment).commit();
                    }
                }
            }});
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isEdit){
            inflater.inflate(R.menu.menu_form, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.action_delete:
                wishlistHelper.delete(id);
                showSnackbarMessage(getString(R.string.item_deleted, item));
                returnToWishlistFragment();
                break;
            case R.id.action_return:
                returnToWishlistFragment();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void returnToWishlistFragment() {
        WishlistFragment fragment = new WishlistFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.wishlistitem_update_form, fragment).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wishlistHelper != null){
            wishlistHelper.close();
        }
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }
}