package com.mood.lucky.goodmood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mood.lucky.goodmood.R;
import com.mood.lucky.goodmood.model.BashModel;

import java.util.List;

/**
 * Created by lucky on 09.10.2017.
 */

public class BashAdapter extends BaseAdapter{

    private List<BashModel> objects;
    private Context context;
    private LayoutInflater inflater;

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v==null){
            v=inflater.inflate(R.layout.bash_item,viewGroup,false);
        }
        BashModel model = getModel(i);

        ((TextView) v.findViewById(R.id.bush_item_post)).setText(model.getDesc());
        ((TextView) v.findViewById(R.id.bush_site)).setText(model.getSite());

        return v;
    }
    public BashModel getModel(int position){
        return ((BashModel) getItem(position));
    }

}
