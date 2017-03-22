package cn.edu.neusoft.simplefragmentdemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.edu.neusoft.simplefragmentdemo.fragment.DetialFragment;
import cn.edu.neusoft.simplefragmentdemo.R;

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
