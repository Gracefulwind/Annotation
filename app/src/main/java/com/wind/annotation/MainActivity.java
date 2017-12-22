package com.wind.annotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wind.simpleinject.SimpleInject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleInject.inject(this);
    }

    int count=0;
    //onClick事件注入
    @OnClick({R.id.h_btn_click1, R.id.h_btn_click2})
    public void testOnClickMethod(View v){
        switch (v.getId()){
            case R.id.h_btn_click1:
                tvResult1.setText("第" + count++ + "次点击");
                break;
            case R.id.h_btn_click2:
                tvResult2.setText("第" + count++ + "次点击");
                break;
            default:
                break;
        }
    }
}
