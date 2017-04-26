package com.wenzhiguo.consummate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Custom mCustom = (Custom) findViewById(R.id.custom);
        mCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustom.onStart();
            }
        });

    }
}
