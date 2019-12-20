package com.example.dangerousproj_20191102;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class Data_update extends Handler implements View.OnClickListener {

    private ArrayList arr;

    private Button SWITCH_PATTERN;
    private Boolean HOW_SHOWING=true;
    private TextView HEAD_EXP;
    private TextView TAIL_EXP;


    public Data_update(ArrayList arr) {
        this.arr=arr;
    }

    @Override
    public void handleMessage(android.os.Message msg) {  //这个是发送过来的消息

        Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据

        TextView T = (TextView) arr.get(6);
        TextView H = (TextView) arr.get(7);
        TextView WIND_DIRECT = (TextView) arr.get(8);
        TextView WIND_SPEED = (TextView) arr.get(9);
        TextView GPS_LATITUDE = (TextView) arr.get(10);
        TextView GPS_LONGITUDE = (TextView) arr.get(11);
        TextView GPS_SPEED = (TextView) arr.get(12);
        TextView GPS_DIRECT = (TextView) arr.get(13);
        TextView TANK_PRESSURE = (TextView) arr.get(14);
        TextView TANK_LEVEL = (TextView) arr.get(15);

        ImageView imageView = (ImageView) arr.get(16);
        ImageView imageView2 = (ImageView) arr.get(17);
        TextView EXP=(TextView)arr.get(18);
        Button SPRAY_BUTTON = (Button)arr.get(19);

        HEAD_EXP=(TextView)arr.get(20);
        TAIL_EXP=(TextView)arr.get(21);
        TextView ANGLE_WIND=(TextView)arr.get(22);
        SWITCH_PATTERN=(Button) arr.get(23);
        SWITCH_PATTERN.setOnClickListener(this);

        if (msg.what == 01) {
            int[] alarmArray = bundle.getIntArray("alarm");
            double[] dataArray = bundle.getDoubleArray("data");
            boolean isData_realtime = bundle.getBoolean("isData_realtime");
            if(isData_realtime) {
                T.setText(Double.toString(dataArray[24]));
                H.setText(Double.toString(dataArray[25]));

                if((0<=dataArray[26]&&dataArray[26]<90)||dataArray[26]==360){
                    WIND_DIRECT.setText("北偏东");
                    if(dataArray[26]==360){
                        ANGLE_WIND.setText("0度");
                    }else{
                        ANGLE_WIND.setText(Double.toString(dataArray[26])+"度");
                    }
                }else if(90<=dataArray[26]&&dataArray[26]<180){
                    WIND_DIRECT.setText("南偏东");
                    ANGLE_WIND.setText(Double.toString(180.0-dataArray[26])+"度");
                }else if(180<=dataArray[26]&&dataArray[26]<270){
                    WIND_DIRECT.setText("南偏西");
                    ANGLE_WIND.setText(Double.toString(dataArray[26]-180)+"度");
                }else if(270<=dataArray[26]&&dataArray[26]<360){
                    WIND_DIRECT.setText("北偏西");
                    ANGLE_WIND.setText(Double.toString(360-dataArray[26])+"度");
                }



                WIND_SPEED.setText(Double.toString(dataArray[27]));

                if(dataArray[28]>0){
                    GPS_LATITUDE.setText(Double.toString(dataArray[28])+"N");
                }else{
                    GPS_LATITUDE.setText(Double.toString(-dataArray[28])+"S");
                }
                if(dataArray[29]>0){
                    GPS_LONGITUDE.setText(Double.toString(dataArray[29])+"E");
                }else{
                    GPS_LONGITUDE.setText(Double.toString(-dataArray[29])+"W");
                }


                GPS_SPEED.setText(Double.toString(dataArray[30]));
                GPS_DIRECT.setText(Double.toString(dataArray[31]));
                TANK_PRESSURE.setText(Double.toString(dataArray[32]));
                TANK_LEVEL.setText(Double.toString(dataArray[33]));


                for (int j = 0; j < 24; j += 6) {
                    if (dataArray[j] != -1) {
                        switch (j) {
                            case 0:
                                // 此处设置气体名称为：可燃气体
                                if(HOW_SHOWING) EXP.setText("可燃气体相对浓度(%)");
                                else EXP.setText("可燃气体绝对浓度(%)");
                                break;
                            case 6:
                                // 此处设置气体名称为：氯气x
                                if(HOW_SHOWING) EXP.setText("氯气相对浓度(%)");
                                else EXP.setText("氯气绝对浓度(%)");
                                break;
                            case 12:
                                // 此处设置气体名称为：氨气
                                if(HOW_SHOWING) EXP.setText("氨气相对浓度(%)");
                                else EXP.setText("氨气绝对浓度(%)");
                                break;
                            case 18:
                                // 此处设置气体名称为：环氧乙烷
                                if(HOW_SHOWING) EXP.setText("环氧乙烷相对浓度(%)");
                                else EXP.setText("环氧乙烷绝对浓度(%)");
                                break;
                        }
                        for (int k = 0; k <  6; k++) {

                            // 此处设置device j为：dataArray[j];
                            System.out.println("第" + (k - j + 1) + "个设备的值为" + dataArray[k+j]);
                           //SWITCH_PATTERN为true显示相对浓度,false显示绝对浓度
                            if(HOW_SHOWING){
                                ((TextView)arr.get(k-j)).setText(Double.toString(dataArray[k+j]));
                            }else {
                                ((TextView)arr.get(k-j)).setText(Double.toString(dataArray[k+j]*0.05));
                            }
//                            //注意，图形监测中车头车尾的数据来自第一行设备监测值的第三第四个
                                HEAD_EXP.setText(Double.toString(dataArray[j+2]));
                                TAIL_EXP.setText(Double.toString(dataArray[j+3]));
                        }
                        break;
                    }
                }
            } else {
                for(int i = 0; i<6; i++){
                    if(alarmArray[i] == 1){
                        ((TextView)arr.get(i)).setTextColor(Color.parseColor("#FFB90F"));
                    } else if(alarmArray[i] == 2){
                        ((TextView)arr.get(i)).setTextColor(Color.parseColor("#FF0000"));
                    }
                }

            }
        }
        if(msg.what==02){
            String base64_1=bundle.getString("base64_1");
            String base64_2=bundle.getString("base64_2");

            byte[] decodedString= Base64.decode(base64_1,Base64.DEFAULT);
            Bitmap decodedByte= BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
            imageView.setImageBitmap(decodedByte);

            byte[] decodedString2= Base64.decode(base64_2,Base64.DEFAULT);
            Bitmap decodedByte2= BitmapFactory.decodeByteArray(decodedString2,0,decodedString2.length);
            imageView2.setImageBitmap(decodedByte2);
        }
        if(msg.what==03){
            int sprayPara=bundle.getInt("spray");
            if (sprayPara == 1){
                System.out.println("已经开始喷淋！");
                MainActivity.isSpraying = true;
                SPRAY_BUTTON.setText("正在喷淋");
                SPRAY_BUTTON.setBackgroundColor(Color.parseColor("#FF4EA9EF"));

            } else if (sprayPara == 2) {
                System.out.println("已经停止喷淋！");
                MainActivity.isSpraying = false;
                SPRAY_BUTTON.setText("点击开始喷淋");
                SPRAY_BUTTON.setBackgroundColor(Color.parseColor("#FFB2D6F3"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v==SWITCH_PATTERN){
            if(HOW_SHOWING){
                HOW_SHOWING=false;
            }else{
                HOW_SHOWING=true;
            }
        }
    }
}