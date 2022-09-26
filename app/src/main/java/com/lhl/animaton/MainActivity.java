package com.lhl.animaton;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lhl.animation.CircularAnim;
import com.lhl.animation.ParticleAnimator;
import com.lhl.animation.PathAnimator;
import com.lhl.animation.widget.CutoverLoadingView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ParticleAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animator = new ParticleAnimator(this,findViewById(R.id.test),findViewById(R.id.image1));
    }

    public void pathAnimator(View view) {
//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view, "test");
//        startActivity(new Intent(this, Test.class), options.toBundle());
//        animator.startAnimator((i++) % 2 == 0);
//        Path path = new Path();
//        path.lineTo(300, 400);
//        path.lineTo(0, 800);
        Drawable drawable = ((ImageView)view).getDrawable();
        if(drawable instanceof Animatable)
            ((Animatable) drawable).start();
//        new PathAnimator(path, view).setDuration(1000).setRepeatCount(5).setRepeatMode(ValueAnimator.REVERSE).startAnimation();
    }

    int i = 0;

    public void circularAnimHide(View view) {
//        CutoverLoadingView  view1 = (CutoverLoadingView) view;
//        List<Integer> res  = new ArrayList<>();
//        res.add(R.mipmap.ic_launcher);
//        res.add(R.mipmap.test);
//        view1.setImageRes(res);
//        view1.startAnimator();
          ImageView imageView = (ImageView) view;
          i++;
          imageView.setImageResource(i%2==0?R.mipmap.test:R.mipmap.ic_launcher);
//        CircularAnim.hide(view).triggerView(findViewById(R.id.target)).go();
//        new AbsBitmapAnimator(this, view).startAnimator();
    }

    public void circularAnimShow(View view) {
        CircularAnim.show(findViewById(R.id.image1)).triggerView(view).go();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

    }

    public void hide(View view) {
        ImageView imageView = (ImageView) view;
        i++;
        imageView.setImageResource(i%2==0?R.mipmap.test:R.mipmap.ic_launcher);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }
}