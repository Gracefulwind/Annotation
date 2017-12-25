package com.wind.annotation.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Gracefulwind Wang on 2017/12/25.
 * Email : Gracefulwind@163.com
 */

@Target(ElementType.TYPE)
// CLASS: 编译时； RUNTIME: 运行时
@Retention(RetentionPolicy.CLASS)
public @interface InjectHolder {

}
