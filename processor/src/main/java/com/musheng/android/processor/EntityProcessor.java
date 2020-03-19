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
import static com.musheng.android.processor.MSProcessor.createPackage;
import static com.musheng.android.processor.MSProcessor.createParams;
import static com.musheng.android.processor.MSProcessor.isNotTextEmpty;
import static com.musheng.android.processor.MSProcessor.toUpperCaseFirstOne;

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
            if((annotation.forceBuildFetcher() || annotation.forceBuildRequest() || annotation.forceBuildResponse())
                && annotation.keep()){
                throw new RuntimeException(annotation.name() + " can't override, check 'keep' annotation.");
            }
            String packageName = ENTITY_PACKAGE;
            String className = annotation.name();
            String fileDir = JAVA_DIR + packageName.replace(".", "\\") + "\\";
            writeResponse(packageName, className, fileDir, annotation.response(), annotation.dbTable(), annotation.forceBuildResponse());
            if(annotation.post()){
                String baseName = methodElement.toString().replaceAll("\\(\\)", "").replaceAll("/","_");
                writeFetcher(packageName, className, fileDir, annotation.request(), annotation.response(), baseName,  annotation.encrypt(),annotation.request().length > 0,  annotation.json(), annotation.forceBuildFetcher());
                writeRequest(packageName, className, fileDir, annotation.request(),  annotation.forceBuildRequest());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeResponse(String packageName, String className, String dir, String[] params, String dbTable, boolean forceBuild) throws IOException{

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
        String classContent = createParams("    ", params, true)
                + createConstructor("    ", className, null, "");

        if(isNotTextEmpty(dbTable)){
            builder += createImport("org.greenrobot.greendao.annotation.*");
            builder += "\n";
            builder += "@Entity(\n" +
                    "        nameInDb = \"" + dbTable + "\"\n" +
                    ")";
        } else {
            classContent += createConstructor("    ", className, params, "");
        }
        classContent += createGetterAndSetter("    ", params);

        builder += createClass("","public class", className,"BaseEntity", classContent, null);

        writer.write(builder);
        writer.flush();
        writer.close();
    }

    private void writeFetcher(String packageName, String className, String dir, String[] requests, String[] params, String baseName, String encrypt, boolean hasRequestBody, boolean isJson, boolean forceBuild) throws IOException{
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
        if(isJson){
            if(isNotTextEmpty(encrypt)){
                defaultContent = padding + "return ((Api)MSRetrofit.getApi())." + baseName + (hasRequestBody ? ("(new "+ encrypt +"(request.toJSONString()))") : "()") + ".execute().body();";
            } else {
                defaultContent = padding + "return ((Api)MSRetrofit.getApi())." + baseName + (hasRequestBody ? ("(request)") : "()") + ".execute().body();";
            }
        } else {
            defaultContent = padding + "return ((Api)MSRetrofit.getApi())." + baseName+"(";
            if(requests.length == 0){
                requests = new String[]{"defaultParam"};
            }

            for(int i = 0; i < requests.length; i++){
                String item = requests[i];
                defaultContent = defaultContent + "request.get" + toUpperCaseFirstOne(item)+"()";
                if(i < requests.length - 1){
                    defaultContent = defaultContent + ", ";
                }
            }

            defaultContent += (").execute().body();");
        }

/*        String debugContent = createObject("            ", className, params);
/       String content = padding + "if(ServerConfig.IS_LOCAL_DEBUG){\n";
        content += padding + "    Thread.sleep(1000);\n";
        content += debugContent + "\n";
        content += padding + "}\n";
        content += defaultContent;*/

        String content = defaultContent;

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
                        "    }\n"
                        + "    public String getDefaultParam(){\n" +
                        "        return \"\";\n" +
                        "    }\n"
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
            if(!annotation.json()){
                content.append("    @FormUrlEncoded\n");
            }
            content.append("    @POST(\"/").append(url).append("\")\n");
            content.append("    Call<").append(name).append("> ").append(url.replaceAll("/", "_"));

            if (annotation.json()){
                if(annotation.request().length > 0){
                    if(isNotTextEmpty(encrypt)){
                        content.append("(@Body ").append(encrypt).append(" encrypt);");
                    } else {
                        content.append("(@Body ").append(name).append("Request param);");
                    }
                } else {
                    content.append("();");
                }
            } else {
                String[] request = annotation.request();
                if(request.length == 0){
                    request = new String[]{"defaultParam"};
                }
                content.append("(");
                for(int i = 0; i < request.length; i++){
                    String item = request[i];
                    content.append("@Field(\""+ item +"\") String " + item);
                    if(i < request.length - 1){
                        content.append(", ");
                    }
                }
                content.append(");");
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
                    + createImport("retrofit2.http.Field")
                    + createImport("retrofit2.http.FormUrlEncoded")
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
