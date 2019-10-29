package com.musheng.android.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.musheng.android.common.util.NumberUtils;
import com.musheng.android.library.R;

import static android.content.Context.CLIPBOARD_SERVICE;


/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/30 10:30
 * Description :
 */
public class MSEditText extends AppCompatEditText {

    public static final int EASY_TYPE_PHONE = 1;
    public static final int EASY_TYPE_VERIFY_CODE = 2;
    public static final int EASY_TYPE_ACCOUNT = 3;
    public static final int EASY_TYPE_PASSWORD = 4;
    public static final int EASY_TYPE_PAY_PASSWORD = 5;
    public static final int EASY_TYPE_TOKEN = 6;
    public static final int EASY_TYPE_CNY = 7;

    public static final String PATTERN_PHONE = "^[0-9]{1,11}$";
    public static final String PATTERN_VERIFY_CODE = "^[0-9]{1,6}$";
    public static final String PATTERN_ACCOUNT = "^[a-zA-Z0-9]{1,20}$";
    public static final String PATTERN_PASSWORD = "^[a-zA-Z0-9]{1,15}$";
    public static final String PATTERN_PAY_PASSWORD = "^[0-9]{1,6}$";
    public static final String PATTERN_TOKEN = "^[-.0-9]{1,30}$";
    public static final String PATTERN_CNY = "^[-.0-9]{1,30}$";

    private String pattern;
    private TextWatcher patternWatcher;

    private int scale;
    private TextWatcher scaleWatcher;

    private int maxNum;
    private int minNum;
    private MinMaxTextWatcher maxMinNumWatcher;

    public MSEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MSEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MSEditText, defStyleAttr, defStyleAttr);
        boolean bgEnable = array.getBoolean(R.styleable.MSEditText_ms_edit_bg_enable, false);
        if (!bgEnable){
            setBackground(null);
        }
        if (array.hasValue(R.styleable.MSEditText_ms_edit_font)){
            try {
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + array.getString(R.styleable.MSEditText_ms_edit_font)));
            } catch (Exception ignored){
            }
        }
        if (array.getBoolean(R.styleable.MSEditText_ms_edit_type_phone, false)){
            setEasyType(EASY_TYPE_PHONE);
        }
        if (array.getBoolean(R.styleable.MSEditText_ms_edit_type_verify_code, false)){
            setEasyType(EASY_TYPE_VERIFY_CODE);
        }
        if (array.getBoolean(R.styleable.MSEditText_ms_edit_type_account, false)){
            setEasyType(EASY_TYPE_ACCOUNT);
        }
        if (array.getBoolean(R.styleable.MSEditText_ms_edit_type_password, false)){
            setEasyType(EASY_TYPE_PASSWORD);
        }
        if (array.getBoolean(R.styleable.MSEditText_ms_edit_type_pay_password, false)){
            setEasyType(EASY_TYPE_PAY_PASSWORD);
        }
        if (array.getBoolean(R.styleable.MSEditText_ms_edit_type_token, false)){
            setEasyType(EASY_TYPE_TOKEN);
        }
        if (array.getBoolean(R.styleable.MSEditText_ms_edit_type_cny, false)){
            setEasyType(EASY_TYPE_CNY);
        }
        if (array.hasValue(R.styleable.MSEditText_ms_edit_pattern)){
            String pattern = array.getString(R.styleable.MSEditText_ms_edit_pattern);
            if(!TextUtils.isEmpty(pattern)){
                setPattern(pattern);
            }
        }
        if (array.hasValue(R.styleable.MSEditText_ms_edit_scale)){
            int scale = array.getInteger(R.styleable.MSEditText_ms_edit_scale, 2);
            setScale(scale);
        }
        array.recycle();

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(TextUtils.isEmpty(getText().toString())){
                    ClipboardManager clipboard = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
                    if(clipboard != null){
                        ClipData primaryClip = clipboard.getPrimaryClip();
                        if(primaryClip != null && primaryClip.getItemCount() > 0){
                            ClipData.Item clipItem = primaryClip.getItemAt(0);
                            setText(clipItem.getText().toString());
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if(getText() != null){
            setSelection(getText().toString().length());
        }
    }

    public void setEasyType(int type){
        switch (type){
            case EASY_TYPE_PHONE:
                setInputType(InputType.TYPE_CLASS_PHONE);
                setPattern(PATTERN_PHONE);
                break;
            case EASY_TYPE_VERIFY_CODE:
                setInputType(InputType.TYPE_CLASS_PHONE);
                setPattern(PATTERN_VERIFY_CODE);
                break;
            case EASY_TYPE_ACCOUNT:
                setInputType(InputType.TYPE_CLASS_TEXT);
                setPattern(PATTERN_ACCOUNT);
                break;
            case EASY_TYPE_PASSWORD:
                setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                setPattern(PATTERN_PASSWORD);
                break;
            case EASY_TYPE_PAY_PASSWORD:
                setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                setPattern(PATTERN_PAY_PASSWORD);
                break;
            case EASY_TYPE_TOKEN:
                setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                setScale(8);
                setPattern(PATTERN_TOKEN);
                break;
            case EASY_TYPE_CNY:
                setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                setScale(2);
                setPattern(PATTERN_CNY);
                break;
        }
    }

    public void setPattern(String p) {
        this.pattern = p;
        if(patternWatcher == null){
            patternWatcher = new TextWatcher() {
                String before = "";

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (pattern != null) {
                        if (!s.toString().matches(pattern) && !"".equals(s.toString())) {
                            setText(before);
                            if (getText() != null) {
                                setSelection(getText().toString().length());
                            }
                        } else {
                            before = s.toString();
                        }
                    }
                }
            };
            addTextChangedListener(patternWatcher);
        }
    }

    public void setScale(int inputScale){
        this.scale = inputScale;
        if(scaleWatcher == null){
            scaleWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    //删除“.”后面超过2位后的数据
                    if (s.toString().contains(".")) {
                        if (s.length() - 1 - s.toString().indexOf(".") > scale) {
                            s = s.toString().subSequence(0,
                                    s.toString().indexOf(".") + scale+1);
                            setText(s);
                            setSelection(s.length()); //光标移到最后
                        }
                    }
                    //如果"."在起始位置,则起始位置自动补0
                    if (s.toString().trim().substring(0).equals(".")) {
                        s = "0" + s;
                        setText(s);
                        setSelection(2);
                    }

                    //如果起始位置为0,且第二位跟的不是".",则无法后续输入
                    if (s.toString().startsWith("0")
                            && s.toString().trim().length() > 1) {
                        if (!s.toString().substring(1, 2).equals(".")) {
                            setText(s.subSequence(0, 1));
                            setSelection(1);
                            return;
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };
            addTextChangedListener(scaleWatcher);
        }
    }



    private class MinMaxTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(TextUtils.isEmpty(editable.toString())){
                return;
            }
            int i = NumberUtils.parseInt(editable.toString(), 0);

            if(i > maxNum){
                setText(String.valueOf(maxNum));
            }

            if(i < minNum){
                setText(String.valueOf(minNum));
            }
        }
    }

    public void setMaxNum(int maxNum){
        this.maxNum = maxNum;
        if(maxMinNumWatcher == null){
            maxMinNumWatcher = new MinMaxTextWatcher();
            addTextChangedListener(maxMinNumWatcher);
        }
    }

    public void setMinNum(int minNum){
        this.minNum = minNum;
        if(maxMinNumWatcher == null){
            maxMinNumWatcher = new MinMaxTextWatcher();
            addTextChangedListener(maxMinNumWatcher);
        }
    }


}
