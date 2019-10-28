package com.example.cilent;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener{

    private ArrayList arr;
    private Data_update data_update;
    private ImageView imageView;
    private ImageView imageView2;
    private String base64String;
    private String base64String2;
    private Button SPRAY_BUTTON;
    private Button USER_DEFINED;
    final static int COUNTS = 5;// 点击次数
    final static long DURATION = 1000;// 规定有效时间
    long[] mHits = new long[COUNTS];
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //避免进入应用自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //喷淋按钮相关
        flag=false;
        SPRAY_BUTTON = (Button) findViewById(R.id.SPRAY_BUTTON);
        SPRAY_BUTTON.setOnClickListener(this);

        USER_DEFINED= (Button) findViewById(R.id.USER_DEFINED);
        USER_DEFINED.setOnClickListener(this);

        //实时数据传递更新相关
        arr=HandObj();
        data_update= new Data_update(arr);
        // TODO Auto-generated method stub
        // 启动子线程
        new Thread(new realtime_require()).start();
        new Thread(new realtime_receive(data_update)).start();

    }



    public ArrayList HandObj(){
        TextView DEVICE1=this.findViewById(R.id.DEVICE1);
        TextView DEVICE2=this.findViewById(R.id.DEVICE2);
        TextView DEVICE3=this.findViewById(R.id.DEVICE3);
        TextView DEVICE4=this.findViewById(R.id.DEVICE4);
        TextView DEVICE5=this.findViewById(R.id.DEVICE5);
        TextView DEVICE6=this.findViewById(R.id.DEVICE6);
        TextView T=this.findViewById(R.id.T);
        TextView H=this.findViewById(R.id.H);
        TextView WIND_DIRECT=this.findViewById(R.id.WIND_DIRECT);
        TextView WIND_SPEED=this.findViewById(R.id.WIND_SPEED);
        TextView GPS_LATITUDE=this.findViewById(R.id.GPS_LATITUDE);
        TextView GPS_LONGITUDE=this.findViewById(R.id.GPS_LONGITUDE);
        TextView GPS_SPEED=this.findViewById(R.id.GPS_SPEED);
        TextView GPS_DIRECT=this.findViewById(R.id.GPS_DIRECT);
        TextView TANK_PRESSURE=this.findViewById(R.id.TANK_PRESSURE);
        TextView TANK_LEVEL=this.findViewById(R.id.TANK_LEVEL);

        TextView et_alarmOn_dataValue_low=this.findViewById(R.id.et_alarmOn_dataValue_low);
        TextView et_alarmOn_timeValue_low=this.findViewById(R.id.et_alarmOn_timeValue_low);
        TextView et_alarmOn_dataValue_high=this.findViewById(R.id.et_alarmOn_dataValue_high);
        TextView et_alarmOn_timeValue_high=this.findViewById(R.id.et_alarmOn_timeValue_high);
        TextView et_alarmOff_dataValue_low=this.findViewById(R.id.et_alarmOff_dataValue_low);
        TextView et_alarmOff_timeValue_low=this.findViewById(R.id.et_alarmOff_timeValue_low);
        TextView et_alarmOff_dataValue_high=this.findViewById(R.id.et_alarmOff_dataValue_high);
        TextView et_alarmOff_timeValue_high=this.findViewById(R.id.et_alarmOff_timeValue_high);

        imageView=this.findViewById(R.id.imageView);
        imageView2=this.findViewById(R.id.imageView2);

        ArrayList arr = new ArrayList();
        arr.add(DEVICE1);
        arr.add(DEVICE2);
        arr.add(DEVICE3);
        arr.add(DEVICE4);
        arr.add(DEVICE5);
        arr.add(DEVICE6);
        arr.add(T);
        arr.add(H);
        arr.add(imageView);
        arr.add(imageView2);
        arr.add(base64String);
        arr.add(WIND_DIRECT);
        arr.add(WIND_SPEED);
        arr.add(GPS_LATITUDE);
        arr.add(GPS_LONGITUDE);
        arr.add(GPS_SPEED);
        arr.add(GPS_DIRECT);
        arr.add(TANK_PRESSURE);
        arr.add(TANK_LEVEL);

        arr.add(et_alarmOn_dataValue_low);
        arr.add(et_alarmOn_timeValue_low);
        arr.add(et_alarmOn_dataValue_high);
        arr.add(et_alarmOn_timeValue_high);
        arr.add(et_alarmOff_dataValue_low);
        arr.add(et_alarmOff_timeValue_low);
        arr.add(et_alarmOff_dataValue_high);
        arr.add(et_alarmOff_timeValue_high);



        return arr;
    }

    @Override
    public void onClick(View v) {
        continuousClick(COUNTS, DURATION);
    }


    private void continuousClick(int count, long time) {
        //每次点击时，数组向前移动一位
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //为数组最后一位赋值
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            mHits = new long[COUNTS];//重新初始化数组
            System.out.println("点击了5次");
            if(flag){
                SPRAY_BUTTON.setBackgroundColor(Color.parseColor("#FFB2D6F3"));
                flag=false;
            }else{
                SPRAY_BUTTON.setBackgroundColor(Color.parseColor("#FF4EA9EF"));
                flag=true;
            }
        }
    }

}
