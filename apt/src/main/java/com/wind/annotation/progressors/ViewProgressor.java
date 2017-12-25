package com.wind.annotation.progressors;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wind.annotation.events.InjectActivity;
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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by Gracefulwind Wang on 2017/12/25.
 * Email : Gracefulwind@163.com
 */

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
        //由于InjectView和OnClick都是Activity的内部注解，所以这几个set也可以不返回
//        set.add(InjectView.class.getCanonicalName());
//        set.add(OnClick.class.getCanonicalName());
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
            TypeElement holderElement = (TypeElement) element;
//            elementUtils.
            //com.xx.yy
            String packageName = elementUtils.getPackageOf(holderElement).getQualifiedName().toString();
            //com.xx.yy.外部类名.内部类名
            String fullName = holderElement.getQualifiedName().toString();
            //
            String realNameWithPoint = fullName.replace(packageName, "");
            String realName = realNameWithPoint.substring(1, realNameWithPoint.length()).replace(".", "$");
            //对这个类生成注解类
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(realName + "$$Inject")
                .addModifiers(Modifier.PUBLIC);
//            ClassName androidView = ClassName.get("android.widget","LinearLayout");
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
                // 获取包名
//                PackageElement packageElement = elementUtils.getPackageOf(holderElement);
//                String packageName = packageElement.getQualifiedName().toString();
                // 创建文件
                JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
                javaFile.writeTo(processingEnv.getFiler());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

//    private void injectView(MethodSpec.Builder findViewMethodBuilder, Element fieldElement) {
//        //对注释了InjectView的做处理
//        InjectView injectViewAnnotation = fieldElement.getAnnotation(InjectView.class);
//        if (null != injectViewAnnotation) {
//            // 3. 获取resID
//            int redID = injectViewAnnotation.value();
//            findViewMethodBuilder.addStatement("activity.$L= ($T) activity.findViewById($L)",
//                fieldElement,
//                ClassName.get(fieldElement.asType()),
//                redID);
//        }
//    }

//    private void injectOnClick(MethodSpec.Builder onClickMethodBuilder, Element fieldElement) {
//        //只支持方法注解
//        ClassName androidView = ClassName.get("android.view","View");
//        if (fieldElement.getKind() != ElementKind.METHOD) {
//            return;
//        }
//        //转化成方法元素
//        ExecutableElement executableElement = (ExecutableElement) fieldElement;
//        //获取id
//        String methodName = executableElement.getSimpleName().toString();
//        //获取注解对象整体
//        OnClick onClickAnnotation = executableElement.getAnnotation(OnClick.class);
//        if(onClickAnnotation == null){
//            return;
//        }
//        int[] resIds = onClickAnnotation.value();
//        for(int resId : resIds){
//            //资源文件的值不会小于0
//            if (resId < 0) {
//                throw new IllegalArgumentException(
//                    String.format("value() in %s for field %s is not valid !", OnClick.class.getSimpleName(),
//                        executableElement.getSimpleName()));
////                continue;
//            }
//            //空格和\R\N只是为了格式好看，生成的代码理论上不给别人看，其实没必要加
//            onClickMethodBuilder.addStatement(
//                "activity.findViewById($L).setOnClickListener(new $T.OnClickListener(){ \r\n" +
//                    "@Override \r\n" +
//                    "public void onClick($T view){ \r\n" +
//                        "    activity.$N \r\n" +
//                        "} \r\n" +
//                    "})",
//                resId,
//                //是否可以全用view的具体子类？ --不可，因为id未必有field给他类型，所以类型是未知的。这里用基类View是最好的
//                androidView,
//                androidView,
//                //onClick方法：
//                methodName + "(view);"
//            );
//
//        }
//    }

}
