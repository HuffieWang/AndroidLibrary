package com.musheng.android.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import static com.musheng.android.processor.MSProcessor.CONFIG_CLASS;
import static com.musheng.android.processor.MSProcessor.ENTITY_PACKAGE;
import static com.musheng.android.processor.MSProcessor.JAVA_DIR;
import static com.musheng.android.processor.MSProcessor.createClass;
import static com.musheng.android.processor.MSProcessor.createConstructor;
import static com.musheng.android.processor.MSProcessor.createGetterAndSetter;
import static com.musheng.android.processor.MSProcessor.createImport;
import static com.musheng.android.processor.MSProcessor.createMethod;
import static com.musheng.android.processor.MSProcessor.createObject;
import static com.musheng.android.processor.MSProcessor.createPackage;
import static com.musheng.android.processor.MSProcessor.createParams;
import static com.musheng.android.processor.MSProcessor.isNotTextEmpty;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/30 14:51
 * Description :
 */
public class EntityProcessor {

    public void process(Set<? extends Element> entities){
        if (entities != null && !entities.isEmpty()) {
            List<Element> elements = new ArrayList<>();
            for (Element annotatedElement : entities) {
                if (annotatedElement.getKind() == ElementKind.METHOD) {

                    writeEntity(annotatedElement);

                    elements.add(annotatedElement);
                }
            }
            if(!elements.isEmpty()){
                writeApi(elements);
            }
        }
    }

    private void writeEntity(Element methodElement) {
        try {
            MSEntity annotation = methodElement.getAnnotation(MSEntity.class);
            String packageName = ENTITY_PACKAGE;
            String className = annotation.name();
            String fileDir = JAVA_DIR + packageName.replace(".", "\\") + "\\";
            writeResponse(packageName, className, fileDir, annotation.response(), annotation.forceBuildResponse());
            if(annotation.post()){
                String baseName = methodElement.toString().replaceAll("\\(\\)", "").replaceAll("/","_");
                writeFetcher(packageName, className, fileDir, annotation.response(), baseName,  annotation.encrypt(),annotation.request().length > 0,  annotation.forceBuildFetcher());
                writeRequest(packageName, className, fileDir, annotation.request(),  annotation.forceBuildRequest());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeResponse(String packageName, String className, String dir, String[] params, boolean forceBuild) throws IOException{

        String fileName = className + ".java";
        String path = dir + fileName;
        File file = new File(path);
        if(!forceBuild && file.exists()){
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String builder = createPackage(packageName);
        if(params != null && params.length > 0){
            for(String param : params){
                if(param.contains("[")){
                    builder += createImport("java.util.List");
                    break;
                }
            }
        }
        builder += createClass("","public class", className,"BaseEntity", createParams("    ", params)
                        + createConstructor("    ", className, null, "")
                        + createConstructor("    ", className, params, "")
                        + createGetterAndSetter("    ", params),
                null);
        writer.write(builder);
        writer.flush();
        writer.close();
    }

    private void writeFetcher(String packageName, String className,  String dir, String[] params, String baseName, String encrypt, boolean hasRequestBody, boolean forceBuild) throws IOException{
        String fileName = className + "Fetcher.java";
        String path = dir + fileName;
        String simpleName = className + "Fetcher";
        String requestName = className + "Request";
        File file = new File(path);
        if(!forceBuild && file.exists()){
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        String padding = "        ";

        String defaultContent = "";
        if(isNotTextEmpty(encrypt)){
            defaultContent = padding + "return ((Api)MSRetrofit.getApi())." + baseName + (hasRequestBody ? ("(new "+ encrypt +"(request.toJSONString()))") : "()") + ".execute().body();";
        } else {
            defaultContent = padding + "return ((Api)MSRetrofit.getApi())." + baseName + (hasRequestBody ? ("(request)") : "()") + ".execute().body();";
        }

        String debugContent = createObject("            ", className, params);
        String content = padding + "if(ServerConfig.IS_LOCAL_DEBUG){\n";
        content += padding + "    Thread.sleep(1000);\n";
        content += debugContent + "\n";
        content += padding + "}\n";
        content += defaultContent;

        String fetchNetwork = createMethod("    ",new String[]{"@Override"}, className,
                "fetchNetwork", new String[]{requestName + " request"},  new String[]{"Exception"}, content

        );
        String fetchCache = createMethod("    ",new String[]{"@Override"}, className,
                "fetchCache", new String[]{requestName + " request"}, new String[]{"Exception"},
                "        return null;"
        );
        String writeCache = createMethod("    ",new String[]{"@Override"}, "void",
                "writeCache", new String[]{requestName + " request", className+ " fetcher"}, new String[]{"Exception"},
                ""
        );
        String fetchDefault = createMethod("    ",new String[]{"@Override"}, className,
                "fetchDefault", new String[]{requestName + " request"}, new String[]{"Exception"},
                "        return null;"
        );
        String fetcher = createPackage(packageName)
                + createImport("com.musheng.android.fetcher.MSBaseFetcher")
                + createImport("com.musheng.android.common.retrofit.MSRetrofit")
                + createImport("java.util.ArrayList")
                + createImport("java.util.List")
                + createImport(CONFIG_CLASS)
                + createClass("","public class", simpleName, "MSBaseFetcher<" + requestName + "," + className + ">",
                fetchNetwork + fetchCache + writeCache + fetchDefault, null);

        writer.write(fetcher);
        writer.flush();
        writer.close();
    }

    private void writeRequest(String packageName, String className, String dir, String[] params, boolean forceBuild) throws IOException{

        String fileName = className + "Request.java";
        String path = dir + fileName;
        String simpleName = className + "Request";
        File file = new File(path);
        if(!forceBuild && file.exists()){
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String builder = createPackage(packageName)
                + createImport("com.musheng.android.fetcher.MSFetcherRequest")
                + createImport("com.google.gson.Gson")
                + createClass("", "public class", simpleName,null, createParams("    ", params)
                        + createConstructor("    ", simpleName, params, "")
                        + createGetterAndSetter("    ", params)
                        + "    public String toJSONString(){\n" +
                        "        Gson gson = new Gson();\n" +
                        "        return gson.toJson(this);\n" +
                        "    }"
                ,
                new String[]{"MSFetcherRequest"});
        writer.write(builder);
        writer.flush();
        writer.close();
    }

    private void writeApi(List<Element> elements){

        String packageName = ENTITY_PACKAGE;
        String className = "Api";
        String fileDir = JAVA_DIR + packageName.replace(".", "\\") + "\\";

        StringBuilder content = new StringBuilder();
        for(Element element : elements){
            MSEntity annotation = element.getAnnotation(MSEntity.class);
            if(!annotation.post()){
                continue;
            }
            String name = annotation.name();
            String encrypt = annotation.encrypt();
            String url = element.toString().replaceAll("\\(\\)", "").replaceAll("_", "/");
            content.append("    @POST(\"/").append(url + ".do").append("\")\n");
            content.append("    Call<").append(name).append("> ").append(url.replaceAll("/", "_"));
            if(annotation.request().length > 0){
                if(isNotTextEmpty(encrypt)){
                    content.append("(@Body ").append(encrypt).append(" encrypt);");
                } else {
                    content.append("(@Body ").append(name).append("Request param);");
                }
            } else {
                content.append("();");
            }
            content.append("\n\n");
        }

        String fileName = className + ".java";
        String path = fileDir + fileName;
        File file = new File(path);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String builder = createPackage(packageName)
                    + createImport("retrofit2.Call")
                    + createImport("retrofit2.http.Body")
                    + createImport("retrofit2.http.POST")
                    + createClass("", "public interface", className,null,
                    content.toString(),
                    null);
            writer.write(builder);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
