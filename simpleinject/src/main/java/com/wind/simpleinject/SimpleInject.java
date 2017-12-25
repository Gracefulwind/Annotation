package com.wind.simpleinject;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.view.Surface;
import android.view.View;
import android.widget.LinearLayout;

import com.squareup.javapoet.ClassName;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Gracefulwind Wang on 2017/12/21.
 * Email : Gracefulwind@163.com
 */

public class SimpleInject {

    final static HashMap<String, Method> METHODS = new HashMap<>();

//    public enum Finder {
//        VIEW {
//            @Override public View findOptionalView(Object source, int id) {
//                return ((View) source).findViewById(id);
//            }
//        },
//        ACTIVITY {
//            @Override public View findOptionalView(Object source, int id) {
//                return ((Activity) source).findViewById(id);
//            }
//        },
//        DIALOG {
//            @Override public View findOptionalView(Object source, int id) {
//                return ((Dialog) source).findViewById(id);
//            }
//        };


    public static<T extends Activity> void inject(T target){
        inject(target, target, target.getClass());
    }

    public static<S extends View, T extends Object> void inject(S source, T target){
        inject(source, target, target.getClass(), true);
    }

    private static void inject(Object source, Object target, Class<? extends Object> targetClass,boolean flag) {
        Method injector = findInjector(source, target);
        if(null == injector){
            return;
        }
        try {
            injector.invoke(null, source, target);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void inject(Object target, Object source, Class<? extends Activity> activityClass) {
        Method injector = findInjector(source, target);
        try {
            injector.invoke(null,source,target);
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
    private static Method findInjector(Object source, Object target) {
        Method injector;
        //减少反射，提高效率
        Class<?> targetClass = target.getClass();
        String targetClassName = targetClass.getName();
        injector = METHODS.get(targetClassName);
        if(null != injector){
            return injector;
        }
        try {
            Class<?> cls = Class.forName(targetClassName + "$$Inject");
            //View的注入必须要传递一个View
            if(source instanceof View){
                Class<?> viewClass = Class.forName("android.view.View");
                injector = cls.getDeclaredMethod("inject", viewClass, target.getClass());
            }else {
                injector = cls.getDeclaredMethod("inject", source.getClass(), target.getClass());
            }
            //添加到map中，加快下次读取
            METHODS.put(targetClassName, injector);
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
