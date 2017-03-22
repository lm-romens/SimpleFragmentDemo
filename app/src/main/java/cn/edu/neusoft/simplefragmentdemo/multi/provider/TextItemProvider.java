package cn.edu.neusoft.simplefragmentdemo.multi.provider;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.neusoft.simplefragmentdemo.multi.MultiTypeHolder;
import cn.edu.neusoft.simplefragmentdemo.multi.modal.TextItem;
import me.drakeet.multitype.ItemViewProvider;

/**
 * @author liang mei
 * @create 2017/3/8
 * @description
 */

public class TextItemProvider extends ItemViewProvider<TextItem,MultiTypeHolder> {


    @NonNull
    @Override
    protected MultiTypeHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {

        return null;
    }

    @Override
    protected void onBindViewHolder(@NonNull MultiTypeHolder holder, @NonNull TextItem textItem) {

    }
}
