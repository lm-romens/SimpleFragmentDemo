package cn.edu.neusoft.simplefragmentdemo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.PointerIconCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.neusoft.simplefragmentdemo.cell.TestCell;

import static cn.edu.neusoft.simplefragmentdemo.R.layout.listview_item;


public class DetialFragment extends  Fragment {

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

        news_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if (position==0){
                    Toast.makeText(getActivity(),"tcghv n mlm",Toast.LENGTH_SHORT).show();
               // }
            }
        });
        return view;
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList< Map <String,Object> >();
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("new_title", "毡帽系列");
        map.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map.put("new_thumb", R.drawable.i1);
        list.add(map);
        Map<String, Object> map1 = new HashMap<String,Object>();
        map1.put("new_title", "毡帽系列");
        map1.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map1.put("new_thumb", R.drawable.i2);
        list.add(map1);
        Map<String, Object> map2 = new HashMap<String,Object>();
        map2.put("new_title", "毡帽系列");
        map2.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map2.put("new_thumb", R.drawable.i3);
        list.add(map2);
        Map<String, Object> map3 = new HashMap<String,Object>();
        map3.put("new_title", "毡帽系列");
        map3.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map3.put("new_thumb", R.drawable.i4);
        list.add(map3);
        Map<String, Object> map4 = new HashMap<String,Object>();
        map4.put("new_title", "毡帽系列");
        map4.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map4.put("new_thumb", R.drawable.i5);
        list.add(map4);
        Map<String, Object> map5 = new HashMap<String,Object>();
        map5.put("new_title", "毡帽系列");
        map5.put("new_info", "此系列服装有点cute，像不像小车夫。");
        map5.put("new_thumb", R.drawable.i6);
        list.add(map5);
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
        public int getItemViewType(int position) {
            if (position==0){
                return 0;
            }else if (position==1){
                return 1;
            }else {
                return 2;

            }
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

//                @Override
//        public int getViewTypeCount() {
//            return super.getViewTypeCount();
//        }

//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            return null;
//        }
//
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
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //将布局文件转化为View对象
            int type=getItemViewType(i);
            if (type==0){
                if (view==null){
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
                }
            }if (type==1){
                if (view==null){

                    view =LayoutInflater.from(context).inflate(R.layout.listview_item2,null);

                    /**
                     * 找到item布局文件中对应的控件
                     */
                    ImageView imageView = (ImageView) view.findViewById(R.id.news_thumb);
                    TextView titleTextView = (TextView) view.findViewById(R.id.news_title);
//                    TextView contentTextView = (TextView) view.findViewById(R.id.news_info);

                    Map<String, Object> map = getItem(i);

                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource((int) map.get("new_thumb"));

                    titleTextView.setText(map.get("new_title").toString());
                    titleTextView.setTextColor(0xff212121);
                    titleTextView.setSingleLine();//单行
                    titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);//动态设置字体及大小

//                    contentTextView.setText(map.get("new_info").toString());
//                    contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
                }
            }else if (type==2){
                if (view==null){
                    view=new TestCell(context);
                }
                Map<String, Object> map = getItem(i);
                TestCell cell=(TestCell)view;
                cell.setValue(map.get("new_title").toString());
            }


            return view;
        }
    }
}






