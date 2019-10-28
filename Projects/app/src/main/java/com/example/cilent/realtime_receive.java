package com.example.cilent;

import android.os.Bundle;
import android.os.Message;

public class realtime_receive extends Thread{

    private Data_update data_update;
    private String str1;
    private String str2;
    private int i;
    private int j;

    public realtime_receive(Data_update data_update){
        this.data_update=data_update;
    }

    public void run(){
            str1 = "7";
            str2 = "6";
            while (true) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message message = Message.obtain();
                Bundle data = new Bundle();

                data.putInt("exp1", i);
                i = Integer.parseInt(str1);
                i += 1;
                if(i>30)i=7;
                str1 = String.valueOf(i);

                data.putInt("cl1", j);
                j = Integer.parseInt(str2);
                j += 1;
                str2 = String.valueOf(j);

                message.what = 111;

                message.setData(data);
                data_update.sendMessage(message);
            }
    }

}
