//package com.wind.annotation.progressors;
//
//import com.wind.annotation.events.InjectHolder;
//import com.wind.annotation.events.InjectView;
//import com.wind.annotation.events.OnClick;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.lang.annotation.ElementType;
//import java.util.HashSet;
//import java.util.Set;
//
//import javax.annotation.processing.AbstractProcessor;
//import javax.annotation.processing.ProcessingEnvironment;
//import javax.annotation.processing.RoundEnvironment;
//import javax.annotation.processing.SupportedSourceVersion;
//import javax.lang.model.SourceVersion;
//import javax.lang.model.element.Element;
//import javax.lang.model.element.TypeElement;
//import javax.lang.model.util.Elements;
//
///**
// * Created by Gracefulwind Wang on 2017/12/26.
// * Email : Gracefulwind@163.com
// */
//@SupportedSourceVersion(SourceVersion.RELEASE_7)
//public class TestProgressor extends AbstractProcessor {
//    /**
//     * 元素操作的辅助类
//     * */
//    Elements elementUtils;
//
//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//        // 元素操作的辅助类
//        elementUtils = processingEnv.getElementUtils();
//    }
//
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        HashSet<String> set = new HashSet<>();
//        //must be add if an annotation u want to be processed
//        set.add(OnClick.class.getCanonicalName());
//        set.add(InjectView.class.getCanonicalName());
//        return set;
//    }
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        File dir = new File("f://test");
//        if(!dir.exists()){
//            dir.mkdirs();
//        }
//        File file = new File(dir, "log.txt");
//        FileWriter fileWriter = null;
//        try {
//            fileWriter = new FileWriter(file, true);
//            //=====
//            fileWriter.append("=====start InjectHolder=====").append("\r\n");
//            fileWriter.flush();
//            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(InjectHolder.class);
//            for(Element element : elements){
//                String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
//                String fullName = ((TypeElement)element).getQualifiedName().toString();
//                fileWriter.append("element is " + element.getSimpleName()).append("\r\n")
//                        .append("    it's package : ").append(packageName).append("\r\n")
//                .flush();
//            }
//            fileWriter.append("=====start InjectView=====").append("\r\n");
//            elements = roundEnv.getElementsAnnotatedWith(InjectView.class);
//            for(Element element : elements){
//                String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
//                fileWriter.append("element is " + element.getSimpleName()).append("\r\n")
//                        .append("    it's package : ").append(packageName).append("\r\n")
//                        .flush();
//            }
//            fileWriter.append("=====start OnClick=====").append("\r\n");
//            elements = roundEnv.getElementsAnnotatedWith(OnClick.class);
//            for(Element element : elements){
//                String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
//                fileWriter.append("element is " + element.getSimpleName()).append("\r\n")
//                        .append("    it's package : ").append(packageName).append("\r\n")
//                        .flush();
//            }
//            //can't find somethings useful
//            //==root======
//            //====
//            Set<? extends Element> rootElements = roundEnv.getRootElements();
//            fileWriter.append("===========================================================").flush();
//            fileWriter.append("this is root elements (size is " + rootElements.size() + "):").flush();
//            for(Element element : rootElements){
//                String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
//                fileWriter.append("element is " + element.getSimpleName()).append("\r\n")
//                        .append("    it's package : ").append(packageName).append("\r\n")
//                        .append("-------")
//                        .flush();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if(null != fileWriter){
//                try {
//                    fileWriter.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return false;
//    }
//
//
//}
