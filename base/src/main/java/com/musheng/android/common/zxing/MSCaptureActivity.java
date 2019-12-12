package com.musheng.android.common.zxing;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.musheng.android.common.glide.Glide4Engine;
import com.musheng.android.common.log.MSLog;
import com.musheng.android.common.util.SystemUtils;
import com.musheng.android.library.R;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.util.List;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/12/11 18:02
 * Description :
 */
public class MSCaptureActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 500;

    private CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            MSCaptureActivity.this.setResult(RESULT_OK, resultIntent);
            MSCaptureActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            MSCaptureActivity.this.setResult(RESULT_OK, resultIntent);
            MSCaptureActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ms_qrscan);
        /**
         * 执行扫面Fragment的初始化操作
         */
        CaptureFragment captureFragment = new CaptureFragment();

        captureFragment.setAnalyzeCallback(analyzeCallback);
        /**
         * 替换我们的扫描控件
         */
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

        View view = findViewById(R.id.tv_gallery);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Matisse.from(MSCaptureActivity.this)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(9)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new Glide4Engine())
                        .forResult(REQUEST_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            List<Uri> selected = Matisse.obtainResult(data);
            if (selected != null && !selected.isEmpty()) {
                try {
                    String realFilePath = SystemUtils.getRealFilePath(this, selected.get(0));
                    MSLog.d("real " + realFilePath);
                    CodeUtils.analyzeBitmap(realFilePath, new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            MSLog.d("analyzeBitmap success");
                            Intent resultIntent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
                            bundle.putString(CodeUtils.RESULT_STRING, result);
                            resultIntent.putExtras(bundle);
                            MSCaptureActivity.this.setResult(RESULT_OK, resultIntent);
                            MSCaptureActivity.this.finish();
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            MSLog.d("analyzeBitmap fail");
                            Intent resultIntent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
                            bundle.putString(CodeUtils.RESULT_STRING, "");
                            resultIntent.putExtras(bundle);
                            MSCaptureActivity.this.setResult(RESULT_OK, resultIntent);
                            MSCaptureActivity.this.finish();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
