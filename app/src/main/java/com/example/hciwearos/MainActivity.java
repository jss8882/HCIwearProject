package com.example.hciwearos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import processing.android.CompatUtils;
import processing.android.PFragment;
import processing.core.PApplet;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

//    public PApplet createSketch() {
//        PApplet sketch = new processing();
//
//        return sketch;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        FrameLayout frame = new FrameLayout(this);
//        frame.setId(CompatUtils.getUniqueViewId());
//        setContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
        PApplet sketch = new processing();
        PFragment fragment = new PFragment(sketch);
        View container = findViewById(R.id.container);
        fragment.setView(container, this);

    }
}