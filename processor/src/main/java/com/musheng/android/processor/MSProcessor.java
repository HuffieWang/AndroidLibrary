package com.musheng.android.processor;

import com.google.auto.service.AutoService;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class MSProcessor extends AbstractProcessor {

    /** 调整为你的项目路径和包名 **/
    public static final String ROOT_DIR = "E:\\Android\\Project\\HippoV2\\";
    public static final String ROOT_PACKAGE = "com.hippo.app";
    public static final String CONFIG_CLASS = "com.hippo.app.config.ServerConfig";

    /** 以下不用改 **/
    public static final String JAVA_DIR = ROOT_DIR + "app\\src\\main\\java\\";
    public static final String LAYOUT_DIR = ROOT_DIR + "app\\src\\main\\res\\layout\\";
    public static final String ENTITY_PACKAGE = ROOT_PACKAGE + ".entity";
    public static final String ADAPTER_PACKAGE = ROOT_PACKAGE + ".adapter";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(MSContract.class.getCanonicalName());
        annotations.add(MSEntity.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null || set.isEmpty()) {
            return true;
        }
        Set<? extends Element> contracts = roundEnvironment.getElementsAnnotatedWith(MSContract.class);
        new ContractProcessor().process(contracts);


        Set<? extends Element> entities = roundEnvironment.getElementsAnnotatedWith(MSEntity.class);
        new EntityProcessor().process(entities);

        Set<? extends Element> adapters = roundEnvironment.getElementsAnnotatedWith(MSAdapter.class);
        new AdapterProcessor().process(adapters);
        return true;
    }

    public static boolean isNotTextEmpty(String text){
        return text != null && !text.equals("");
    }

    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static <T> T[] arrayConcat(T[] a, T[] b) {
        final int alen = a.length;
        final int blen = b.length;
        if (alen == 0) {
            return b;
        }
        if (blen == 0) {
            return a;
        }
        final T[] result = (T[]) java.lang.reflect.Array.
                newInstance(a.getClass().getComponentType(), alen + blen);
        System.arraycopy(a, 0, result, 0, alen);
        System.arraycopy(b, 0, result, alen, blen);
        return result;
    }


    public static  String createPackage(String packageName){
        return "package " + packageName + ";\n";
    }

    public static  String createImport(String content){
        return "import " + content + ";\n";
    }

    public static  String createClass(String padding, String type, String className, String extend, String content, String[] iml){
        StringBuilder builder = new StringBuilder();
        builder.append("\n").append(padding).append(type).append(" ").append(className);
        if(isNotTextEmpty(extend)){
            builder.append(" extends ").append(extend);
        }
        if(iml != null && iml.length > 0){
            builder.append(" implements ");
            for(int i = 0; i < iml.length; i++){
                builder.append(iml[i]);
                if (i+1 != iml.length){
                    builder.append(", ");
                }
            }
        }
        builder.append("{\n\n");
        if(isNotTextEmpty(content)){
            builder.append(content);
        }
        builder.append(padding).append("}\n\n");
        return builder.toString();
    }

    public static  String createParams(String padding, String[] params){
        return createParams(padding, params, false);
    }

    public static  String createParams(String padding, String[] params, boolean containAnnotation) {
        StringBuilder builder = new StringBuilder();
        if(params != null && params.length > 0){
            for (String param : params) {
                String type;
                String value;
                if (param.contains("$")) {
                    if(param.contains("[")) {
                        type = "List<" + param.substring(param.indexOf("$") + 1, param.indexOf("[")) + ">";
                        value = param.substring(0, param.indexOf("$"));
                    } else {
                        type = param.substring(param.indexOf("$") + 1);
                        value = param.substring(0, param.indexOf("$"));
                    }
                } else {
                    type = "String";
                    value = param;
                }
                String[] valueSplit = value.split("#");
                if(valueSplit.length > 1){
                    if(containAnnotation){
                        for(int i = 1; i < valueSplit.length; i++){
                            builder.append(padding).append(valueSplit[i]).append("\n");
                        }
                    }
                }
                value = valueSplit[0];
                builder.append(padding).append("private ").append(type).append(" ").append(value).append(";\n");
            }
        }
        builder.append("\n");
        return builder.toString();
    }


    public static  String createConstructor(String padding, String constructorName, String[] params, String content){
        StringBuilder builder = new StringBuilder();
        builder.append(padding).append("public ").append(constructorName).append("(");

        StringBuilder input = new StringBuilder();
        StringBuilder copy = new StringBuilder();
        if(params != null && params.length > 0){
            for(int i = 0; i < params.length; i++){
                String param = params[i];
                String type;
                String value;
                if (param.contains("$")){
                    if(param.contains("[")){
                        type = "List<" + param.substring(param.indexOf("$") + 1, param.indexOf("[")) + ">";
                        value = param.substring(0, param.indexOf("$"));
                    } else {
                        type = param.substring(param.indexOf("$") + 1);
                        value = param.substring(0, param.indexOf("$"));
                    }
                } else {
                    type = "String";
                    value = param;
                }
                input.append(type).append(" ").append(value);
                copy.append(padding).append("    this.").append(value).append(" = ").append(value).append(";");
                if(i+1 != params.length){
                    input.append(",");
                    copy.append("\n");
                }
            }
        }

        builder.append(input.toString()).append(") {\n");
        if(isNotTextEmpty(content)){
            builder.append(content).append("\n");
        }
        builder.append(copy.toString()).append("\n");
        builder.append(padding).append("}\n\n");
        return builder.toString();
    }


    public static  String createGetterAndSetter(String padding, String[] params){
        StringBuilder builder = new StringBuilder();
        if(params != null && params.length > 0){
            for (String param : params) {
                param = param.split("#")[0];

                String type;
                String value;
                if (param.contains("$")) {
                    if(param.contains("[")){
                        type = "List<" + param.substring(param.indexOf("$") + 1, param.indexOf("[")) + ">";
                        value = param.substring(0, param.indexOf("$"));
                    } else {
                        type = param.substring(param.indexOf("$") + 1);
                        value = param.substring(0, param.indexOf("$"));
                    }
                } else {
                    type = "String";
                    value = param;
                }

                builder.append(padding).append("public ").append(type).append(" get").append(toUpperCaseFirstOne(value)).append("() {\n");
                builder.append(padding).append("    return ").append(value).append(";\n");
                builder.append(padding).append("}\n\n");

                builder.append(padding).append("public void set").append(toUpperCaseFirstOne(value)).append("(").append(type).append(" ").append(value).append(") {\n");
                builder.append(padding).append("    this.").append(value).append(" = ").append(value).append(";\n");
                builder.append(padding).append("}\n\n");
            }
        }
        return builder.toString();
    }

    public static String createMethod(String padding, String[] annotations, String returnType, String methodName, String[] params, String[] exceptions, String content){
        StringBuilder method = new StringBuilder();
        if(annotations != null && annotations.length > 0){
            for (String annotation : annotations) {
                method.append(padding).append(annotation).append("\n");
            }
        }
        method.append(padding).append("public ").append(returnType).append(" ").append(methodName).append("(");
        if(params != null && params.length > 0){
            for(int i = 0; i < params.length; i++){
                method.append(params[i]);
                if(i+1 < params.length){
                    method.append(", ");
                }
            }
        }
        method.append(")");
        if(exceptions != null && exceptions.length > 0){
            method.append(" throws ");
            for(int i = 0; i < exceptions.length; i++){
                method.append(exceptions[i]);
                if(i+1 < exceptions.length){
                    method.append(", ");
                }
            }
        }
        method.append("{\n");
        if(isNotTextEmpty(content)){
            method.append(content);
        }
        method.append("\n    }\n\n");
        return method.toString();
    }

    public static String createObject(String padding, String name, String[] params){
        String buildObject = "";
        String returnObject = padding + "return new " + name + "(";
        if(params != null && params.length > 0){
            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                String input;
                if (param.contains("$")) {
                    String type ;
                    if(param.contains("[")) {
                        type = param.substring(param.indexOf("$") + 1, param.indexOf("["));
                        input = param.substring(0, param.indexOf("$"));
                        buildObject +=  padding + "List<" + type + "> " + input + " = new ArrayList<>();\n";
                        buildObject += padding + "for(int i = 0; i < 10; i++){\n";
                        buildObject += padding + "    " + input + ".add(new " + type + "());\n";
                        buildObject += padding + "}\n";
                    } else {
                        type = param.substring(param.indexOf("$") + 1);
                        input = param.substring(0, param.indexOf("$"));
                        buildObject += padding + type + " " + input + " = new " + type + "();\n";
                    }
                } else {
                    input = "\"" + param + "\"";
                }
                returnObject += input;
                if(i < params.length - 1){
                    returnObject += ", ";
                }
            }
        }
        returnObject += ");";
        return buildObject + returnObject;
    }

}
