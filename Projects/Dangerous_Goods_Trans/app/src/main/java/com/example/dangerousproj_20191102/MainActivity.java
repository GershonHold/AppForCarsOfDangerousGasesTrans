package com.example.dangerousproj_20191102;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.CollationElementIterator;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {

    private ArrayList arr;
    private Data_update data_update;
    private Button SPRAY_BUTTON;
    private Button SET_ALARM_VALUE;
    private Button IMAGE_VIEW;
    private Button SWITCH_PATTERN;
    private TextView HEAD_EXP;
    private TextView TAIL_EXP;
    private int COUNTS = 2;// 点击次数
    final static long DURATION = 1500;// 规定有效时间
    long[] mHits = new long[COUNTS];
    public static boolean isSpraying;
    private Image_view imageview;
    private ImageView imageView;
    private ImageView imageView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //喷淋按钮相关
        isSpraying = false;
        SPRAY_BUTTON = findViewById(R.id.SPRAY_BUTTON);
        SPRAY_BUTTON.setOnClickListener(this);
        SET_ALARM_VALUE = findViewById(R.id.SET_ALARM_VALUE);
        SET_ALARM_VALUE.setOnClickListener(this);
        IMAGE_VIEW = findViewById(R.id.IMAGE_VIEW);
        IMAGE_VIEW.setOnClickListener(this);
        SWITCH_PATTERN = findViewById(R.id.SWITCH_PATTERN);

        //传递相关控件
        arr = HandObj();
        data_update = new Data_update(arr);
        // TODO Auto-generated method stub
        // 启动子线程

//        SharedPrefsUtil sp=new SharedPrefsUtil();
//        sp.putValue(content,"11","1");
        new RealTime_SendThread("192.168.2.1", 10000, data_update).start();
        new Picture_sendThread("192.168.2.1", 10000, data_update).start();
    }

    public ArrayList HandObj() {
        TextView EXP = this.findViewById(R.id.EXP);
        Button SPRAY_BUTTON = this.findViewById(R.id.SPRAY_BUTTON);
        TextView DEVICE1 = this.findViewById(R.id.DEVICE1);
        TextView DEVICE2 = this.findViewById(R.id.DEVICE2);
        TextView DEVICE3 = this.findViewById(R.id.DEVICE3);
        TextView DEVICE4 = this.findViewById(R.id.DEVICE4);
        TextView DEVICE5 = this.findViewById(R.id.DEVICE5);
        TextView DEVICE6 = this.findViewById(R.id.DEVICE6);
        TextView T = this.findViewById(R.id.T);
        TextView H = this.findViewById(R.id.H);
        TextView WIND_DIRECT = this.findViewById(R.id.WIND_DIRECT);
        TextView WIND_SPEED = this.findViewById(R.id.WIND_SPEED);
        TextView GPS_LATITUDE = this.findViewById(R.id.GPS_LATITUDE);
        TextView GPS_LONGITUDE = this.findViewById(R.id.GPS_LONGITUDE);
        TextView GPS_SPEED = this.findViewById(R.id.GPS_SPEED);
        TextView GPS_DIRECT = this.findViewById(R.id.GPS_DIRECT);
        TextView TANK_PRESSURE = this.findViewById(R.id.TANK_PRESSURE);
        TextView TANK_LEVEL = this.findViewById(R.id.TANK_LEVEL);

        imageView = this.findViewById(R.id.imageView);
        imageView2 = this.findViewById(R.id.imageView2);


//        TextView SPARED_DEVICE1=this.findViewById(R.id.SPARED_DEVICE1);
//        TextView SPARED_DEVICE2=this.findViewById(R.id.SPARED_DEVICE2);
//        TextView SPARED_DEVICE3=this.findViewById(R.id.SPARED_DEVICE3);
//        TextView SPARED_DEVICE4=this.findViewById(R.id.SPARED_DEVICE4);
//        TextView SPARED_DEVICE5=this.findViewById(R.id.SPARED_DEVICE5);
//        TextView SPARED_DEVICE6=this.findViewById(R.id.SPARED_DEVICE6);
//        TextView SPARED_DEVICE7=this.findViewById(R.id.SPARED_DEVICE7);
//        TextView SPARED_DEVICE8=this.findViewById(R.id.SPARED_DEVICE8);
//        TextView SPARED_DEVICE9=this.findViewById(R.id.SPARED_DEVICE9);

        TextView ANGLE_WIND = this.findViewById(R.id.ANGLE_WIND);
        SWITCH_PATTERN = this.findViewById(R.id.SWITCH_PATTERN);

        imageview = new Image_view(this);
        HEAD_EXP = imageview.findViewById(R.id.HEAD_EXP);
        TAIL_EXP = imageview.findViewById(R.id.TAIL_EXP);

        ArrayList arr = new ArrayList();

        arr.add(DEVICE1);
        arr.add(DEVICE2);
        arr.add(DEVICE3);
        arr.add(DEVICE4);
        arr.add(DEVICE5);
        arr.add(DEVICE6);

//        arr.add(SPARED_DEVICE1);
//        arr.add(SPARED_DEVICE2);
//        arr.add(SPARED_DEVICE3);
//        arr.add(SPARED_DEVICE4);
//        arr.add(SPARED_DEVICE5);
//        arr.add(SPARED_DEVICE6);
//        arr.add(SPARED_DEVICE7);
//        arr.add(SPARED_DEVICE8);
//        arr.add(SPARED_DEVICE9);

        arr.add(T);
        arr.add(H);
        arr.add(WIND_DIRECT);
        arr.add(WIND_SPEED);
        arr.add(GPS_LATITUDE);
        arr.add(GPS_LONGITUDE);
        arr.add(GPS_SPEED);
        arr.add(GPS_DIRECT);
        arr.add(TANK_PRESSURE);
        arr.add(TANK_LEVEL);   //在ArrayList中的编号为15

        arr.add(imageView);
        arr.add(imageView2);

        arr.add(EXP);
        arr.add(SPRAY_BUTTON);

        arr.add(HEAD_EXP);
        arr.add(TAIL_EXP);

        arr.add(ANGLE_WIND);

        arr.add(SWITCH_PATTERN);

        return arr;
    }

    @Override
    public void onClick(View v) {
        if (v == SPRAY_BUTTON) {
            continuousClick(COUNTS, DURATION);
        } else if (v == SET_ALARM_VALUE) {
            set_alarm_value();
        } else if (v == IMAGE_VIEW) {

            imageview.showDialog();
//            View view = LayoutInflater.from(MainActivity.this).inflate(
//                    R.layout.view_dialog_advertisement, null, false);
        }
    }

    public void continuousClick(int count, long time) {
        //每次点击时，数组向前移动一位
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //为数组最后一位赋值
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            mHits = new long[COUNTS];//重新初始化数组
            if (isSpraying) {
                new Spray("192.168.2.1", 10000, 2, data_update, arr).start();
            } else {
                new Spray("192.168.2.1", 10000, 1, data_update, arr).start();
            }
        }
    }

    public void set_alarm_value() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.set_alarm_value_dialog, null, false);
        builder.setTitle("请输入8个预警值");
        builder.setIcon(R.drawable.ic_launcher_background);
        builder.setView(view);
        final Dialog dialog = builder.create();
        // 初始化控件，注意这里是通过view.findViewById
        final EditText et1 = view.findViewById(R.id.et_alarmOn_dataValue_low);
        final EditText et2 = view.findViewById(R.id.et_alarmOn_timeValue_low);
        final EditText et3 = view.findViewById(R.id.et_alarmOn_dataValue_high);
        final EditText et4 = view.findViewById(R.id.et_alarmOn_timeValue_high);
        final EditText et5 = view.findViewById(R.id.et_alarmOff_dataValue_low);
        final EditText et6 = view.findViewById(R.id.et_alarmOff_timeValue_low);
        final EditText et7 = view.findViewById(R.id.et_alarmOff_dataValue_high);
        final EditText et8 = view.findViewById(R.id.et_alarmOff_timeValue_high);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean IS_SEND = true;
                int[] paras = new int[8];
                int input1 = 20, input2 = 20, input3 = 50, input4 = 50, input5 = 20, input6 = 20, input7 = 50, input8 = 50;
                if (!((et1.getText().toString()).equals(""))) {
                    input1 = Integer.parseInt(et1.getText().toString());
                    paras[0] = input1;
                } else {
                    IS_SEND = false;
                }
                if (!((et2.getText().toString()).equals(""))) {
                    input2 = Integer.parseInt(et2.getText().toString());
                    paras[1] = input2;
                } else {
                    IS_SEND = false;
                }
                if (!((et3.getText().toString()).equals(""))) {
                    input3 = Integer.parseInt(et3.getText().toString());
                    paras[2] = input3;
                } else {
                    IS_SEND = false;
                }
                if (!((et4.getText().toString().equals("")))) {
                    input4 = Integer.parseInt(et4.getText().toString());
                    paras[3] = input4;
                } else {
                    IS_SEND = false;
                }
                if (!((et5.getText().toString()).equals(""))) {
                    input5 = Integer.parseInt(et5.getText().toString());
                    paras[4] = input5;
                } else {
                    IS_SEND = false;
                }
                if (!((et6.getText().toString()).equals(""))) {
                    input6 = Integer.parseInt(et6.getText().toString());
                    paras[5] = input6;
                } else {
                    IS_SEND = false;
                }
                if (!((et7.getText().toString()).equals(""))) {
                    input7 = Integer.parseInt(et7.getText().toString());
                    paras[6] = input7;
                } else {
                    IS_SEND = false;
                }
                if (!((et8.getText().toString()).equals(""))) {
                    input8 = Integer.parseInt(et8.getText().toString());
                    paras[7] = input8;
                } else {
                    IS_SEND = false;
                }

                if (IS_SEND) {
                    new Alarm_state("192.168.2.1", 10000, paras).start();
                } else {
                    commonDialog();
                }
            }
        }).setNegativeButton("取消", null).show();
    }

    private void commonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("警告！");
        builder.setMessage("预警值信息未填或未填完整，预警值设置失败！");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        builder.create().show();
    }

    public void Limit_Image() {
        imageView.setMaxWidth(1200);
        imageView2.setMaxHeight(600);
    }
}