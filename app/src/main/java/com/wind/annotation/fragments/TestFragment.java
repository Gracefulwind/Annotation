package com.wind.annotation.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.wind.annotation.R;
import com.wind.annotation.events.InjectFragment;
import com.wind.annotation.events.InjectHolder;
import com.wind.annotation.events.InjectView;
import com.wind.annotation.events.OnClick;
import com.wind.simpleinject.SimpleInject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Gracefulwind Wang on 2018/1/19.
 * Email : Gracefulwind@163.com
 */

@InjectFragment
public class TestFragment extends Fragment {

    private View rootView;

    @InjectView(R.id.imta_ll_contain)
    LinearLayout root;
    @InjectView(R.id.imta_tv_text1)
    TextView tv1;
    @InjectView(R.id.imta_tv_text2)
    TextView tv2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = View.inflate(getActivity(), R.layout.item_main_test_adapter, null);
        SimpleInject.inject(rootView, this);
        return rootView;
    }

    @OnClick({R.id.imta_tv_text1, R.id.imta_tv_text2})
    public void test(View v){

    }
}
