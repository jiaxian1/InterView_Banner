package edu.feicui.interview_banner;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewPagerAdapter adapter;
    private List<ImageView> imageViews;//存放图片的集合
    private TextView tittle;//图片的文字描述
    private List<View> dots;//存放小点的集合
    private int currentItem;//当前位置
    private int oldPosition;//记录上一个点的位置
    private int[] imageIds=new int[]{//图片的id
            R.mipmap.fun_share_friends,
            R.mipmap.fun_share_weibo,
            R.mipmap.fun_share_weixin
    };
    private String[] tittles=new String[]{//标题
            "送君者",
            "皆",
            "自捱而返"
    };
    private ScheduledExecutorService scheduledExecutorService;//线程池，用来定时轮播

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mViewPager= (ViewPager) findViewById(R.id.viewpager);
        //显示图片的集合
        imageViews=new ArrayList<>();
        for (int i = 0; i <imageIds.length ; i++) {
            //初始化ImageView
            ImageView imageView=new ImageView(this);
            imageView.setBackgroundResource(imageIds[i]);
            imageViews.add(imageView);
        }

        //显示小店的集合
        dots=new ArrayList<>();
        dots.add(findViewById(R.id.dot1));
        dots.add(findViewById(R.id.dot2));
        dots.add(findViewById(R.id.dot3));

        //显示标题
        tittle= (TextView) findViewById(R.id.tv_tittle);
        tittle.setText(tittles[0]);

        //初始化适配器 并绑定
        adapter=new ViewPagerAdapter();
        mViewPager.setAdapter(adapter);
        //当滑动时候的监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //标题的改变
                tittle.setText(tittles[position]);
                //小点的改变
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_default);
                oldPosition=position;
                currentItem=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启一个单个后台线程
        scheduledExecutorService= Executors.newSingleThreadScheduledExecutor();
        //给线程添加一个定时的任务调度（延迟initialDelay时间后开始执行command，
        // 并且按照period时间周期性重复调用(周期事件包括command运行事件，
        // 如果周期时间比command运行时间短，则command运行完毕后，立即重复运行)）
        scheduledExecutorService.scheduleWithFixedDelay(
                new ViewPagerTask(),
                2,
                2,
                TimeUnit.SECONDS
        );
    }
    private class ViewPagerTask implements Runnable{

        @Override
        public void run() {
            //用取余的方式来确定当前item
            currentItem=(currentItem+1)%imageIds.length;
            mHandler.sendEmptyMessage(0);//只是为了调动handler执行UI更新
        }
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //更新viewpager当前显示的pager
            mViewPager.setCurrentItem(currentItem);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (scheduledExecutorService!=null){
            scheduledExecutorService.shutdown();
            scheduledExecutorService=null;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter{

        /**
         * 获取当前窗体界面的数量
         * @return
         */
        @Override
        public int getCount() {
            return imageViews==null?0:imageViews.size();
        }
        /**
         * 是否由对象生成界面
         * @param view
         * @param object
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        /**
         * return一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放入ViewPager中
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViews.get(position));
            return imageViews.get(position);
        }

        /**
         * 从ViewPagerz中移除当前View
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position));
        }
    }
}
