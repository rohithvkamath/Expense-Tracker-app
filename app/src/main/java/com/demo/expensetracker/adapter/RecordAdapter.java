package com.demo.expensetracker.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.demo.expensetracker.R;
import com.demo.expensetracker.db.RecordHelper;
import com.demo.expensetracker.entity.Record;
import com.demo.expensetracker.fragments.MonthFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewholder>{
    private OnItemClickListener onItemClickListener;
    private ArrayList<Record> records;
    private Activity activity;
    private Context context;
    private ArrayList<Record> recordsSearch;
    private RecordHelper recordHelper;

    public RecordAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        recordHelper = new RecordHelper(context);
    }

    public ArrayList<Record> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }

    @Override
    public RecordViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_record, parent, false);
        return new RecordViewholder(view, onItemClickListener);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Record item);
    }

    private String simplifyDate(String dateSaved){
        String dateToShow = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = inputFormat.parse(dateSaved);
            DateFormat outputFormat = new SimpleDateFormat("dd.MM");
            dateToShow = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateToShow;
    }

    @Override
    public void onBindViewHolder(RecordViewholder holder, final int position) {

        if (getItemCount() == 0) {

        } else {
            recordHelper.open();

            holder.tvDate.setText(simplifyDate(getRecords().get(position).getDate()));
            holder.tvDescription.setText(getRecords().get(position).getDescription());
            holder.tvDate.setText(simplifyDate(getRecords().get(position).getDate()));

            boolean isNumber = Pattern.matches(".*[0-9]+.*", String.valueOf(getRecords().get(position).getDescription()));

            if(isNumber) {
                holder.tvSum.setText("Rs." + countDayExpense(getRecords().get(position).getDescription()));
            }
        }

    }

    private String countDayExpense(String str){
        float sum = 0;
        String newString = str.replaceAll("[^0-9.]+", " ");
        newString = newString.trim();
        newString = newString.replaceAll(" +", ",");
        String[] numbers = newString.split(",");

        for (int i = 0; i < numbers.length; i++) {
            sum += Float.parseFloat(numbers[i]);
        }
        if(sum == (long) sum)
            return String.format("%d",(long)sum);
        else
            return String.format("%.1f", sum);
    }

    @Override
    public int getItemCount() {
        return getRecords().size();
    }

    public Record getItem(int position) {
        return getRecords().get(position);
    }

    public void removeItem(int position) {
        getRecords().remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Record item, int position) {
        records.add(position, item);
        notifyItemInserted(position);
    }

    public class RecordViewholder extends RecyclerView.ViewHolder{
        TextView tvDescription, tvDate, tvSum, tvTotalSum;
        LinearLayout recordLayout;

        public RecordViewholder(View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            tvSum = itemView.findViewById(R.id.tv_item_sum);
            tvTotalSum = itemView.findViewById(R.id.tv_item_total_sum);
            recordLayout = itemView.findViewById(R.id.row_day_record);
        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();

                if (charSequence != null) {
                    String whereCondition = " WHERE description LIKE '%"+ charSequence.toString() + "%'  --case-insensitive";
                    recordsSearch = recordHelper.getRecords(whereCondition);

                    oReturn.values = recordsSearch;
                    oReturn.count = recordsSearch.size();
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.count > 0) {
                    MonthFragment.setResultsMessage(false);
                } else {
                    MonthFragment.setResultsMessage(true);
                }
                records = (ArrayList<Record>) filterResults.values;

                notifyDataSetChanged();
            }
        };
    }
}