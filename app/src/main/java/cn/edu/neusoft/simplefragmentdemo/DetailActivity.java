package cn.edu.neusoft.simplefragmentdemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author liang mei
 * @create 2017/2/9
 * @description
 */

public class DetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //在Activity里放置Fragment；
        DetialFragment fragment=new DetialFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_detail,fragment).commit();

    }
}
