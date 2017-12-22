//package com.wind.annotation;
//
//import com.squareup.javapoet.ClassName;
//import com.squareup.javapoet.MethodSpec;
//
//import java.util.ArrayList;
//
//import javax.lang.model.element.TypeElement;
//import javax.lang.model.util.Elements;
//
///**
// * Created by Gracefulwind Wang on 2017/12/21.
// * Email : Gracefulwind@163.com
// */
//
//public class AnnotatedClass {
//    private static class TypeUtil {
//        //todo:待修改
//        static final ClassName BINDER = ClassName.get("process.gzoom.com.appapi", "ViewBinder");
//        static final ClassName PROVIDER = ClassName.get("process.gzoom.com.appapi", "ViewFinder");
//    }
//
//    /**类或者接口元素*/
//    private TypeElement mTypeElement;
//
//    /**绑定的view对象*/
//    private ArrayList<BindViewField> mFields;
//
//    /**辅助类，用于后文的文件输出*/
//    private Elements mElements;
//
//    /**绑定方法域*/
//    private ArrayList<ClickViewFIled> mClickFiled;
//
//
//    /**增加绑定方法域*/
//    void addClickField(ClickViewFIled fIled)
//    {
//        mClickFiled.add(fIled);
//    }
//
//    /**
//     * @param typeElement 注解所在的类或者接口
//     *
//     * @param elements 辅助类
//     * */
//    AnnotatedClass(TypeElement typeElement, Elements elements) {
//        mTypeElement = typeElement;
//        mElements = elements;
//        mFields = new ArrayList<>();
//        mClickFiled = new ArrayList<>();
//    }
//
//    void addField(BindViewField field) {
//        mFields.add(field);
//    }
//
//    JavaFile generateFile() {
//        //定义方法 bindbindView(final T host, Object object, ViewFinder finder);
//        MethodSpec.Builder bindViewMethod = MethodSpec.methodBuilder("bindView")
//                .addModifiers(Modifier.PUBLIC)
//                .addAnnotation(Override.class)
//                .addParameter(TypeName.get(mTypeElement.asType()), "host")
//                //后面我们需要使用源文件注册方法到控件中，因此这里需要final
//                .addParameter(TypeName.OBJECT, "source",Modifier.FINAL)
//                .addParameter(TypeUtil.PROVIDER, "finder");
//
//
//        for (BindViewField field : mFields) {
//            // find views
//            bindViewMethod.addStatement("host.$N = ($T)(finder.findView(source, $L))", field.getFieldName(), ClassName.get(field.getFieldType()), field.getResId());
//        }
//
//        ClassName androidView = ClassName.get("android.view","View");
//
//        //add clickFiled
//        if(mClickFiled!=null) {
//            for (ClickViewFIled fIled : mClickFiled) {
//                bindViewMethod.addStatement("finder.findView(source, $L).setOnClickListener(new $T.OnClickListener()" +
//                        " {" +
//                        "@Override " +
//                        "public void onClick($T view) " +
//                        "{ " +
//                        " (($T)source).$N " +
//                        "}" +
//                        "}" +
//                        ");", fIled.getResId(),androidView,androidView,TypeName.get(mTypeElement.asType()),fIled.getMethodName() + "();");
//            }//使用source直接调用方法
//        }
//        //类似的，这里生成unbind方法
//        MethodSpec.Builder unBindViewMethod = MethodSpec.methodBuilder("unBindView")
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(TypeName.get(mTypeElement.asType()), "host")
//                .addAnnotation(Override.class);
//        for (BindViewField field : mFields) {
//            unBindViewMethod.addStatement("host.$N = null", field.getFieldName());
//        }
//
//
//        //generaClass 生成类
//        TypeSpec injectClass = TypeSpec.classBuilder(mTypeElement.getSimpleName() + "$$ViewBinder")//类名字
//                .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.BINDER, TypeName.get(mTypeElement.asType())))//接口，首先是接口然后是范型
//                //再加入我们的目标方法
//                .addMethod(bindViewMethod.build())
//                .addMethod(unBindViewMethod.build())
//                .build();
//
//        String packageName = mElements.getPackageOf(mTypeElement).getQualifiedName().toString();
//        return JavaFile.builder(packageName, injectClass)
//                .build();
//    }
//}
