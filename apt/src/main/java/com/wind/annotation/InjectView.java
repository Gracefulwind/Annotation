package com.wind.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by Gracefulwind Wang on 2017/12/20.
 * Email : Gracefulwind@163.com
 */

// TYPE: 类或接口； FIELD: 成员变量； METHOD: 方法； ...
@Target(ElementType.FIELD)
// CLASS: 编译时； RUNTIME: 运行时
@Retention(RetentionPolicy.CLASS)
public @interface InjectView {
    // 布局资源: R.layout.XXX
    //用value且只有唯一方法时可以省去value=
    int value();
}
