package com.lhl.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public class ParticleAnimator {
    private ViewGroup group;
    private ParticleView[] views;

    public ParticleAnimator(Activity activity, View... views) {
        assert views != null && views.length > 0 : "views is empty or null";
        assert activity != null : "activity is null";
        group = (ViewGroup) activity.getWindow().getDecorView();
        int len = views.length;
        this.views = new ParticleView[len];
        for (int i = 0; i < len; i++)
            this.views[i] = new ParticleView(group, views[i]);
    }

    public void startAnimator(boolean show) {
        for (ParticleView particleView : views)
            particleView.startAnimator(show);
    }

    protected static class ParticleView extends View {
        private int width;
        private int height;
        private View view;
        private ViewGroup group;
        private int particleNum = 40;
        private Particle[] particles;
        private int x;
        private int y;
        float hefW;
        float hefH;
        private ValueAnimator valueAnimator;
        private boolean show = true;

        private Paint paint = new Paint();

        ParticleView(ViewGroup group, View view) {
            super(view.getContext());
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(0.5f);
            width = view.getMeasuredWidth();
            height = view.getMeasuredHeight();
            if (width <= 0 || height <= 0)
                view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        width = view.getWidth();
                        height = view.getHeight();
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            this.view = view;
            this.group = group;
        }

        public void startAnimator(boolean show) {
            this.show = show;
            if (width <= 0 || height <= 0)
                view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        width = view.getWidth();
                        height = view.getHeight();
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        createBitmap();
                    }
                });
            else
                createBitmap();
        }

        private void createBitmap() {
            if (width <= 0 || height <= 0)
                return;
            int location[] = new int[2];
            view.getLocationOnScreen(location);
            x = location[0];
            y = location[1];
            hefW = width * .5f;
            hefH = hefW * .5f;
            view.setVisibility(GONE);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(width * 2, height * 2);
            layoutParams.leftMargin = (int) (x - hefW);
            layoutParams.topMargin = (int) (y - hefH);
            group.addView(this, layoutParams);
            paint.setAlpha(show ? 0 : 255);
            int lenX = width / particleNum;
            int lenY = height / particleNum;
            Bitmap bitmap = createBitmapFromView(view);
            particles = new Particle[particleNum * particleNum];
            int[] buff = new int[lenX * lenY];
            for (int i = 0; i < particleNum; i++) {
                for (int j = 0; j < particleNum; j++) {
                    bitmap.getPixels(buff, 0, lenX, i * lenX, j * lenY, lenX, lenY);
                    Particle particle = new Particle();
                    particle.startX = hefW + i * lenX;
                    particle.startY = hefH + j * lenY;
                    float moveX = (float) (1 + Math.random()) * hefW;
                    float moveY = (float) (1 + Math.random()) * hefH;
                    boolean isSub = Math.random() > 0.5;
                    if (isSub) {
                        particle.endX = particle.startX - moveX;
                    } else
                        particle.endX = particle.startX + moveX;

                    isSub = Math.random() > 0.5;
                    if (isSub)
                        particle.endY = particle.startY - moveY;
                    else
                        particle.endY = particle.startY + moveY;
                    if (show) {
                        float mut = particle.startX;
                        particle.startX = particle.endX;
                        particle.endX = mut;
                        mut = particle.startY;
                        particle.startY = particle.endY;
                        particle.endY = mut;
                    }
                    particle.x = particle.startX;
                    particle.y = particle.startY;
                    particle.bitmap = Bitmap.createBitmap(buff, lenX, lenY, Bitmap.Config.ARGB_8888);
                    particles[i * particleNum + j] = particle;
                }
            }
            valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
            valueAnimator.setDuration(500);
            valueAnimator.addUpdateListener((a) -> {
                float input = (float) a.getAnimatedValue();
                if (particles != null && particles.length > 0) {
                    for (Particle particle : particles) {
                        particle.x = particle.startX + (particle.endX - particle.startX) * input;
                        particle.y = particle.startY + (particle.endY - particle.startY) * input;
                        paint.setAlpha((int) ((show ? input : (1 - input)) * 255));
                        invalidate();
                    }
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    clean();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    clean();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valueAnimator.start();
            invalidate();
        }

        private void clean() {
            valueAnimator = null;
            group.removeView(this);
            particles = null;
            if (show)
                view.setVisibility(VISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (particles != null && particles.length > 0)
                for (Particle particle : particles)
                    canvas.drawBitmap(particle.bitmap, particle.x, particle.y, paint);
        }
    }

    protected static class Particle {
        float startX;
        float startY;
        float endX;
        float endY;
        Bitmap bitmap;
        float x;
        float y;
    }

    public static Bitmap createBitmapFromView(View view) {
        Canvas canvas = new Canvas();
//         为什么屏蔽以下代码段？
//         如果ImageView直接得到位图，那么当它设置背景（backgroud)时，不会读取到背景颜色
//        if (view instanceof ImageView) {
//            Drawable drawable = ((ImageView)view).getDrawable();
//            if (drawable != null && drawable instanceof BitmapDrawable) {
//                return ((BitmapDrawable) drawable).getBitmap();
//            }
//        }
        //view.clearFocus(); //不同焦点状态显示的可能不同——（azz:不同就不同有什么关系？）

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        canvas.setBitmap(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}
