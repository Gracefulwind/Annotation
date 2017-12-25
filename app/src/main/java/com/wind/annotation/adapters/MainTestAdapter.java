package com.wind.annotation.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wind.annotation.R;
import com.wind.annotation.beans.ClickItemBean;
import com.wind.annotation.events.InjectHolder;
import com.wind.annotation.events.InjectView;
import com.wind.annotation.events.OnClick;
import com.wind.simpleinject.SimpleInject;

import java.util.List;

/**
 * Created by Gracefulwind Wang on 2017/12/25.
 * Email : Gracefulwind@163.com
 */

public class MainTestAdapter extends BaseAdapter {

    List<ClickItemBean> datas;
    Context context;

    public MainTestAdapter(Context context, List<ClickItemBean> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public ClickItemBean getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            convertView = View.inflate(context, R.layout.item_main_test_adapter, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            SimpleInject.inject(convertView, holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        ClickItemBean item = getItem(position);
        holder.tv1.setText(item.msg);
        String resource = "";
        switch (item.source){
            case 1:
                resource = "事件来自按钮1";
                break;
            case 2:
                resource = "事件来自按钮2";
                break;
            default:
                resource = "事件来源未知";
                break;
        }
        holder.tv2.setText(resource);
        return convertView;
    }

    @InjectHolder
    class ViewHolder{
        @InjectView(R.id.imta_tv_text1)
        TextView tv1;
        @InjectView(R.id.imta_tv_text2)
        TextView tv2;

        @OnClick({R.id.imta_tv_text1, R.id.imta_tv_text2})
        public void onClick(View v){
            String str = ((TextView) v).getText().toString();
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        }
    }
}
