package com.musheng.android.view.recycler;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/12 17:36
 * Description :
 */
public class Rotate3dAnimation extends Animation {

    // 开始角度
    private final float mFromDegrees;
    // 结束角度
    private final float mToDegrees;
    // 中心点
    private final float mCenterX;
    private final float mCenterY;
    private final float mDepthZ;
    // 是否需要扭曲
    private final boolean mReverse;
    // 摄像头
    private Camera mCamera;

    public Rotate3dAnimation(float fromDegrees, float toDegrees, float centerX,
                             float centerY, float depthZ, boolean reverse) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
        mReverse = reverse;
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    // 生成Transformation
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        // 生成中间角度
        float degrees = fromDegrees
                + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();
        if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }
        camera.rotateX(degrees);
        // 取得变换后的矩阵
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }

    public static void applyRotation(View view, float start, float end, AnimationListener animationListener) {
        // 计算中心点
        float centerX = view.getWidth() / 2.0f;
        float centerY = view.getHeight() / 2.0f;
        Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
                centerX, centerY, 1.0f, animationListener != null);
        rotation.setDuration(200);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        if(animationListener != null){
            rotation.setAnimationListener(animationListener);
        }
        view.startAnimation(rotation);
    }
}

