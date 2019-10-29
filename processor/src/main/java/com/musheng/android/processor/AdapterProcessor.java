package com.musheng.android.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import static com.musheng.android.processor.MSProcessor.ADAPTER_PACKAGE;
import static com.musheng.android.processor.MSProcessor.ENTITY_PACKAGE;
import static com.musheng.android.processor.MSProcessor.JAVA_DIR;
import static com.musheng.android.processor.MSProcessor.LAYOUT_DIR;
import static com.musheng.android.processor.MSProcessor.ROOT_PACKAGE;
import static com.musheng.android.processor.MSProcessor.arrayConcat;
import static com.musheng.android.processor.MSProcessor.createClass;
import static com.musheng.android.processor.MSProcessor.createConstructor;
import static com.musheng.android.processor.MSProcessor.createImport;
import static com.musheng.android.processor.MSProcessor.createMethod;
import static com.musheng.android.processor.MSProcessor.createPackage;
import static com.musheng.android.processor.MSProcessor.createParams;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/30 14:51
 * Description :
 */
public class AdapterProcessor {

    public void process(Set<? extends Element> adapters){
        if (adapters != null && !adapters.isEmpty()) {
            for (Element annotatedElement : adapters) {
                if (annotatedElement.getKind() == ElementKind.METHOD) {
                    writeAdapter(annotatedElement);
                }
            }
        }
    }

    private void writeAdapter(Element adapterElement){
        try {
            MSAdapter annotation = adapterElement.getAnnotation(MSAdapter.class);
            String packageName = ADAPTER_PACKAGE;
            String adapterName = adapterElement.toString().replaceAll("\\(\\)", "");
            String className = adapterName + "Adapter";
            String entityName = annotation.entity();

            String fileDir = JAVA_DIR + packageName.replace(".", "\\") + "\\";

            File file = new File(fileDir + className + ".java");
            if(!annotation.forceBuild() && file.exists()){
                return;
            }

            String layoutName = "item_" + adapterName.toLowerCase();
            File layoutFile = new File(LAYOUT_DIR + layoutName + ".xml");
            if(!annotation.forceBuild() && layoutFile.exists()){
                return;
            }

            StringBuilder viewholderContent = new StringBuilder("            super(view);\n");

            StringBuilder layout = new StringBuilder();
            layout.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                    +"<RelativeLayout xmlns:android=\"http://schemas.android.com/apk/res/android\""
                    +"    android:background=\"@color/white\"\n"
                    +"    android:layout_width=\"match_parent\"\n"
                    +"    android:layout_height=\"match_parent\">\n");

            StringBuilder imports = new StringBuilder();
            imports.append(createImport("android.view.LayoutInflater"))
                    .append(createImport("android.view.View"))
                    .append(createImport("android.view.ViewGroup"))
                    .append(createImport("androidx.annotation.NonNull"))
                    .append(createImport("androidx.recyclerview.widget.RecyclerView"))
                    .append(createImport(ROOT_PACKAGE + ".R"))
                    .append(createImport("java.util.List"));

            if(entityName.endsWith("Entity")){
                imports.append(createImport(ENTITY_PACKAGE+ "." + entityName));
            }

            if(annotation.views().length > 0){
                for(String param : annotation.views()){
                    if (param.contains("$")) {
                        String type = param.substring(param.indexOf("$") + 1);
                        String value = param.substring(0, param.indexOf("$"));

                        viewholderContent.append("            ").append(value).append("= view.findViewById(R.id.").append(layoutName + "_" +value).append(");\n");

                        layout.append("    <").append(type.startsWith("MS") ? "com.musheng.android.view" + "." + type : type).append("\n")
                                .append("        android:id=\"@+id/").append(layoutName + "_" + value).append("\"\n")
                                .append("        android:layout_width=\"wrap_content\"\n")
                                .append("        android:layout_height=\"wrap_content\" />\n\n");

                        if(type.startsWith("MS")){
                            imports.append(createImport("com.musheng.android.view." + type));
                        }
                    }
                }
            }
            layout.append("</RelativeLayout>\n");


            StringBuilder content = new StringBuilder();
            content
                    .append(createParams("        ", arrayConcat(new String[]{"view$View"}, annotation.views())))
                    .append(createConstructor("        ", "ViewHolder", new String[]{"view$View"},
                            viewholderContent.toString()));

            String[] adapterParams = new String[]{"list$"+entityName+"[]"};
            String adapterContent = createParams("    ", adapterParams)
                    + createMethod("    ", new String[]{"@NonNull", "@Override"}, "ViewHolder", "onCreateViewHolder",
                    new String[]{"@NonNull ViewGroup parent", "int viewType"}, null,
                    "        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout." + layoutName + ", parent, false);\n"
                            + "        final ViewHolder holder = new ViewHolder(view);\n"
                            + "\n"
                            + "        return holder;"
            )
                    + createMethod("    ", new String[]{"@Override"}, "void", "onBindViewHolder",
                    new String[]{"ViewHolder holder", "int position"}, null, "        " + entityName + " item = list.get(position);\n")
                    + createConstructor("    ", className, adapterParams, "")
                    + createMethod("    ", new String[]{"@Override"}, "int", "getItemCount",
                    null, null,"        return list.size();")
                    + createClass("    ","static class", "ViewHolder", "RecyclerView.ViewHolder", content.toString(), null);

            String adapter = createPackage(packageName) + imports.toString() + createClass("","public class",
                    className, "RecyclerView.Adapter<" + className + ".ViewHolder>",
                    adapterContent, null);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(adapter);
            writer.flush();
            writer.close();

            BufferedWriter layoutWriter = new BufferedWriter(new FileWriter(layoutFile));
            layoutWriter.write(layout.toString());
            layoutWriter.flush();
            layoutWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
