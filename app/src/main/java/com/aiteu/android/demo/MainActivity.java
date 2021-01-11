package com.aiteu.android.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aiteu.android.xtablayout.R;
import com.aiteu.android.xtablayout.XTabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final XTabLayout tabView = findViewById(R.id.tab_view);
        tabView.setTitles(new String[]{"TAB1", "TAB2", "TAB3", "TAB4"});
    }
}
