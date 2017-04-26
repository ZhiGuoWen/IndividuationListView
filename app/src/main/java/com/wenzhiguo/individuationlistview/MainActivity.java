package com.wenzhiguo.individuationlistview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Custom.OnPositionChangedListener {

    private Custom mCustom;
    private List<String> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //控件
        mCustom = (Custom) findViewById(R.id.list);
        //数据
        initData();
        //适配器
        mCustom.setAdapter(new myAdapter());
        mCustom.setCacheColorHint(Color.TRANSPARENT);
        mCustom.setOnPositionChangedListener(this);
    }

    private void initData() {
        mList = new ArrayList<>();
        for (int i = 1; i < 101; i++) {
            mList.add(""+i);
        }
    }

    @Override
    public void onPostioinChanged(Custom custom, int position, View scrollBarPanel) {
        //((TextView)scrollBarPanel).setText(""+position);
    }

    class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = View.inflate(MainActivity.this,R.layout.item,null);
            TextView text = (TextView) view.findViewById(android.R.id.text1);
            text.setText(mList.get(i));
            return view;
        }
    }


}
