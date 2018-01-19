package com.wind.annotation.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Gracefulwind Wang on 2018/1/19.
 * Email : Gracefulwind@163.com
 */

/*
    其实现在InjectFragment和InjectHolder完全一样，Fragment就是个Holder。。。
    考虑后续把onCreateView的view的生成也一起绑了。
    感觉绑的意义不大。得单独为这个inject方法设置返回值。不统一。
 */

@Target(ElementType.TYPE)
// CLASS: 编译时； RUNTIME: 运行时
@Retention(RetentionPolicy.CLASS)
public @interface InjectFragment {
    int value() default -1;
}
