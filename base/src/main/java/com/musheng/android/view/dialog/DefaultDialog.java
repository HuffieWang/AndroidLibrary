package com.musheng.android.view.dialog;

import android.content.Context;
import android.view.View;

import com.musheng.android.library.R;
import com.musheng.android.view.MSTextView;

import razerdp.basepopup.BasePopupWindow;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/8 17:01
 * Description :
 */
public class DefaultDialog extends BasePopupWindow {


    private MSTextView title;
    private MSTextView enterButton;
    private MSTextView cancelButton;
    private DefaultDialog.OnCompleteListener listener;

    public DefaultDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {

        View popupById = createPopupById(R.layout.view_default_dialog);
        title = popupById.findViewById(R.id.tv_title);
        enterButton = popupById.findViewById(R.id.btn_enter);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onEnter();
                }
                dismiss();
            }
        });
        cancelButton = popupById.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onCancel();
                }
                dismiss();
            }
        });
        return popupById;
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setCancelText(String text){
        cancelButton.setText(text);
    }

    public void setEnterText(String text){
        enterButton.setText(text);
    }

    public void setCancelable(boolean cancelable){
        setOutSideDismiss(cancelable);
        setBackPressEnable(cancelable);
    }

    public DefaultDialog setListener(OnCompleteListener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnCompleteListener {
        void onEnter();
        void onCancel();
    }
}
