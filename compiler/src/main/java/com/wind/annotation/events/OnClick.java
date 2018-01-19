package com.wind.annotation.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Gracefulwind Wang on 2017/12/21.
 * Email : Gracefulwind@163.com
 */
//@ListenerClass(
//        targetType = "android.view.View",
//        setter = "setOnClickListener",
//        type = "android.view.View.OnClickListener",
//        method = @ListenerMethod(
//                name = "onClick",
//                parameters = "android.view.View"
//        )
//)
// TYPE: 类或接口； FIELD: 成员变量； METHOD: 方法； ...
@Target(ElementType.METHOD)
// CLASS: 编译时； RUNTIME: 运行时
@Retention(RetentionPolicy.CLASS)
public @interface OnClick {
    int[] value();
}
