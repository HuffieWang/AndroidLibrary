package com.musheng.android.view.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.musheng.android.library.R;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/11 19:25
 * Description : 滚动指示器，可用于把滚动视图返回到顶部，同时也可以显示当前显示的页数
 * 
 */
public class MSScrollIndicator extends RelativeLayout {

    private View rootView;
    private View goTopView;
    private RelativeLayout pageLayout;

    private TextView currentPage;
    private TextView totalPage;
    private RecyclerView.OnScrollListener scrollListener;

    private boolean isAnimationRunning;
    private int currentState;

    public MSScrollIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rootView = LayoutInflater.from(context).inflate(R.layout.view_ms_scroll_inficator, this);
        goTopView = rootView.findViewById(R.id.ms_scroll_indicator_go_top);
        pageLayout = rootView.findViewById(R.id.ms_scroll_indicator_page_layout);
        currentPage = rootView.findViewById(R.id.ms_scroll_indicator_current_page);
        totalPage = rootView.findViewById(R.id.ms_scroll_indicator_total_page);
    }

    public void bindRecyclerView(RecyclerView recyclerView){
        bindRecyclerView(recyclerView, 0);
    }

    public void bindRecyclerView(final RecyclerView recyclerView, final int pageSize){

        //点击返回顶部
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
            }
        });

        if(scrollListener != null){
            recyclerView.removeOnScrollListener(scrollListener);
        }
        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //未开启页数提示时，不显示页数提示
                if(pageSize <= 0){
                    return;
                }
                int totalCount = recyclerView.getAdapter() == null ? 0 : recyclerView.getAdapter().getItemCount();
                //Adapter数据为空时，不显示页数提示
                if(totalCount <= 0){
                    return;
                }
                totalPage.setText(String.valueOf(totalCount / pageSize));
                //滚动状态未改变或者为滚动中时，不进行切换动画（返回顶部<--->页数提示）
                if(newState == SCROLL_STATE_SETTLING || currentState == newState){
                    return;
                }
                currentState = newState;
                //当前有动画未结束时，不进行新的切换动画，动画结束后会根据currentState判断是否进行新的动画
                if(isAnimationRunning){
                    return;
                }
                runRotateAnimation(newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    //获取RecyclerView当前顶部显示的第一个条目对应的索引
                    int position = layoutManager.findFirstVisibleItemPosition();
                    //根据索引来获取对应的itemView
                    View firstVisibleChildView = layoutManager.findViewByPosition(position);
                    if(firstVisibleChildView != null){
                        //获取当前显示条目的高度
                        int itemHeight = firstVisibleChildView.getHeight();
                        //获取当前RecyclerView 偏移量
                        int flag = (position) * itemHeight - firstVisibleChildView.getTop();
                        //注意事项：RecyclerView不要设置padding
                        if(flag == 0) {
                            setVisibility(View.GONE);
                        } else {
                            setVisibility(View.VISIBLE);
                        }
                    }
                    if(pageSize > 0){
                        currentPage.setText(String.valueOf((position+1) / pageSize));
                    }
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void runRotateAnimation(final int newState){
        isAnimationRunning = true;
        Rotate3dAnimation.applyRotation(rootView, 0, 90, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(newState == SCROLL_STATE_IDLE){
                    pageLayout.setVisibility(GONE);
                    goTopView.setVisibility(VISIBLE);
                } else {
                    pageLayout.setVisibility(VISIBLE);
                    goTopView.setVisibility(GONE);
                }
                Rotate3dAnimation.applyRotation(rootView, -90, 0, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if(currentState != newState){
                            runRotateAnimation(currentState);
                        } else {
                            isAnimationRunning = false;
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void debug(String msg){
        Log.d("wangyufei", msg);
    }
}
