package com.example.dangerousproj_20191102;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Image_view extends Dialog implements View.OnClickListener {

    public Image_view(Context context) {
        super(context);
        setContentView(R.layout.view_dialog_advertisement);
        //设置点击布局外则Dialog消失
        setCanceledOnTouchOutside(true);
    }

    public void showDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        //设置弹窗位置
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
        show();
        findViewById(R.id.iv_advertisement).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}