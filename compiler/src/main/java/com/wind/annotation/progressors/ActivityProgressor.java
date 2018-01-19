package com.wind.annotation.progressors;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wind.annotation.events.InjectActivity;
import com.wind.annotation.events.InjectView;
import com.wind.annotation.events.OnClick;
import com.wind.annotation.utils.ProgressorUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

/**
 * Created by Gracefulwind Wang on 2017/12/20.
 * Email : Gracefulwind@163.com
 */
//@SupportedAnnotationTypes的值为当前类支持的注解的完整类路径，支持通配符
//@SupportedAnnotationTypes("com.wind.annotation.annotations.InjectView")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ActivityProgressor extends AbstractProcessor {

    /**
     * 元素操作的辅助类
     * */
    Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        // 元素操作的辅助类
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
        //只处理InjectActivity相关注解
        set.add(InjectActivity.class.getCanonicalName());
        //由于InjectView和OnClick都是Activity的内部注解，所以这几个set也可以不返回
//        set.add(InjectView.class.getCanonicalName());
//        set.add(OnClick.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 1. 获取所有注解了TargetClass的Element
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(InjectActivity.class);
        for (Element element : elements) {
            InjectActivity injectActivityAnnotation = element.getAnnotation(InjectActivity.class);
            if(null == injectActivityAnnotation) {
                continue;
            }
            TypeElement activityElement = (TypeElement) element;
//            elementUtils.
            //在gradle打包时不能直接用R资源id的int值，所以需要在方法上加注解
            ClassName suppressWarnings = ClassName.get("java.lang", "SuppressWarnings");
            AnnotationSpec annoSpec = AnnotationSpec.builder(suppressWarnings)
                    .addMember("value", "\"ResourceType\"")
                    .build();
            //对这个类生成注解类
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(activityElement.getSimpleName() + "$$Inject")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(annoSpec);
            typeSpecBuilder.addJavadoc("created by SimpleInject,do not modify it!!!\r\nPlease email me(429344332@qq.com) if any error raise.");
            //给类setContentView
            int value = injectActivityAnnotation.value();
            MethodSpec injectSpec = MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(activityElement), "source")
                    .addParameter(ClassName.get(activityElement), "target")
                    .addStatement("setContentView(source)")
                    .addStatement("findView(source, target)")
                    .addStatement("onClick(source, target)")
                    .build();
            typeSpecBuilder.addMethod(injectSpec);
            MethodSpec setContentViewSpec = MethodSpec.methodBuilder("setContentView")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(activityElement), "source")
                    .addStatement("source.setContentView($L)",
                        value)
                    .build();
            typeSpecBuilder.addMethod(setContentViewSpec);
//            try {
//                Class.forName("SuppressWarnings");
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }

            //生成findView方法
            MethodSpec.Builder findViewMethodBuilder = MethodSpec.methodBuilder("findView")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(activityElement), "source")
                    .addParameter(ClassName.get(activityElement), "target");
            //生成onClick方法
            MethodSpec.Builder onClickMethodBuilder = MethodSpec.methodBuilder("onClick")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(activityElement), "source")
                    //由于activity的调用是在接口实现类的内部，所以需要final
                    .addParameter(ClassName.get(activityElement), "target", Modifier.FINAL);
            // 2. 获取单个类中，所有子Element
            List<? extends Element> members = elementUtils.getAllMembers(activityElement);
            for (Element fieldElement : members) {
                //findView只处理绑定了 *InjectView注解* 的 *成员量*
                if(fieldElement.getKind() == ElementKind.FIELD && null != fieldElement.getAnnotation(InjectView.class)){
                    ProgressorUtils.injectView(findViewMethodBuilder, fieldElement);
                }
                //findView只处理绑定了 *OnClick注解* 的 *成员方法*
                if(fieldElement.getKind() == ElementKind.METHOD && null != fieldElement.getAnnotation(OnClick.class)){
                    ProgressorUtils.injectOnClick(onClickMethodBuilder, fieldElement);
                }
            }
            MethodSpec findViewSpec = findViewMethodBuilder.build();
            MethodSpec onClickSpec = onClickMethodBuilder.build();
            //addAllFindView
            typeSpecBuilder.addMethod(findViewSpec);
            typeSpecBuilder.addMethod(onClickSpec);
            //将注解类生成
            TypeSpec typeSpec = typeSpecBuilder.build();
            try {
                // 获取包名
                PackageElement packageElement = elementUtils.getPackageOf(activityElement);
                String packageName = packageElement.getQualifiedName().toString();
                // 创建文件
                JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
                javaFile.writeTo(processingEnv.getFiler());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
