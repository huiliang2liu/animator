package com.lhl.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PathAnimator implements Interpolator {
    private PathMeasure measure;
    private float[] mCurrentPosition = new float[2];
    private long duration = 300;
    private int repeatCount = 1;
    private View view;
    private int repeatMode = ValueAnimator.RESTART;
    private Animator.AnimatorListener listener;
    private Animator.AnimatorListener defaultListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    private Animator.AnimatorListener proxy = (Animator.AnimatorListener) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Animator.AnimatorListener.class}, new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.d("PathAnimator", method.getName());
            return method.invoke(listener == null ? defaultListener : listener, args);
        }
    });

    public PathAnimator(Path path, View view) {
        this(path, view, false);
    }

    public PathAnimator setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }

    public PathAnimator setListener(Animator.AnimatorListener listener) {
        this.listener = listener;
        return this;
    }

    public PathAnimator setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
        return this;
    }

    public PathAnimator setDuration(long duration) {
        if (duration > 0)
            this.duration = duration;
        return this;
    }

    public PathAnimator(Path path, View view, boolean forceClosed) {
        if (path == null)
            throw new RuntimeException("you path is null");
        if (view == null)
            throw new RuntimeException("you view is null");
        this.view = view;
        measure = new PathMeasure(path, forceClosed);
    }

    public Animator pathAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, measure.getLength());
        animator.setDuration(duration);
        if (repeatCount == -1 || repeatCount > 1) {
            animator.setRepeatCount(repeatCount);
            animator.setRepeatMode(repeatMode);
        }

        animator.setInterpolator(this);
        animator.addUpdateListener(
                animation -> {
                    float value = (float) animation.getAnimatedValue();
                    measure.getPosTan(value, mCurrentPosition, null);
                    view.setTranslationX(mCurrentPosition[0]);
                    view.setTranslationY(mCurrentPosition[1]);
                });
        animator.addListener(proxy);
        return animator;
    }

    public void startAnimation() {
        pathAnimation().start();
    }

    @Override
    public float getInterpolation(float input) {
        return input;
    }

}
