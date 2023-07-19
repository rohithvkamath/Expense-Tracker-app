package com.demo.expensetracker.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.demo.expensetracker.R;
import com.demo.expensetracker.db.RecordHelper;
import com.demo.expensetracker.entity.Record;
import com.demo.expensetracker.utils.BalooTextView;
import com.demo.expensetracker.utils.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private RecordHelper recordHelper;
    private LinearLayout monthHistoryLinearLayout;
    private LinearLayout noMonthResultsLayout, noRepeatedItemsLayout;
    int monthHistoryCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        recordHelper = new RecordHelper(getActivity());
        recordHelper.open();

        noMonthResultsLayout = root.findViewById(R.id.no_month_results);
        noRepeatedItemsLayout = root.findViewById(R.id.no_repeated_results);

        // get month year of saved records
        ArrayList<String> monthYearStrings = recordHelper.getMonthYearWithRecords();

        monthHistoryLinearLayout = root.findViewById(R.id.month_history);

        if (monthYearStrings.size() > 0){
            // prepare bar chart
            AnyChartView anyChartView = root.findViewById(R.id.any_chart_view);
            anyChartView.setProgressBar(root.findViewById(R.id.progress_bar));
            Cartesian cartesian = AnyChart.column();
            List<DataEntry> dataForBarChart = new ArrayList<>();

            // add TextViews to month history linear layout
            for (String monthYear: monthYearStrings){
                String year = monthYear.substring(0,4);
                String month = monthYear.substring(5,7);

                ArrayList<Record> monthRecords = recordHelper.queryByMonthYear(month, year);

                ArrayList<String> descriptionStrings = new ArrayList<>();

                if (monthRecords.size() > 0){
                    // get month year repeated records
                    for (Record item : monthRecords){
                        descriptionStrings.add(item.getDescription());
                    }

                    // create RelativeLayout container for holding month TextView and repeated Items TextView
                    RelativeLayout relativeLayout = new RelativeLayout(getContext());
                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rlp.setMargins(0, 20, 0, 20);
                    relativeLayout.setLayoutParams(rlp);
                    relativeLayout.setPadding(100, 200, 100, 200);
                    relativeLayout.setBackgroundResource(R.drawable.bg_corners);

                    // month year heading TextView
                    TextView monthYearTextView = createTextView();
                    TextView monthYearAmountTextView = createTextView();

                    // show total amount spent in a month (eg Oct 2020  1000)
                    RelativeLayout.LayoutParams lp_right = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp_right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    RelativeLayout.LayoutParams lp_left = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp_left.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                    monthYearTextView.setLayoutParams(lp_left);
                    monthYearTextView.setText(DateUtils.DateFormat(monthYear));
                    monthYearTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
                    monthYearTextView.setId(monthHistoryCount + 1);

                    monthYearAmountTextView.setLayoutParams(lp_right);
                    monthYearAmountTextView.setText("Rs." + recordHelper.getTotalAmountSpent(monthRecords));
                    monthYearAmountTextView.setId(monthHistoryCount + 50);

                    relativeLayout.addView(monthYearTextView);
                    relativeLayout.addView(monthYearAmountTextView);

                    // prepare data for bar chart
                    dataForBarChart.add(new ValueDataEntry(
                            DateUtils.GetMonth(monthYear),
                            Integer.parseInt(recordHelper.getTotalAmountSpent(monthRecords))
                    ));

                    monthHistoryLinearLayout.addView(relativeLayout);

                    Map<String, Integer> repeatedDescriptions = countRepeated(descriptionStrings);

                    // if there are repeated items
                    if (Arrays.stream(repeatedDescriptions.values().stream().mapToInt(i -> i).toArray()).distinct().toArray().length != 1){
                        // create LinearLayout for holding repeated Items
                        LinearLayout repeatedItemsLinearLayout = new LinearLayout(getContext());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);
                        repeatedItemsLinearLayout.setOrientation(LinearLayout.VERTICAL);
                        repeatedItemsLinearLayout.setLayoutParams(lp);
                        repeatedItemsLinearLayout.setPadding(100, 20, 100, 20);

                        // get repeated occurrences for each month with records
                        for (Map.Entry<String, Integer> entry : repeatedDescriptions.entrySet()) {
                            if (entry.getValue() > 1){
                                // repeated item TextView
                                TextView repeatedItemTextView = createTextView();
                                repeatedItemTextView.setText(entry.getKey() + " x " + entry.getValue());
                                repeatedItemTextView.setId(monthHistoryCount + 10);
                                repeatedItemsLinearLayout.addView(repeatedItemTextView);
                            }
                        }
                        monthHistoryLinearLayout.addView(repeatedItemsLinearLayout);
                    } else {
                        noRepeatedItemsLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            // prepare bar chart
            Column column = cartesian.column(dataForBarChart);

            column.tooltip()
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(5d)
                    .format("Rs.{%Value}{groupsSeparator: }");

            cartesian.animation(true);

            cartesian.yScale().minimum(0d);

            cartesian.yAxis(0).labels().format("Rs.{%Value}{groupsSeparator: }");

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
            cartesian.interactivity().hoverMode(HoverMode.BY_X);

            anyChartView.setChart(cartesian);
        } else {
            noMonthResultsLayout.setVisibility(View.VISIBLE);
            noRepeatedItemsLayout.setVisibility(View.VISIBLE);
        }

        return root;
    }

    private TextView createTextView(){
        TextView textView = new BalooTextView(getContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(lp);
        return textView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Map<String, Integer> countRepeated(ArrayList<String> arrayInput) {
        String input = String.join(", ", arrayInput);
        input = input.replaceAll("[0-9]+", "");
        String[] splitedInput = input.split("\\s*,+\\s*");
        List<String> cleanedInput = cleanInputs(splitedInput);

        Map<String, Integer> repeated = new HashMap<>();
        for (String item : cleanedInput) {
            if (repeated.keySet().contains(item)) {
                repeated.put(item, repeated.get(item) + 1);
            } else {
                repeated.put(item, 1);
            }
        }

        return repeated;
    }

    private static List<String> cleanInputs(String[] inputArray) {
        List<String> result = new ArrayList<>(inputArray.length);
        for (String input : inputArray) {
            if (input != null) {
                String str = input.trim();
                if (!str.isEmpty()) {
                    result.add(str);
                }
            }
        }
        return result;
    }
}