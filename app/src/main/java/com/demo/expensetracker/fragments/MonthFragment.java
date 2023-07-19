package com.demo.expensetracker.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.expensetracker.R;
import com.demo.expensetracker.adapter.RecordAdapter;
import com.demo.expensetracker.db.RecordHelper;
import com.demo.expensetracker.entity.Record;
import com.demo.expensetracker.utils.DateUtils;
import com.demo.expensetracker.utils.SwipeToDeleteCallback;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthFragment extends Fragment {

    private ArrayList<Record> list;
    private RecordAdapter adapter;
    private RecordHelper recordHelper;
    private RelativeLayout containerLayout;
    private static LinearLayout noResults;
    private SearchView searchView;
    private LinearLayout monthRecordLayout, monthNavLayout;
    private RecyclerView rvRecords;

    Calendar now = Calendar.getInstance();
    private int month = now.get(Calendar.MONTH);
    private int year = now.get(Calendar.YEAR);

    private TextView tvMonth;
    private TextView tvTotalSum;
    private ImageView ivMonthNavDivider;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_month, container, false);
        setHasOptionsMenu(true);

        monthRecordLayout = root.findViewById(R.id.month_record_layout);
        setAnimation(monthRecordLayout);

        monthNavLayout = root.findViewById(R.id.nav_month);
//        ivMonthNavDivider = root.findViewById(R.id.month_nav_divider);
        noResults = root.findViewById(R.id.noResults);
        tvMonth = root.findViewById(R.id.tv_month);
        tvMonth.setText(DateUtils.getMonthYear(year + "-" + (month + 1)));
        tvTotalSum = root.findViewById(R.id.tv_item_total_sum);
        containerLayout = root.findViewById(R.id.fragment_month_container);
        rvRecords = root.findViewById(R.id.rv_records);
        rvRecords.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecords.setHasFixedSize(true);

        ImageView nextIcon = root.findViewById(R.id.ib_next_month);
        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextMonthYear();
                refresh(month, year);
            }
        });
        ImageView prevIcon = root.findViewById(R.id.ib_prev_month);
        prevIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPreviousMonthYear();
                refresh(month, year);
            }
        });

        recordHelper = new RecordHelper(getActivity());
        recordHelper.open();

        list = new ArrayList<>();
        adapter = new RecordAdapter(getContext(), getActivity());
        adapter.setRecords(list);
        rvRecords.setAdapter(adapter);

        new LoadRecordAsync().execute();
        enableSwipeToDeleteAndUndo();

        return root;
    }

    private int nextMonth(int month) {
        return (month + 1 == 12) ? 0 : (month + 1);
    }

    private int previousMonth(int month) {
        return (month - 1 == -1) ? 11 : (month - 1);
    }

    private void goToNextMonthYear() {
        int nextMonth = nextMonth(month);
        if (nextMonth < month) {
            year++;
        }
        month = nextMonth;
    }

    private void goToPreviousMonthYear() {
        int prevMonth = previousMonth(month);
        if (prevMonth > month) {
            year--;
        }
        month = prevMonth;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void refresh(int month, int year) {
        this.month = month;
        this.year = year;
        tvMonth.setText(DateUtils.getMonthYear(year + "-" + (month + 1)));

        list.clear();
        ArrayList<Record> monthRecords = recordHelper.queryByMonthYear(fixSingleDigit(month+1), String.valueOf(year));
        list.addAll(monthRecords);
        adapter.setRecords(list);
        adapter.notifyDataSetChanged();

        tvTotalSum.setText("Rs." + recordHelper.getTotalAmountSpent(list));

        if (monthRecords.size() == 0) {
            setResultsMessage(true);
        } else {
            setResultsMessage(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint(getText(R.string.search_records));
        searchMenuItem.getIcon().setVisible(false, false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onQueryTextChange(String query) {
                if (query.isEmpty()) {
                    refresh(month, year);
                    adapter.getFilter().filter("");
                    toggleMonthNavVisibility(true);
                }

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    list.clear();
                    ArrayList<Record> monthRecords = recordHelper.query();
                    list.addAll(monthRecords);
                    adapter.setRecords(list);
                    adapter.notifyDataSetChanged();
                    adapter.getFilter().filter(query.toLowerCase());
                    toggleMonthNavVisibility(false);
                }
                return false;
            }
        });
    }

    private void toggleMonthNavVisibility(boolean show){
        if (show){
            monthNavLayout.setVisibility(View.VISIBLE);
            tvTotalSum.setVisibility(View.VISIBLE);
//            ivMonthNavDivider.setVisibility(View.VISIBLE);
        } else {
            monthNavLayout.setVisibility(View.GONE);
            tvTotalSum.setVisibility(View.GONE);
//            ivMonthNavDivider.setVisibility(View.GONE);
        }
    }

    private String fixSingleDigit(int digit) {
        int single = Integer.toString(digit).trim().length();
        if (single == 1) {
            return "0" + digit;
        }
        return String.valueOf(digit);
    }

    private class LoadRecordAsync extends AsyncTask<Void, Void, ArrayList<Record>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* codes for generating demo data
            if (recordHelper.query().size() == 0){
                ArrayList<String> dates = new ArrayList<String>();
                final Calendar cal = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                ArrayList<String> expenses = new ArrayList<String>();
                if (Locale.getDefault().getLanguage() == "en"){
                    expenses = generateRandomExpensesEng();
                } else {
                    expenses = generateRandomExpenses();
                }
                for (int i = 1; i < 61; i++) {
                    cal.add(Calendar.DATE, -1);
                    dates.add(dateFormat.format(cal.getTime()));
                    recordHelper.insert(new Record(dates.get(i-1), generateRandomDescription(expenses)));
                }
            }
            */
        }

        @Override
        protected ArrayList<Record> doInBackground(Void... voids) { return recordHelper.queryByMonthYear(fixSingleDigit(month+1), String.valueOf(year)); }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(ArrayList<Record> monthRecords) {
            super.onPostExecute(monthRecords);

            list.addAll(monthRecords);
            adapter.setRecords(list);
            adapter.notifyDataSetChanged();

            if (list.size() > 0) {
                tvTotalSum.setText("Rs." + recordHelper.getTotalAmountSpent(monthRecords));
                setResultsMessage(false);
            } else {
                showSnackbarMessage(getString(R.string.no_items));
                setResultsMessage(true);
            }
        }
    }

    private void enableSwipeToDeleteAndUndo() {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final Record item = adapter.getItem(position);

                adapter.removeItem(position);
                recordHelper.delete(item.getId());
                tvTotalSum.setText("Rs." + recordHelper.getTotalAmountSpent(adapter.getRecords()));

                Snackbar snackbar = Snackbar.make(monthRecordLayout, getString(R.string.date_item_deleted, item.getDate()), Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.restoreItem(item, position);
                        rvRecords.scrollToPosition(position);
                        recordHelper.insert(item);
                        tvTotalSum.setText("Rs." + recordHelper.getTotalAmountSpent(adapter.getRecords()));
                    }
                });

                snackbar.setActionTextColor(Color.WHITE);
                snackbar.show();

                if (list.size() == 0) {
                    setResultsMessage(true);
                } else {
                    setResultsMessage(false);
                }
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rvRecords);
    }

    public static void setResultsMessage(Boolean result) {
        if (result) {
            noResults.setVisibility(View.VISIBLE);
        } else {
            noResults.setVisibility(View.GONE);
        }
    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recordHelper != null){
            recordHelper.close();
        }
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(containerLayout, message, Snackbar.LENGTH_SHORT).show();
    }}

