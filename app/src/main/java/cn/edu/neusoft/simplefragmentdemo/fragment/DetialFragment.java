package cn.edu.neusoft.simplefragmentdemo.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PointerIconCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.neusoft.simplefragmentdemo.R;


public class DetialFragment extends Fragment {

    private ListView news_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detial, container, false);
        news_list = (ListView) view.findViewById(R.id.news_list);


//        SimpleAdapter adapter = new SimpleAdapter(getActivity(), getData(), R.layout.listview_item, new
//                String[]{"news_title", "news_info", "news_thumb"}, new int[]{R.id.news_title, R.id.news_info, R.id.news_thumb});
      MyAdapter adapter=new MyAdapter(getContext(),getData());
        news_list.setAdapter(adapter);
        return view;
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList< Map <String,Object> >();
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("new_title", "毡帽系列");
        map.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map.put("new_thumb", R.drawable.i1);
        list.add(map);
        map.put("new_title", "毡帽系列");
        map.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map.put("new_thumb", R.drawable.i2);
        list.add(map);
        map.put("new_title", "毡帽系列");
        map.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map.put("new_thumb", R.drawable.i3);
        list.add(map);
        map.put("new_title", "毡帽系列");
        map.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map.put("new_thumb", R.drawable.i4);
        list.add(map);
        map.put("new_title", "毡帽系列");
        map.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map.put("new_thumb", R.drawable.i5);
        list.add(map);
        map.put("new_title", "毡帽系列");
        map.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map.put("new_thumb", R.drawable.i6);
        list.add(map);
        return list;
    }

    /**
     * 自定义适配器写法  继承BaseAdapter  像上面用的系统自带适配器（SimpleAdapter）一般不用
     */

    public class MyAdapter extends BaseAdapter{
        private Context context;
        private List<Map<String, Object>> mapList=new ArrayList<>();

        public  MyAdapter(Context context, List<Map<String, Object>> list){
            this.context=context;
            this.mapList=list;
        }

        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return mapList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //将布局文件转化为View对象

            view =LayoutInflater.from(context).inflate(R.layout.listview_item,null);

            /**
             * 找到item布局文件中对应的控件
             */
            ImageView imageView = (ImageView) view.findViewById(R.id.news_thumb);
            TextView titleTextView = (TextView) view.findViewById(R.id.news_title);
            TextView contentTextView = (TextView) view.findViewById(R.id.news_info);
            Map<String, Object> map = getItem(i);

            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource((int) map.get("new_thumb"));

            titleTextView.setText(map.get("new_title").toString());
            titleTextView.setTextColor(0xff212121);
            titleTextView.setSingleLine();//单行
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);//动态设置字体及大小

            contentTextView.setText(map.get("new_info").toString());
            contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);

            return view;
        }
    }
}






