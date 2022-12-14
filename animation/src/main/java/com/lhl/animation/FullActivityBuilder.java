package com.lhl.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FullActivityBuilder {
    private Activity mActivity;
    private View mTriggerView;
    private float mStartRadius = CircularAnim.MINI_RADIUS;
    private int mColorOrImageRes = CircularAnim.getColorOrImageRes();
    private Long mDurationMills;
    private CircularAnim.OnAnimatorDeployListener mStartAnimatorDeployListener;
    private CircularAnim.OnAnimatorDeployListener mReturnAnimatorDeployListener;
    private CircularAnim.OnAnimationEndListener mOnAnimationEndListener;
    private int mEnterAnim = R.anim.fade_actiivity_in, mExitAnim = R.anim.fade_activity_out;

    public FullActivityBuilder(Activity activity, View triggerView) {
        mActivity = activity;
        mTriggerView = triggerView;
    }

    public FullActivityBuilder startRadius(float startRadius) {
        mStartRadius = startRadius;
        return this;
    }

    public FullActivityBuilder colorOrImageRes(int colorOrImageRes) {
        mColorOrImageRes = colorOrImageRes;
        return this;
    }

    public FullActivityBuilder duration(long durationMills) {
        mDurationMills = durationMills;
        return this;
    }

    public FullActivityBuilder overridePendingTransition(int enterAnim, int exitAnim) {
        mEnterAnim = enterAnim;
        mExitAnim = exitAnim;
        return this;
    }

    public FullActivityBuilder deployStartAnimator(CircularAnim.OnAnimatorDeployListener onAnimatorDeployListener) {
        mStartAnimatorDeployListener = onAnimatorDeployListener;
        return this;
    }

    public FullActivityBuilder deployReturnAnimator(CircularAnim.OnAnimatorDeployListener onAnimatorDeployListener) {
        mReturnAnimatorDeployListener = onAnimatorDeployListener;
        return this;
    }

    public void go(CircularAnim.OnAnimationEndListener onAnimationEndListener) {
        mOnAnimationEndListener = onAnimationEndListener;
        // ????????????,??????5.0????????????.
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            doOnEnd();
            return;
        }

        int[] location = new int[2];
        mTriggerView.getLocationInWindow(location);
        final int cx = location[0] + mTriggerView.getWidth() / 2;
        final int cy = location[1] + mTriggerView.getHeight() / 2;
        final ImageView view = new ImageView(mActivity);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setImageResource(mColorOrImageRes);
        final ViewGroup decorView = (ViewGroup) mActivity.getWindow().getDecorView();
        int w = decorView.getWidth();
        int h = decorView.getHeight();
        decorView.addView(view, w, h);

        // ??????????????????view?????????????????????
        int maxW = Math.max(cx, w - cx);
        int maxH = Math.max(cy, h - cy);
        final int finalRadius = (int) Math.sqrt(maxW * maxW + maxH * maxH) + 1;

        try {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, mStartRadius, finalRadius);

            int maxRadius = (int) Math.sqrt(w * w + h * h) + 1;
            // ???????????????????????????PERFECT_MILLS?????????????????????????????????????????????????????????
            if (mDurationMills == null) {
                // ??????????????????????????????????????????
                double rate = 1d * finalRadius / maxRadius;
                // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? @rate ??? 1 ?????????
                mDurationMills = (long) (CircularAnim.getFullActivityMills() * Math.sqrt(rate));
            }
            final long finalDuration = mDurationMills;
            // ??????thisActivity.startActivity()??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            anim.setDuration((long) (finalDuration * 0.9));
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    doOnEnd();

                    mActivity.overridePendingTransition(mEnterAnim, mExitAnim);

                    // ???????????????????????????Activity?????????.
                    mTriggerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mActivity.isFinishing())
                                return;
                            try {
                                Animator returnAnim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
                                        finalRadius, mStartRadius);
                                returnAnim.setDuration(finalDuration);
                                returnAnim.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        try {
                                            decorView.removeView(view);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                if (mReturnAnimatorDeployListener != null)
                                    mReturnAnimatorDeployListener.deployAnimator(returnAnim);
                                returnAnim.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {
                                    decorView.removeView(view);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }, 1000);

                }
            });
            if (mStartAnimatorDeployListener != null)
                mStartAnimatorDeployListener.deployAnimator(anim);
            anim.start();
        } catch (Exception e) {
            e.printStackTrace();
            doOnEnd();
        }
    }

    private void doOnEnd() {
        mOnAnimationEndListener.onAnimationEnd();
    }
}
