package com.wind.annotation;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

import java.lang.annotation.Annotation;
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
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getBaseContext();
        SimpleInject.inject(this);
        initData();
        //在填充前只有这种方式能获取到根布局id
        ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0).getId();
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
//                Dialog dialog = new Dialog(mContext);
//                dialog.setTitle("test");
//                View.inflate(mContext, )
//                dialog.addContentView();
                break;
            case R.id.h_btn_click2:
                add2();
                tryGetClass();
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("ResourceType")
    private void tryGetClass() {
//        try {
//            Class<?> injectView = Class.forName("com.wind.annotation.events.InjectView");
//            System.out.println(injectView);
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        try {
            Class<?> clazz = Class.forName("SuppressWarnings");
            boolean annotation = clazz.isAnnotation();
            System.out.println("is anno: " + annotation);
            Annotation[] annotations = clazz.getAnnotations();
            System.out.println("get Annos:");
            int index = 0;
            for(Annotation a : annotations){
                index++;
                System.out.println("index is " + index + " , anno is :" + a);
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void add2() {
        String msg;
        ClickItemBean item;
        msg = "第" + count++ + "次点击";
        tvResult2.setText(msg);
        item = new ClickItemBean();
        item.msg = msg;
        item.source = 2;
        data.add(item);
    }
}
