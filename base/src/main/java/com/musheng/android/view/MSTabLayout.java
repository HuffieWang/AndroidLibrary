package com.musheng.android.view;

import android.content.Context;
import android.util.AttributeSet;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/3 17:18
 * Description :
 */
public class MSTabLayout extends CommonTabLayout {

    public MSTabLayout(Context context) {
        super(context);
    }

    public MSTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MSTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static class MSTabEntity implements CustomTabEntity {

        private String tabTitle;
        private int selectedIcon;
        private int unselectedIcon;

        public MSTabEntity(String tabTitle, int selectedIcon, int unselectedIcon) {
            this.tabTitle = tabTitle;
            this.selectedIcon = selectedIcon;
            this.unselectedIcon = unselectedIcon;
        }

        @Override
        public String getTabTitle() {
            return tabTitle;
        }

        @Override
        public int getTabSelectedIcon() {
            return selectedIcon;
        }

        @Override
        public int getTabUnselectedIcon() {
            return unselectedIcon;
        }
    }
}
