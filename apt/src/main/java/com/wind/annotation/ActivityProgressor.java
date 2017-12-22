package com.wind.annotation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

/**
 * Created by Gracefulwind Wang on 2017/12/20.
 * Email : Gracefulwind@163.com
 */
//@SupportedAnnotationTypes的值为当前类支持的注解的完整类路径，支持通配符
//@SupportedAnnotationTypes("com.wind.annotation.InjectView")
//@SupportedSourceVersion(SourceVersion.RELEASE_7)
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
            TypeElement activityElement = (TypeElement) element;
            //对这个类生成注解类
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(activityElement.getSimpleName() + "$$Inject")
                    .addModifiers(Modifier.PUBLIC);
            //给类setContentView
            InjectActivity injectActivityAnnotation = element.getAnnotation(InjectActivity.class);
            if(null == injectActivityAnnotation) {
                continue;
            }
            int value = injectActivityAnnotation.value();
            MethodSpec injectSpec = MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(activityElement), "activity")
                    .addStatement("setContentView(activity)"
                    ).addStatement("findView(activity)"
                    ).addStatement("onClick(activity)"
                    )
                    .build();
            typeSpecBuilder.addMethod(injectSpec);
            MethodSpec setContentViewSpec = MethodSpec.methodBuilder("setContentView")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(activityElement), "activity")
                    .addStatement("activity.setContentView($L)",
                            value
                            )
                    .build();
            typeSpecBuilder.addMethod(setContentViewSpec);
            //生成findView方法
            MethodSpec.Builder findViewMethodBuilder = MethodSpec.methodBuilder("findView")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(activityElement), "activity");
            //生成onClick方法
            MethodSpec.Builder onClickMethodBuilder = MethodSpec.methodBuilder("onClick")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    //由于activity的调用是在接口实现类的内部，所以需要final
                    .addParameter(ClassName.get(activityElement), "activity", Modifier.FINAL);
            // 2. 获取单个类中，所有子Element
            List<? extends Element> members = elementUtils.getAllMembers(activityElement);
            for (Element fieldElement : members) {
                //findView只处理绑定了 *InjectView注解* 的 *成员量*
                if(fieldElement.getKind() == ElementKind.FIELD && null != fieldElement.getAnnotation(InjectView.class)){
                    injectView(findViewMethodBuilder, fieldElement);
                }
                //findView只处理绑定了 *OnClick注解* 的 *成员方法*
                if(fieldElement.getKind() == ElementKind.METHOD && null != fieldElement.getAnnotation(OnClick.class)){
                    injectOnClick(onClickMethodBuilder, fieldElement);
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

    private FileWriter getLog(Set<? extends Element> elements) {
        File dir = new File("f://apt_test");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "log.txt");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.append("elements.size=" + elements.size()).append("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileWriter;
    }

    private void injectOnClick(MethodSpec.Builder onClickMethodBuilder, Element fieldElement) {
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
            onClickMethodBuilder.addStatement("activity.findViewById($L).setOnClickListener(new $T.OnClickListener(){ \r\n" +
                                    "@Override \r\n" +
                                    "public void onClick($T view){ \r\n" +
                                     "    activity.$N \r\n" +
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

    private void injectView(MethodSpec.Builder findViewMethodBuilder, Element fieldElement) {
        //对注释了InjectView的做处理
        InjectView injectViewAnnotation = fieldElement.getAnnotation(InjectView.class);
        if (null != injectViewAnnotation) {
            // 3. 获取resID
            int redID = injectViewAnnotation.value();
            findViewMethodBuilder.addStatement("activity.$L= ($T) activity.findViewById($L)",
                        fieldElement,
                        ClassName.get(fieldElement.asType()),
                        redID);
        }
    }

    /**
     *
     * 泓洋大神的学习代码
     *
     * */
    private void oldMethod(RoundEnvironment roundEnv) {
        //        if (set != null && !set.isEmpty()) {
//            generateJavaClassFile(set, roundEnvironment);
//            return true;
//        }
        //do someThings
        // 获得被该注解声明的元素
        Set<? extends Element> elememts = roundEnv
                .getElementsAnnotatedWith(InjectView.class);
        // 声明类元素
        TypeElement classElement = null;
        // 声明一个存放成员变量的列表
        List<VariableElement> fields = null;
        // 存放二者
        Map<String, List<VariableElement>> maps = new HashMap<String, List<VariableElement>>();
        // 遍历
        for (Element ele : elememts)
        {
            // 判断该元素是否为类
            if (ele.getKind() == ElementKind.CLASS)
            {
                classElement = (TypeElement) ele;
                maps.put(classElement.getQualifiedName().toString(),
                        fields = new ArrayList<VariableElement>());
            // 判断该元素是否为成员变量
            } else if (ele.getKind() == ElementKind.FIELD){
                VariableElement varELe = (VariableElement) ele;
                // 获取该元素封装类型
                TypeElement enclosingElement = (TypeElement) varELe
                        .getEnclosingElement();
                // 拿到key
                String key = enclosingElement.getQualifiedName().toString();
                fields = maps.get(key);
                if (fields == null)
                {
                    maps.put(key, fields = new ArrayList<VariableElement>());
                }
                fields.add(varELe);
            }
        }

        for (String key : maps.keySet())
        {
            if (maps.get(key).size() == 0)
            {
                TypeElement typeElement = elementUtils.getTypeElement(key);
                List<? extends Element> allMembers = elementUtils
                        .getAllMembers(typeElement);
                if (allMembers.size() > 0)
                {
                    maps.get(key).addAll(ElementFilter.fieldsIn(allMembers));
                }
            }
        }

        generateCodes(maps);
    }

    private void generateCodes(Map<String, List<VariableElement>> maps) {
        File dir = new File("f://apt_test");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 遍历map
        for (String key : maps.keySet()) {
            // 创建文件
            File file = new File(dir, key.replaceAll("\\.", "_") + ".txt");
            try {
                /**
                 * 编写json文件内容
                 */
                FileWriter fw = new FileWriter(file);
                fw.append("{").append("class:").append("\"" + key + "\"")
                        .append(",\n ");
                fw.append("fields:\n {\n");
                List<VariableElement> fields = maps.get(key);

                for (int i = 0; i < fields.size(); i++)
                {
                    VariableElement field = fields.get(i);
                    fw.append("  ").append(field.getSimpleName()).append(":")
                            .append("\"" + field.asType().toString() + "\"");
                    if (i < fields.size() - 1)
                    {
                        fw.append(",");
                        fw.append("\n");
                    }
                }
                fw.append("\n }\n");
                fw.append("}");
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
