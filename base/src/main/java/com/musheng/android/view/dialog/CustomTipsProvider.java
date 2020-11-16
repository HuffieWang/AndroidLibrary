package com.musheng.android.view.dialog;

/**
 * @Name CustomTipsProvider
 * 自定义Toast布局Provider
 * @Author LDL
 * @Date 2020/10/29 14:17
 */
public interface CustomTipsProvider {

    int createView();

    int createMsgView();

    int createExpressionView();

    int createLeftExpressionView();

    int createResultIconView();

    int successResourcesId();

    int failResourcesId();


}
