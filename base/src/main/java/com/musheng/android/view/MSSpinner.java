package com.musheng.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import java.util.List;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/12 17:14
 * Description :
 */
public class MSSpinner extends AppCompatSpinner {

    private Context context;

    public MSSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MSSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setData(int layoutID, int itemID, List<String> data){
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, layoutID);
        stringArrayAdapter.setDropDownViewResource(itemID);
        stringArrayAdapter.addAll(data);
        setAdapter(stringArrayAdapter);
    }

    public void setData(int layoutID, List<String> data){
        setData(layoutID, android.R.layout.simple_spinner_dropdown_item, data);
    }

    public void setData(List<String> data){
        setData(android.R.layout.simple_spinner_item, android.R.layout.simple_spinner_dropdown_item, data);
    }
}
