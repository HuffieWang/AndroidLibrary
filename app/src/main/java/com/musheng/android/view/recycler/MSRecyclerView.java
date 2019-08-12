package com.musheng.android.view.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.musheng.android.library.R;


/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/11 18:10
 * Description :
 */
public class MSRecyclerView extends RelativeLayout {

    private RecyclerView recyclerView;

    public MSRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_ms_recycler_view, this);
        recyclerView = view.findViewById(R.id.view_ms_recycler_view);
        MSScrollIndicator scrollIndicator = view.findViewById(R.id.view_ms_scroll_indicator);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        scrollIndicator.bindRecyclerView(recyclerView, 1);
    }

    public RecyclerView getRecyclerView(){
        return recyclerView;
    }
}
