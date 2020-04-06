package com.musheng.android.view;

import android.view.View;
import android.widget.ImageView;

/**
 * Author      : MuSheng
 * CreateDate  : 2020/2/21 17:10
 * Description :
 */
public class MSSwitchEquip {
    private ImageView imageView;
    private boolean isOn;
    private int onResId;
    private int offResId;
    private OnSwitchChangeListener onSwitchChangeListener;

    public MSSwitchEquip(ImageView imageView, final boolean isOn, int onResId, int offResId) {
        this.imageView = imageView;
        this.isOn = isOn;
        this.onResId = onResId;
        this.offResId = offResId;
        setOn(isOn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOn(!isOn());
                if(onSwitchChangeListener != null){
                    onSwitchChangeListener.onSwitch(isOn());
                }
            }
        });
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
        if(isOn){
            imageView.setImageResource(onResId);
        } else {
            imageView.setImageResource(offResId);
        }
    }

    public void setOnSwitchChangeListener(OnSwitchChangeListener onSwitchChangeListener) {
        this.onSwitchChangeListener = onSwitchChangeListener;
    }

    public interface OnSwitchChangeListener {
        void onSwitch(boolean isOn);
    }

}
