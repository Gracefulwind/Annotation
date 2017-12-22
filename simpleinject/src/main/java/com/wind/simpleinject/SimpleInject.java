package com.wind.simpleinject;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gracefulwind Wang on 2017/12/21.
 * Email : Gracefulwind@163.com
 */

public class SimpleInject {

    final static HashMap<String, Method> METHODS = new HashMap<>();

    public static<T extends Activity> void inject(T target){
        inject(target, target, target.getClass());
    }

    private static void inject(Object target, Object source, Class<? extends Activity> activityClass) {
        Method injector = findInjector(target.getClass());
        try {
            injector.invoke(null,target);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * 通过类名获取其findView和onClick方法
     *
     * */
    private static Method findInjector(Class target) {
        Method injector;
        //减少反射，提高效率
        String targetName = target.getName();
        injector = METHODS.get(targetName);
        if(null != injector){
            return injector;
        }
        try {
            Class<?> cls = Class.forName(targetName + "$$Inject");
            //findView
            injector = cls.getDeclaredMethod("inject", target);
            //添加到map中，加快下次读取
            METHODS.put(targetName, injector);
            return injector;
        //todo:如果出错应该全部不初始化还是初始化读到的部分？
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void init(Activity activity){
//        String localClassName = activity.getLocalClassName();
////        System.out.println(localClassName);
//        Class<?> activityClass = null;
////        InjectMainActivity.bind(activity);
//        try {
//            activityClass = Class.forName("Inject" + localClassName);
//            Method init = activityClass.getDeclaredMethod("init", );
//            init.invoke(null,activity);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }
}
