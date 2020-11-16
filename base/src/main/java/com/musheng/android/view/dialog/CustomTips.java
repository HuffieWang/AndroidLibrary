package com.musheng.android.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.musheng.android.library.R;
import com.musheng.android.view.MSImageView;
import com.musheng.android.view.MSTextView;

/**
 * @Name CustomTips
 * 自定义布局Toast
 * @Author LDL
 * @Date 2020/10/29 14:14
 */
public class CustomTips {

    private static CustomTips customTips;

    private View contentView;

    private CustomTipsProvider customTipsProvider;

    private Toast dialog;

    private Handler handler = new Handler();

    private Context context;

    private boolean isSuccess = true;

    private boolean isShowResultIcon = true;

    public static CustomTips getInstance(){
        if(customTips == null){
            customTips = new CustomTips();
        }
        return customTips;
    }

    public void init(Context context){
        this.context = context;
        createDefaultDialog(R.layout.view_default_custom_tips);
    }

    public void dissmiss(){
        if(dialog!=null){
//            handler.removeCallbacks(tipsRunnable);
//            dialog.dismiss();
        }
    }

    private void createDefaultDialog(int layoutId){
//        if(context.isFinishing()){
//            return;
//        }
        if(dialog == null){
            dialog = new Toast(context);
        }
        contentView = LayoutInflater.from(context).inflate(layoutId,null);
        dialog.setView(contentView);
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        dialog.getWindow().getAttributes().dimAmount = 0f;
//        dialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
//        dialog.getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void createDefaultDialog(View view){
        if(dialog == null){
            dialog = new Toast(context);
        }
        contentView = view;
        dialog.setView(contentView);
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        dialog.getWindow().getAttributes().dimAmount = 0f;
//        dialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
//        dialog.getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void showTips(View view,String msg,boolean isSuccess,boolean isShowResultIcon){
        this.isSuccess = isSuccess;
        this.isShowResultIcon = isShowResultIcon;
        createDefaultDialog(view);
        showTips(msg);
    }

    public void showTips(View view,String msg,int gravity,boolean isSuccess,boolean isShowResultIcon){
        createDefaultDialog(view);
        showTips(msg,gravity,isSuccess,isShowResultIcon);
    }

    public void showTips(String msg){
        showTips(msg,Gravity.BOTTOM,true,false);
    }

    public void showTips(String msg,boolean isSuccess){
        showTips(msg,Gravity.BOTTOM,isSuccess,true);
    }

    public void showTips(String msg,boolean isSuccess,boolean isShowResultIcon){
        this.isSuccess = isSuccess;
        this.isShowResultIcon = isShowResultIcon;
        showTips(msg,Gravity.BOTTOM,isSuccess,isShowResultIcon);
    }

    public void showTips(String msg,int imgId,int gravity,boolean isSuccess,boolean isShowResultIcon){
        showTips(msg,-1,imgId,gravity,isSuccess,isShowResultIcon);
    }

    public void showTips(String msg,int leftImg,int rightImg,int gravity,boolean isSuccess,boolean isShowResultIcon){
        this.isSuccess = isSuccess;
        this.isShowResultIcon = isShowResultIcon;
        if(customTipsProvider != null){
            int layoutId = customTipsProvider.createView();
            createDefaultDialog(layoutId);
        }
        show(msg,leftImg,rightImg,gravity);
    }

    public void showTips(String msg,int gravity,boolean isSuccess,boolean isShowResultIcon){
        showTips(msg,-1,-1,gravity,isSuccess,isShowResultIcon);
    }

    Runnable tipsRunnable = new Runnable() {
        @Override
        public void run() {
//            if(dialog != null && dialog.isShowing()){
//                context.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.dismiss();
//                    }
//                });
//            }
        }
    };

    public void show(String msg){
//        dialog.getWindow().getAttributes().width = WindowManager.LayoutParams.WRAP_CONTENT;
//        dialog.getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
        show(msg,Gravity.BOTTOM);
    }

    public void show(String msg,int gravity){
        show(msg,-1,-1,gravity);
    }

    public void show(String msg,int expresstionImg,int gravity){
        show(msg,expresstionImg,gravity,false);
    }

    public void show(String msg,int expresstionImg,int gravity,boolean isLeftExPression){
        if(isLeftExPression){
            show(msg,expresstionImg,-1,gravity);
        }else{
            show(msg,-1,expresstionImg,gravity);
        }
    }

    private void show(String msg,int leftExpresstionImg,int rightExpresstionImg,int gravity){
       try{
//           if(context.isFinishing()){
//               return;
//           }
//           if(dialog != null && !dialog.isShowing()){
//               dialog.getWindow().setGravity(gravity);
//               dialog.show();
//               handler.removeCallbacks(tipsRunnable);
//               handler.postDelayed(tipsRunnable,2000);
//           }
           dialog.setGravity(Gravity.CENTER, 0, 0);
           dialog.show();
           if(customTipsProvider != null){
               View view = contentView.findViewById(customTipsProvider.createMsgView());
               ((MSTextView)view).setText(Html.fromHtml(msg));
               if(leftExpresstionImg != -1){
                   contentView.findViewById(customTipsProvider.createLeftExpressionView()).setVisibility(View.VISIBLE);
                   ((MSImageView)contentView.findViewById(customTipsProvider.createLeftExpressionView())).setImageResource(leftExpresstionImg);
               }else{
                   if(contentView.findViewById(customTipsProvider.createLeftExpressionView()) != null){
                       contentView.findViewById(customTipsProvider.createLeftExpressionView()).setVisibility(View.GONE);
                   }
               }
               if(rightExpresstionImg != -1){
                   contentView.findViewById(customTipsProvider.createExpressionView()).setVisibility(View.VISIBLE);
                   ((MSImageView)contentView.findViewById(customTipsProvider.createExpressionView())).setImageResource(rightExpresstionImg);
               }else{
                   if(contentView.findViewById(customTipsProvider.createExpressionView()) != null){
                       contentView.findViewById(customTipsProvider.createExpressionView()).setVisibility(View.GONE);
                   }
               }
               if(isShowResultIcon){
                   View resultView = contentView.findViewById(customTipsProvider.createResultIconView());
                   if(resultView != null){
                       resultView.setVisibility(View.VISIBLE);
                       if(isSuccess){
                           resultView.setBackgroundResource(customTipsProvider.successResourcesId());
                       }else{
                           resultView.setBackgroundResource(customTipsProvider.failResourcesId());
                       }
                   }
               }else{
                   View resultView = contentView.findViewById(customTipsProvider.createResultIconView());
                   if(resultView != null){
                       resultView.setVisibility(View.GONE);
                   }
               }
           }else{
               View view = contentView.findViewById(R.id.tv_tips);
               ((MSTextView)view).setText(Html.fromHtml(msg));
//            throw new NullPointerException("Please ready your CustomTipsProvider !!");
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    public void setCustomTipsProvider(CustomTipsProvider customTipsProvider){
        this.customTipsProvider = customTipsProvider;
    }


}
