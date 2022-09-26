package com.lhl.animation.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lhl.animation.AnimatorFactory;
import com.lhl.animation.ViewEmbellish;

import java.util.ArrayList;
import java.util.List;


public class CutoverLoadingView extends FrameLayout {
    private int width;
    private int height;
    private int imageWidth;
    private int imageHeight;
    private int index = 0;
    private ImageView imageView;
    private AnimatorSet animatorSet;
    private List<Integer> imageRes = new ArrayList<>();

    {

    }

    public CutoverLoadingView(@NonNull Context context) {
        super(context);
    }

    public CutoverLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CutoverLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CutoverLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setImageRes(List<Integer> imageRes) {
        assert imageRes != null && !imageRes.isEmpty() : "imageRes is null or empty";
        this.imageRes = imageRes;
        index = 0;
        if (imageView != null) {
            imageView.setImageResource(imageRes.get(index));
        }
    }

    public void startAnimator() {
        if (animatorSet != null)
            animatorSet.pause();
        ObjectAnimator animator = AnimatorFactory.topMargin(new ViewEmbellish(imageView), 1000, 0, imageHeight);
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addListener(new Animator.AnimatorListener() {
            int target = 0;

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
                target++;
                if (target % 2 == 0)
                    return;
                index++;
                index = index % imageRes.size();
                imageView.setImageResource(imageRes.get(index));
            }
        });

        animatorSet = new AnimatorSet();
        AnimatorSet.Builder builder = animatorSet.play(animator);


        animator = AnimatorFactory.scaleX(new ViewEmbellish(imageView), 1000, 1, 0);
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        builder.with(animator);

        animator = AnimatorFactory.scaleY(new ViewEmbellish(imageView), 1000, 1, 0);
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        builder.with(animator);

        animatorSet.start();
    }

    @Override

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (imageView != null)
            return;
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        imageView = new ImageView(getContext());
        imageWidth = width >> 1;
        imageHeight = height >> 1;
        LayoutParams layoutParams = new LayoutParams(imageWidth, imageHeight);
        layoutParams.leftMargin = imageWidth >> 1;
        addView(imageView, layoutParams);
        if (imageRes == null || imageRes.size() <= 0)
            return;
        imageView.setImageResource(imageRes.get(index));
    }
}
