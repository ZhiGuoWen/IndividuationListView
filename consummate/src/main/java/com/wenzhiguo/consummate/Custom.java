package com.wenzhiguo.consummate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dell on 2017/4/25.
 * action :
 */

public class Custom extends View {

    private int width;
    private int height;
    private int raudis;
    private Paint paint;
    private Bitmap mBitmap;
    private int mBitmapHeight;
    private int mBitmapWidth;
    private long currcent = System.currentTimeMillis();
    private List<Integer> mList = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable runable = new Runnable() {
        @Override
        public void run() {
            //刷新视图
            invalidate();
            /*//修改半径值
            raudis=raudis+4;
            //判断是否画的半径值大于屏幕的一半,给画的半径重新赋值为图片的宽的一半
            if (raudis>width/2){
                raudis=mBitmapWidth/2;
            }*/
            //停格一段时间向集合添加数据
            if (System.currentTimeMillis()-currcent>600){
                mList.add(mBitmapWidth/2);
                //把当前时间重新赋值给currcent
                currcent=System.currentTimeMillis();
            }
            for (int i = 0; i < mList.size(); i++) {
                mList.set(i, mList.get(i) + 4);
            }
            //集合的迭代器
            Iterator<Integer> iterator = mList.iterator();
            while(iterator.hasNext()){
                Integer next = iterator.next();
                if (next>=width/2){
                    if (mList.contains(next)){
                        iterator.remove();
                    }
                }
            }
            //利用Handler延迟发送重新调用子线程
            handler.postDelayed(runable, 100);
        }
    };

    public Custom(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        //用工厂加载图片的bitmap对象
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.touxiang);
        //画笔
        paint = new Paint();
        //画笔的颜色
        paint.setColor(Color.parseColor("#155c7c"));
        //实心圆
        paint.setStyle(Paint.Style.FILL);
        //图片的宽和高
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();
        //图片的宽的半径
        raudis = mBitmapWidth / 2;
        //给集合添加当前的图片的半径
        mList.add(raudis);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        height = canvas.getHeight();
        width = canvas.getWidth();
        int left = (width - mBitmapWidth) / 2;
        int top = (height - mBitmapHeight) / 2;
        //先画出波纹,在画出图片
        for (int i = 0; i < mList.size(); i++) {
            int r = mList.get(i);
            paint.setAlpha(177-177*(r-mBitmapWidth/2)/((width-mBitmapWidth)/2));
            canvas.drawCircle(width / 2, height / 2, r, paint);
        }

        canvas.drawBitmap(mBitmap, left, top, null);
    }

    public void onStart() {
        //启动画图增加半径值
        handler.post(runable);
    }

}
