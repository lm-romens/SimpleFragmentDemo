package cn.edu.neusoft.simplefragmentdemo.multi.provider;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.neusoft.simplefragmentdemo.multi.modal.TextItem;
import me.drakeet.multitype.ItemViewProvider;

/**
 * @author liang mei
 * @create 2017/3/9
 * @description
 */
public class TextItemPrividerTest extends ItemViewProvider<TextItem,TextItemPrividerTest.MyViewHolder>{

    @NonNull
    @Override
    protected MyViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return null;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, @NonNull TextItem textItem) {

    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

}