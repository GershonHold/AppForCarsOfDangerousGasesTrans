package com.example.dangerousproj_20191102;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class Data_update extends Handler {

    private ArrayList arr;


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

        if (msg.what == 01) {
            int[] alarmArray = bundle.getIntArray("alarm");
            double[] dataArray = bundle.getDoubleArray("data");
            boolean isData_realtime = bundle.getBoolean("isData_realtime");
            if(isData_realtime) {
                T.setText(Double.toString(dataArray[24]));
                H.setText(Double.toString(dataArray[25]));
                WIND_DIRECT.setText(Double.toString(dataArray[26]));
                WIND_SPEED.setText(Double.toString(dataArray[27]));
                GPS_LATITUDE.setText(Double.toString(dataArray[28]));
                GPS_LONGITUDE.setText(Double.toString(dataArray[29]));
                GPS_SPEED.setText(Double.toString(dataArray[30]));
                GPS_DIRECT.setText(Double.toString(dataArray[31]));
                TANK_PRESSURE.setText(Double.toString(dataArray[32]));
                TANK_LEVEL.setText(Double.toString(dataArray[33]));

                for (int j = 0; j < 24; j += 6) {
                    if (dataArray[j] != -1) {
                        switch (j) {
                            case 0:
                                // 此处设置气体名称为：可燃气体
                                EXP.setText("可燃气体浓度");
                                break;
                            case 6:
                                // 此处设置气体名称为：氯气
                                EXP.setText("氯气浓度");
                                break;
                            case 12:
                                // 此处设置气体名称为：氨气
                                EXP.setText("氨气浓度");

                                break;
                            case 18:
                                // 此处设置气体名称为：环氧乙烷
                                EXP.setText("环氧乙烷浓度");
                                System.out.println("环氧乙烷");
                                break;
                        }
                        for (int k = j; k < j + 6; k++) {
                            // 此处设置device j为：dataArray[j];
                            System.out.println("第" + (k - j + 1) + "个设备的值为" + dataArray[k]);
                            ((TextView)arr.get(k-j)).setText(Double.toString(dataArray[k]));
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
        if(msg.what==04){

        }
    }

}