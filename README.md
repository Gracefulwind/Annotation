# Annotation  
自定义编译期注解的Demo，在运行期不会造成性能损耗。目前支持Activity、Fragment和ViewHolder的绑定findView和绑定onCLick，以及activity的setContentView  

具体的使用例子在sample中。

在module中的使用：  
dependencies {  
        ...  
        compile 'com.gracefulwind:wdinject:1.1.4'  
        annotationProcessor 'com.gracefulwind:wdinject-compiler:1.1.4'  
}  

关于编译期注解的原理，会在之后更新学习笔记说明。  

# License  
Apache v2.0 licensed

