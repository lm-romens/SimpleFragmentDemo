package cn.edu.neusoft.simplefragmentdemo.weex;

import android.app.Application;

import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;

/**
 * @author liang mei
 * @create 2017/2/14
 * @description
 */

public class WXApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        InitConfig config=new InitConfig.Builder().setImgAdapter(new ImageAdapter()).build();
        WXSDKEngine.initialize(this,config);
    }
}
