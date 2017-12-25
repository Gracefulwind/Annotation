package com.wind.annotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wind.annotation.adapters.MainTestAdapter;
import com.wind.annotation.beans.ClickItemBean;
import com.wind.annotation.events.InjectActivity;
import com.wind.annotation.events.InjectView;
import com.wind.annotation.events.OnClick;
import com.wind.simpleinject.SimpleInject;

import java.util.ArrayList;
import java.util.List;

//setContent方法用注入
@InjectActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    //findView注入
    @InjectView(R.id.h_btn_click1)
    Button btnClick;
    @InjectView(R.id.h_btn_click2)
    Button btnClick2;
    @InjectView(R.id.h_tv_result)
    TextView tvResult1;
    @InjectView(R.id.h_tv_result2)
    TextView tvResult2;
    @InjectView(R.id.h_et_input1)
    EditText etInput;
    @InjectView(R.id.am_lv_list)
    ListView lvList;

    List<ClickItemBean> data = new ArrayList<>();
    private MainTestAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleInject.inject(this);
        initData();
    }

    private void initData() {
        data.clear();
        mAdapter = new MainTestAdapter(getBaseContext(), data);
        lvList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    int count=0;
    //onClick事件注入
    @OnClick({R.id.h_btn_click1, R.id.h_btn_click2})
    public void testOnClickMethod(View v){
        String msg = "";
        ClickItemBean item = null;
        switch (v.getId()){
            case R.id.h_btn_click1:
                msg = "第" + count++ + "次点击";
                tvResult1.setText(msg);
                item = new ClickItemBean();
                item.msg = msg;
                item.source = 1;
                data.add(item);
                break;
            case R.id.h_btn_click2:
                msg = "第" + count++ + "次点击";
                tvResult2.setText(msg);
                item = new ClickItemBean();
                item.msg = msg;
                item.source = 2;
                data.add(item);
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }
}
