package cn.edu.neusoft.simplefragmentdemo.multi;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import cn.edu.neusoft.simplefragmentdemo.multi.modal.Cell;
import cn.edu.neusoft.simplefragmentdemo.multi.modal.TextItem;
import cn.edu.neusoft.simplefragmentdemo.multi.provider.TextItemProvider;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import me.drakeet.multitype.MultiTypePool;

/**
 * @author liang mei
 * @create 2017/3/8
 * @description
 */

public class MultiTestActivity extends AppCompatActivity {
    private RecyclerView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListAdapter listAdapter;

    private final Items dataItems = new Items();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        swipeRefreshLayout = new SwipeRefreshLayout(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        content.addView(swipeRefreshLayout,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        listView = new RecyclerView(this);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 12);

        /* 关键内容：通过 setSpanSizeLookup 来告诉布局，你的 item 占几个横向单位，
           如果你横向有 5 个单位，而你返回当前 item 占用 5 个单位，那么它就会看起来单独占用一行 */
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return listAdapter.getSpanCount(position);
            }
        });
        listView.setLayoutManager(layoutManager);
        swipeRefreshLayout.addView(listView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(content);

        listAdapter = new ListAdapter(dataItems);
        listAdapter.applyGlobalMultiTypePool();
        listAdapter.registerAll(createTypePool());
    }

    private  MultiTypePool createTypePool(){
        MultiTypePool typePool=new MultiTypePool();
        typePool.register(TextItem.class,new TextItemProvider());
        return typePool;
    }

    class ListAdapter extends MultiTypeAdapter{

        public ListAdapter(@NonNull List<?> items) {
            super(items);
        }

        public int getSpanCount(int position) {
            Cell cell = (Cell) items.get(position);
            return cell.getSpanCount();
        }

    }

}
