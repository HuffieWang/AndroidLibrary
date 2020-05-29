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

    private boolean isParentScrollView = false;

    private ImageLoader imageLoader;

    private PictureAdapter pictureAdapter;

    public MSViewPager(@NonNull Context context) {
        super(context);
    }

    public MSViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageSource(OnImageClickListener onImageClickListener, Integer... integers){
        this.onImageClickListener = onImageClickListener;
        pictureAdapter = new PictureAdapter(getContext(), integers);
        setAdapter(pictureAdapter);
    }

    public void setImageSource(OnImageClickListener onImageClickListener, List<String> strings){
        this.onImageClickListener = onImageClickListener;
        pictureAdapter = new PictureAdapter(getContext(), strings);
        setAdapter(pictureAdapter);
    }

    public void notifyDataSetChanged(){
        if(pictureAdapter != null){
            pictureAdapter.notifyDataSetChanged();
        }
    }

    public class PictureAdapter extends PagerAdapter{

        private Integer[] list;
        private List<String> stringList;
        private Context context;

        public PictureAdapter(Context context, Integer... integers) {
            this.context = context;
            this.list = integers;
        }

        public PictureAdapter(Context context, List<String> strings) {
            this.context = context;
            this.stringList = strings;
        }

        @Override
        public int getCount() {
            return list != null ? list.length : stringList.size();
        }


        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            MSImageView iv = new MSImageView(context);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            if(list != null){
                iv.setImageResource(list[position]);
            } else {
                if(imageLoader != null){
                    imageLoader.create(iv, stringList.get(position), position);
                } else {
                    iv.load(stringList.get(position));
                }
            }
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

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public interface ImageLoader {
        void create(MSImageView imageView, String url, int position);
    }
}
