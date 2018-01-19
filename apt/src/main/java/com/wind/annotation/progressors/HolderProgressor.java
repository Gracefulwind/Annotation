package com.wind.annotation.progressors;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wind.annotation.events.InjectHolder;
import com.wind.annotation.events.InjectView;
import com.wind.annotation.events.OnClick;
import com.wind.annotation.utils.ProgressorUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by Gracefulwind Wang on 2017/12/25.
 * Email : Gracefulwind@163.com
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ViewProgressor extends AbstractProcessor {

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
        set.add(InjectHolder.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(InjectHolder.class);
        for (Element element : elements) {
            //没有Holder注解则继续循环
            InjectHolder injectActivityAnnotation = element.getAnnotation(InjectHolder.class);
            if (null == injectActivityAnnotation) {
                continue;
            }
            //生成类名
            TypeElement holderElement = (TypeElement) element;
            //这里获取的是包名：com.xx.yy
            String packageName = elementUtils.getPackageOf(holderElement).getQualifiedName().toString();
            //这里获取的是完整名：com.xx.yy.外部类名.内部类名
            String fullName = holderElement.getQualifiedName().toString();
            //处理，生成内部类名
            //1.将包名去掉
            String realNameWithPoint = fullName.replace(packageName, "");
            //2.将内部类的"."替换成"#"
            String realName = realNameWithPoint.substring(1, realNameWithPoint.length()).replace(".", "$");
            //在gradle打包时不能直接用R资源id的int值，所以需要在方法上加注解
            ClassName suppressWarnings = ClassName.get("java.lang", "SuppressWarnings");
            AnnotationSpec annoSpec = AnnotationSpec.builder(suppressWarnings)
                    .addMember("value", "\"ResourceType\"")
                    .build();
            //对这个类生成注解类
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(realName + "$$Inject")
                .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(annoSpec);
            typeSpecBuilder.addJavadoc("created by SimpleInject,do not modify it!!!\r\nPlease email me(429344332@qq.com) if any error raise.");
            ClassName androidView = ClassName.get("android.view","View");
            //inject方法
            MethodSpec injectSpec = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(androidView, "source")
                .addParameter(ClassName.get(holderElement), "target")
                .addStatement("findView(source, target)")
                .addStatement("onClick(source, target)")
                .build();
            typeSpecBuilder.addMethod(injectSpec);
            //遍历子元素，生成findView方法和onClick方法
            //生成findView方法
            MethodSpec.Builder findViewMethodBuilder = MethodSpec.methodBuilder("findView")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(androidView, "source")
                .addParameter(ClassName.get(holderElement), "target");
            //生成onClick方法
            MethodSpec.Builder onClickMethodBuilder = MethodSpec.methodBuilder("onClick")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(androidView, "source")
                //由于方法的调用是在接口实现类的内部，所以需要final
                .addParameter(ClassName.get(holderElement), "target", Modifier.FINAL);
            // 2. 获取单个类中，所有子Element
            List<? extends Element> members = elementUtils.getAllMembers(holderElement);
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
            //保存文件
            try {
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
