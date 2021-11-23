package com.taiwanmobile.volunteers.v2.ui_component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taiwanmobile.volunteers.R;

import java.util.Locale;

public class NumberPicker extends RelativeLayout {

    private int value = 0;
    private int maxValue = 1;
    private int minValue = 0;
    private EditText numberEt;
    private View addBtn;
    private View removeBtn;
    private TextView nameTv;
    private TextView tailTv;


    public NumberPicker(Context context) {
        super(context);
        init();
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.ui_number_picker, null);
        addView(root);

        nameTv = root.findViewById(R.id.number_picker_name);
        numberEt = root.findViewById(R.id.number_picker_number);
        addBtn = root.findViewById(R.id.number_picker_add);
        removeBtn = root.findViewById(R.id.number_picker_remove);
        tailTv = root.findViewById(R.id.number_picker_tail);

        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                value++;
                refresh();
            }
        });

        removeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                value--;
                refresh();
            }
        });

        numberEt.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int t;
                try {
                    String text = numberEt.getText().toString();
                    t = Integer.parseInt(text);
                } catch (Exception e) {
                    t = 0;
                }
                if(t != value) {
                    value = t;
                    refresh();
                }
            }
        });


        refresh();
    }

    public void setName(String text) {
        nameTv.setText(text);
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        tailTv.setText(String.format(Locale.TAIWAN, "剩餘%d個", maxValue));
        refresh();
    }
    public void setMinValue(int minValue) {
        this.minValue = minValue;
        refresh();
    }

    public int getValue() {
        return value;
    }
    public String getName() {
        return nameTv.getText().toString();
    }

    private void refresh() {
        if(value > maxValue) {
            value = maxValue;
        }
        if(value < minValue) {
            value = minValue;
        }
        numberEt.setText(String.valueOf(value));
    }
}
