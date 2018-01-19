package com.wind.annotation.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.wind.annotation.events.InjectView;
import com.wind.annotation.events.OnClick;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

/**
 * Created by Gracefulwind Wang on 2017/12/25.
 * Email : Gracefulwind@163.com
 */

public class ProgressorUtils {
    public static void injectView(MethodSpec.Builder findViewMethodBuilder, Element fieldElement) {
        //对注释了InjectView的做处理
        InjectView injectViewAnnotation = fieldElement.getAnnotation(InjectView.class);
        if (null != injectViewAnnotation) {
            // 3. 获取resID
            int redID = injectViewAnnotation.value();
            findViewMethodBuilder.addStatement("target.$L= ($T) source.findViewById($L)",
                fieldElement,
                ClassName.get(fieldElement.asType()),
                redID);
        }
    }

    public static void injectOnClick(MethodSpec.Builder onClickMethodBuilder, Element fieldElement) {
        //只支持方法注解
        ClassName androidView = ClassName.get("android.view","View");
        if (fieldElement.getKind() != ElementKind.METHOD) {
            return;
        }
        //转化成方法元素
        ExecutableElement executableElement = (ExecutableElement) fieldElement;
        //获取id
        String methodName = executableElement.getSimpleName().toString();
        //获取注解对象整体
        OnClick onClickAnnotation = executableElement.getAnnotation(OnClick.class);
        if(onClickAnnotation == null){
            return;
        }
        int[] resIds = onClickAnnotation.value();
        for(int resId : resIds){
            //资源文件的值不会小于0
            if (resId < 0) {
                throw new IllegalArgumentException(
                    String.format("value() in %s for field %s is not valid !", OnClick.class.getSimpleName(),
                        executableElement.getSimpleName()));
//                continue;
            }
            //空格和\R\N只是为了格式好看，生成的代码理论上不给别人看，其实没必要加
            onClickMethodBuilder.addStatement(
                "source.findViewById($L).setOnClickListener(new $T.OnClickListener(){ \r\n" +
                    "@Override \r\n" +
                    "public void onClick($T view){ \r\n" +
                    "    target.$N \r\n" +
                    "} \r\n" +
                "})",
                resId,
                //是否可以全用view的具体子类？ --不可，因为id未必有field给他类型，所以类型是未知的。这里用基类View是最好的
                androidView,
                androidView,
                //onClick方法：
                methodName + "(view);"
            );

        }
    }
}
