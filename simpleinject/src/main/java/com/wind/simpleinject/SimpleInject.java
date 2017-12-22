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

    final static HashMap<String, List<Method>> METHODS = new HashMap<>();

    public static<T extends Activity> void inject(T target){
        inject(target, target, target.getClass());
    }

    private static void inject(Object target, Object source, Class<? extends Activity> activityClass) {
        List<Method> injectors = findInjector(target.getClass());
        try {
            //todo:这里下次优化，把所有的方法和遍历invoke再封在一个方法里
            for(Method injector : injectors){
                injector.invoke(null,target);
            }
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
    private static List<Method> findInjector(Class target) {
        List<Method> injectors;
        //减少反射，提高效率
        String targetName = target.getName();
        injectors = METHODS.get(targetName);
        if(null != injectors){
            return injectors;
        }
        injectors = new ArrayList<>();
        try {
            Class<?> cls = Class.forName(targetName + "$$Inject");
            Method injector;
            //findView
            injector = cls.getDeclaredMethod("inject", target);
            injectors.add(injector);
            //添加到map中，加快下次读取
            METHODS.put(targetName, injectors);
            return injectors;
        //todo:如果出错应该全部不初始化还是初始化读到的部分？
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
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
