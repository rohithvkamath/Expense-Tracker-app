package com.demo.expensetracker.fragments;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.demo.expensetracker.R;
import com.demo.expensetracker.db.RecordHelper;
import com.demo.expensetracker.entity.Record;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class RecordFragment extends Fragment{

    private RecordHelper recordHelper;
    private EditText etItem1, etItem2, etItem3;
    private EditText etItem1Price, etItem2Price, etItem3Price;
    private Button btnSubmit, btnAdd, btnCalendar, btnSubmitDate;
    private FloatingActionButton fabSubmit;
    private LinearLayout updateDateForm, btnContainer;
    private ScrollView addRecordForm;
    private DatePicker datePicker;
    private TextView tvTitle;
    private int addCount = 0;
    boolean isKeyboardShowing = false;
    Map<Integer, EditText> idsEditTextMap = new HashMap<Integer, EditText>();
    Map<Integer, EditText> idsEditTextPriceMap = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View root = inflater.inflate(R.layout.fragment_record, container, false);
        recordHelper = new RecordHelper(getActivity());
        recordHelper.open();

        etItem1Price = root.findViewById(R.id.et_item1_price);
        etItem2Price = root.findViewById(R.id.et_item2_price);
        etItem3Price = root.findViewById(R.id.et_item3_price);
        etItem1 = root.findViewById(R.id.et_item1);
        etItem2 = root.findViewById(R.id.et_item2);
        etItem3 = root.findViewById(R.id.et_item3);

        btnSubmit = root.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                recordTodaysExpense();
            }
        });

        addRecordForm = root.findViewById(R.id.add_record_scrollview);
        updateDateForm = root.findViewById(R.id.update_date_form);
        datePicker = root.findViewById(R.id.date_picker);
        btnContainer = root.findViewById(R.id.button_container);
        setAnimation(btnContainer, false);

        btnCalendar = root.findViewById(R.id.btn_calendar);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDateForm.setVisibility(View.VISIBLE);
                addRecordForm.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
            }
        });

        btnSubmitDate = root.findViewById(R.id.btn_ok_date);
        btnSubmitDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                updateDateForm.setVisibility(View.GONE);
                addRecordForm.setVisibility(View.VISIBLE);
                btnContainer.setVisibility(View.VISIBLE);

                String recordDate = fixDateFromPicker(datePicker);
                tvTitle.setText(getString(R.string.date_spending,recordDate));
            }
        });

        datePicker = root.findViewById(R.id.date_picker);
        tvTitle = root.findViewById(R.id.title_for_form);
        setAnimation(tvTitle, true);

        btnAdd = root.findViewById(R.id.btn_add);
        // add a row of edit text and number picker on btnAdd click
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                LinearLayout parent = root.findViewById(R.id.text_inputs_container);

                RelativeLayout relativeLayout = new RelativeLayout(getContext());

                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                relativeLayout.setLayoutParams(rlp);
                relativeLayout.setGravity(Gravity.CENTER_HORIZONTAL);

                // TextView
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Baloo-Regular.ttf");
                EditText etItem = new EditText(getContext());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        (int) (150 * Resources.getSystem().getDisplayMetrics().density),
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                etItem.setLayoutParams(lp);
                etItem.setId(addCount + 1);
                etItem.setHint(R.string.item);
                etItem.setGravity(Gravity.CENTER_HORIZONTAL);
                etItem.setTypeface(tf);
                idsEditTextMap.put(addCount + 1, etItem);

                // TextView Price
                EditText etItemPrice = new EditText(getContext());
                RelativeLayout.LayoutParams lpPrice = new RelativeLayout.LayoutParams(
                        (int) (60 * Resources.getSystem().getDisplayMetrics().density),
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lpPrice.addRule(RelativeLayout.RIGHT_OF, addCount + 1);

                etItemPrice.setLayoutParams(lpPrice);
                etItemPrice.setId(addCount + 100);
                etItemPrice.setHint("Rs.");
                etItemPrice.setGravity(Gravity.CENTER_HORIZONTAL);
                etItemPrice.setTypeface(tf);
                idsEditTextPriceMap.put(addCount + 100, etItemPrice);

                relativeLayout.addView(etItem);
                relativeLayout.addView(etItemPrice);
                parent.addView(relativeLayout);
                addCount += 1;
            }
        });

        root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        root.getWindowVisibleDisplayFrame(r);
                        int screenHeight = root.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!isKeyboardShowing) {
                                isKeyboardShowing = true;
                                onKeyboardVisibilityChanged(true);
                            }
                        }
                        else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                onKeyboardVisibilityChanged(false);
                            }
                        }
                    }
                });

        fabSubmit = root.findViewById(R.id.fab_submit_record);

        fabSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                recordTodaysExpense();
            }
        });
        return root;
    }

    private void onKeyboardVisibilityChanged(boolean opened) {
        if (opened){
            fabSubmit.setVisibility(View.VISIBLE);
            setAnimation(fabSubmit, true);
        } else {
            fabSubmit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String fixDateFromPicker(DatePicker datePicker) {
        LocalDate ld = LocalDate.of(datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth());
        Date date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void recordTodaysExpense(){
        String item1 = etItem1.getText().toString().trim();
        String item2 = etItem2.getText().toString().trim();
        String item3 = etItem3.getText().toString().trim();
        String item1Price = etItem1Price.getText().toString().trim();
        String item2Price = etItem2Price.getText().toString().trim();
        String item3Price = etItem3Price.getText().toString().trim();

        String description = "";
        if (!isEmpty(item1) && !isEmpty(item1Price)){
            description += item1 + (Integer.parseInt(item1Price) != 0 ? " " + item1Price : "");
        }
        if (!isEmpty(item2) && !isEmpty(item2Price)){
            description += ", " + item2 + (Integer.parseInt(item2Price) != 0 ? " " + item2Price : "");;
        }
        if (!isEmpty(item3) && !isEmpty(item3Price)) {
            description += ", " + item3 + (Integer.parseInt(item3Price) != 0 ? " " + item3Price : "");;
        }

        if (addCount > 0 && !isEmpty(item1) && !isEmpty(item2) && !isEmpty(item3)
            && !isEmpty(item1Price) && !isEmpty(item2Price) && !isEmpty(item3Price)) {
            for (int i = 1; i < addCount + 1; i++) {
                description += ", ";
                description += idsEditTextMap.get(i).getText().toString().trim() +
                        (Integer.parseInt(idsEditTextPriceMap.get(i + 99).getText().toString().trim()) != 0 ? " "
                                + idsEditTextPriceMap.get(i + 99).getText().toString().trim() : "");
            }
        }

        Record newRecord = new Record();
        newRecord.setDate(fixDateFromPicker(datePicker));
        newRecord.setDescription(description);
        recordHelper.insert(newRecord);

        NavHostFragment.findNavController(this).navigate(R.id.action_recordFragment_to_monthFragment);
    }

    private void setAnimation(View viewToAnimate, boolean up) {
        if (up) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in);
            viewToAnimate.startAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_in);
            viewToAnimate.startAnimation(animation);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recordHelper != null){
            recordHelper.close();
        }
    }
}