package com.example.dangerousproj_20191102;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener{

    private ArrayList arr;
    private Data_update data_update;
    private Button SPRAY_BUTTON;
    private Button USER_DEFINED;
    private Button SET_ALARM_VALUE;
    private int COUNTS=5;// 点击次数
    final static long DURATION = 1500;// 规定有效时间
    long[] mHits = new long[COUNTS];
    public static boolean isSpraying;
    private EditText et_alarmOn_dataValue_low;
    private EditText et_alarmOn_timeValue_low;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        //避免进入应用自动弹出软键盘
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //喷淋按钮相关
        isSpraying=false;
        SPRAY_BUTTON = findViewById(R.id.SPRAY_BUTTON);
        SPRAY_BUTTON.setOnClickListener(this);



        SET_ALARM_VALUE= findViewById(R.id.SET_ALARM_VALUE);
        SET_ALARM_VALUE.setOnClickListener(this);

        //实时数据传递更新相关
        arr=HandObj();

        data_update= new Data_update(arr);
        // TODO Auto-generated method stub
        // 启动子线程

//        SharedPrefsUtil sp=new SharedPrefsUtil();
//        sp.putValue(content,"11","1");
        new RealTime_SendThread("192.168.2.1",10000,data_update).start();
        new Picture_sendThread("192.168.2.1",10000,data_update).start();
    }

    public ArrayList HandObj(){
        TextView EXP=this.findViewById(R.id.EXP);
        Button SPRAY_BUTTON =this.findViewById(R.id.SPRAY_BUTTON);
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

//        EditText et_alarmOn_dataValue_low=this.findViewById(R.id.et_alarmOn_dataValue_low);
//        EditText et_alarmOn_timeValue_low=this.findViewById(R.id.et_alarmOn_timeValue_low);
//        EditText et_alarmOn_dataValue_high=this.findViewById(R.id.et_alarmOn_dataValue_high);
//        EditText et_alarmOn_timeValue_high=this.findViewById(R.id.et_alarmOn_timeValue_high);
//        EditText et_alarmOff_dataValue_low=this.findViewById(R.id.et_alarmOff_dataValue_low);
//        EditText et_alarmOff_timeValue_low=this.findViewById(R.id.et_alarmOff_timeValue_low);
//        EditText et_alarmOff_dataValue_high=this.findViewById(R.id.et_alarmOff_dataValue_high);
//        EditText et_alarmOff_timeValue_high=this.findViewById(R.id.et_alarmOff_timeValue_high);

        ImageView imageView=this.findViewById(R.id.imageView);
        ImageView imageView2=this.findViewById(R.id.imageView2);


        ArrayList arr = new ArrayList();

        arr.add(DEVICE1);
        arr.add(DEVICE2);
        arr.add(DEVICE3);
        arr.add(DEVICE4);
        arr.add(DEVICE5);
        arr.add(DEVICE6);
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



        return arr;
    }

    @Override
    public void onClick(View v) {
        if(v==SPRAY_BUTTON) {
            continuousClick(COUNTS, DURATION);
        }else if(v==USER_DEFINED){
                user_define();
        }else if(v==SET_ALARM_VALUE){
                set_alarm_value();
        }
    }


    public void continuousClick(int count, long time) {
        //每次点击时，数组向前移动一位
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //为数组最后一位赋值
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            mHits = new long[COUNTS];//重新初始化数组
            if(isSpraying){
                new Spray("192.168.2.1", 10000,2, data_update,arr).start();
            }else{
                new Spray("192.168.2.1", 10000,1, data_update,arr).start();
            }
        }
    }

    public void user_define(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请选择喷淋按钮触发点击次数");
//        builder.setIcon(R.drawable.ic_launcher);
        builder.setSingleChoiceItems(new String[]{"2","3","4","5"}, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(which==0){COUNTS=2;}
                else if(which==1){COUNTS=3;}
                else if(which==2){COUNTS=4;}
                else if(which==3){COUNTS=5;}
                dialog.dismiss();
                System.out.println(COUNTS);

            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    public void set_alarm_value(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view= LayoutInflater.from(MainActivity.this).inflate(
                R.layout.set_alarm_value_dialog, null, false);
        builder.setTitle("请输入8个警示值");
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
        final EditText et7 = (EditText) view.findViewById(R.id.et_alarmOff_dataValue_high);
        final EditText et8 = (EditText) view.findViewById(R.id.et_alarmOff_timeValue_high);
        builder.setPositiveButton("确认" , new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean IS_SEND=true;
                int[] paras = new int[8];
                int input1=20,input2=20,input3=50,input4=50,input5=20,input6=20,input7=50,input8=50;
                if (et1.getText().toString() != null){
                    input1=Integer.parseInt(et1.getText().toString());
                    paras[0]=input1;
                }else{IS_SEND=false;}
                if(et2.getText().toString() != null){
                    input2=Integer.parseInt(et2.getText().toString());
                    paras[1]=input2;
                }else{IS_SEND=false;}
                if(et3.getText().toString() != null){
                    input3=Integer.parseInt(et3.getText().toString());
                    paras[2]=input3;
                }else{IS_SEND=false;}
                if(et4.getText().toString() != null){
                    input4=Integer.parseInt(et4.getText().toString());
                    paras[3]=input4;
                }else{IS_SEND=false;}
                if(et5.getText().toString() != null){
                    input5=Integer.parseInt(et5.getText().toString());
                    paras[4]=input5;
                }else{IS_SEND=false;}
                if(et6.getText().toString() != null){
                    input6=Integer.parseInt(et6.getText().toString());
                    paras[5]=input6;
                }else{IS_SEND=false;}
                if(et7.getText().toString() != null){
                    input7=Integer.parseInt(et7.getText().toString());
                    paras[6]=input7;
                }else{IS_SEND=false;}
                if(et8.getText().toString() != null){
                    input8=Integer.parseInt(et8.getText().toString());
                    paras[7]=input8;
                }else{IS_SEND=false;}

                if(IS_SEND) {
                    new Alarm_state("192.168.2.1", 10000, paras).start();
                }else{
                    commonDialog();
                    }
            }
        }).setNegativeButton("取消", null).show();
    }

    private void commonDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("警告！");// 设置标题
        // builder.setIcon(R.drawable.ic_launcher);//设置图标
        builder.setMessage("数据未填或未填完整，预警值设置失败！");// 为对话框设置内容
        // 为对话框设置取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
//                myToast("您点击了取消按钮");
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
//                myToast("您点击了确定按钮");
            }
        });
        builder.create().show();// 使用show()方法显示对话框
    }

//    private void myToast(String 您点击了确定按钮) {
//    }


}
