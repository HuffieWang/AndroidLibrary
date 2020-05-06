package com.musheng.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/3 17:21
 * Description :
 */
public class MSViewPager extends ViewPager {

    private OnImageClickListener onImageClickListener;

    private boolean isParentScrollView = true;

    public MSViewPager(@NonNull Context context) {
        super(context);
    }

    public MSViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageSource(OnImageClickListener onImageClickListener, Integer... integers){
        this.onImageClickListener = onImageClickListener;
        PictureAdapter pictureAdapter = new PictureAdapter(getContext(), integers);
        setAdapter(pictureAdapter);
    }

    public class PictureAdapter extends PagerAdapter{

        private Integer[] list;
        private Context context;

        public PictureAdapter(Context context, Integer... integers) {
            this.context = context;
            this.list = integers;
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(list[position]);

            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onImageClickListener != null){
                        onImageClickListener.onClick(position);
                    }
                }
            });

            container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public interface OnImageClickListener{
        void onClick(int position);
    }

    public void setParentScrollView(boolean parentScrollView) {
        isParentScrollView = parentScrollView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(isParentScrollView){
            int height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > height)
                    height = h;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
