package com.lhl.animaton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.lhl.animation.CircularAnim;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        new Handler(Looper.getMainLooper()).postDelayed(()->{
//            CircularAnim.hide(getWindow().getDecorView()).go(() -> {
//                finish();
//            });
//        },3000);
    }
}