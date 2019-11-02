package com.musheng.android.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import static com.musheng.android.processor.MSProcessor.ENTITY_PACKAGE;
import static com.musheng.android.processor.MSProcessor.JAVA_DIR;
import static com.musheng.android.processor.MSProcessor.LAYOUT_DIR;
import static com.musheng.android.processor.MSProcessor.ROOT_PACKAGE;
import static com.musheng.android.processor.MSProcessor.createImport;
import static com.musheng.android.processor.MSProcessor.createMethod;
import static com.musheng.android.processor.MSProcessor.isNotTextEmpty;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/30 14:51
 * Description :
 */
public class ContractProcessor {

    public void process(Set<? extends Element> contracts){
        if (contracts != null && !contracts.isEmpty()) {
            for (Element annotatedElement : contracts) {
                if (annotatedElement.getKind() == ElementKind.INTERFACE) {
                    writeContract(annotatedElement);
                }
            }
        }
    }

    private void writeContract(Element classElement) {
        MSContract annotation = classElement.getAnnotation(MSContract.class);
        try {
            String canonicalName = classElement.toString();
            String packageName = canonicalName.substring(0, canonicalName.lastIndexOf("."));
            String className = canonicalName.substring(canonicalName.lastIndexOf(".") + 1);
            String fileDir = JAVA_DIR + packageName.replace(".", "\\") + "\\";

            writePresenter(packageName, className, annotation.name(), annotation.refreshEntity(), annotation.loadMoreEntity(),
                    fileDir, annotation.forcePresenter());
            if (annotation.isFragment()){
                writeFragment(packageName, className, annotation.name(), annotation.refreshEntity(), annotation.loadMoreEntity(),
                        fileDir, annotation.forceView());
            } else {
                writeRouter(packageName, annotation.name(),
                        fileDir, annotation.forceRouter());
                writeActivity(packageName, className, annotation.name(), annotation.refreshEntity(), annotation.loadMoreEntity(),
                        fileDir, annotation.forceView());
            }
            writeLayout(annotation.name(), annotation.refreshEntity(), annotation.loadMoreEntity(),
                    LAYOUT_DIR + (annotation.isFragment() ? "fragment" : "activity") + "_" + annotation.name().toLowerCase() + ".xml" ,
                    annotation.forceLayout());
            if(isNotTextEmpty(annotation.refreshEntity()) && isNotTextEmpty(annotation.loadMoreEntity())){
                writeItemLayout(LAYOUT_DIR + "item_" + annotation.name().toLowerCase() + ".xml", annotation.forceLayout());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeRouter(String packageName, String baseName, String dir, boolean forceBuild) throws IOException{
        String fileName = baseName + "Router.java";
        String path = dir + fileName;
        File file = new File(path);
        if(!forceBuild && file.exists()){
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String builder = "package " + packageName + ";\n" +
                "import com.musheng.android.router.MSBaseRouter;\n\n" +
                "public class " + baseName + "Router extends MSBaseRouter {\n\n" +
                "    public static final String PATH = \"/app/" + baseName + "\";\n\n" +
                "    @Override\n" +
                "    public String getPath() {\n" +
                "        return PATH;\n" +
                "    }\n" +
                "}\n";
        writer.write(builder);
        writer.flush();
        writer.close();
    }

    private void writePresenter(String packageName, String className, String baseName, String entity, String itemEntity,
                                String dir, boolean forceBuild) throws IOException{
        String fileName = baseName + "Presenter.java";
        String path = dir + fileName;
        File file = new File(path);
        if(!forceBuild && file.exists()){
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String result = "package " + packageName + ";\n" +
                "import com.musheng.android.common.mvp.BasePresenter;\n" +
                "import com.musheng.android.fetcher.MSFetcherThrowable;\n" +
                "import com.musheng.android.common.mvp.LoadMoreFetcherResponse;\n" +
                "import java.util.List;\n";

        if(isNotTextEmpty(entity)){
            result = result
                    + "import " + ENTITY_PACKAGE + "." + entity + ";\n"
                    + "import " + ENTITY_PACKAGE + "." + entity + "Fetcher;\n"
                    + "import " + ENTITY_PACKAGE + "." + entity + "Request;\n";
        }


        result = result +

                "\npublic class " + baseName + "Presenter extends BasePresenter<" + className + ".View> implements "+ className + ".Presenter {\n\n";
        if(isNotTextEmpty(entity)){
            result +=
                    "    private " + entity + "Fetcher fetcher = new " + entity + "Fetcher();\n\n" +
                            "    @Override\n" +
                            "    public void onLoadMore(String page, String pageSize, List list) {\n" +
                            "        super.onLoadMore(page, pageSize, list);\n\n" +
                            "        fetcher.enqueue(null,\n" +
                            "        new LoadMoreFetcherResponse<" + entity + "Request, " + entity + ">(this, page) {\n\n" +
                            "            @Override\n" +
                            "            public void onLoadMoreNext(" + entity + " refreshEntity, " + entity + "Request request) {\n\n" +

                            "            }\n\n" +
                            "            @Override\n" +
                            "            public void onLoadMoreError(MSFetcherThrowable error) {\n" +
                            "                getView().showErrorTips(error.getErrorMessage());\n" +
                            "            }\n\n" +
                            "            @Override\n" +
                            "            public void onLoadMoreCancel() {\n" +
                            "            }\n\n";

            if(isNotTextEmpty(itemEntity)){
                result +=
                        "            @Override\n" +
                                "            public List getList(" + entity +" refreshEntity) {\n" +
                                "                return null;\n" +
                                "            }\n\n" +
                                "            @Override\n" +
                                "            public int getTotalCount(" + entity + " refreshEntity) {\n" +
                                "                return 0;\n" +
                                "            }\n" ;
            }
            result +=
                    "        });\n" +
                            "    }\n" ;
        }
        result += "}\n";
        writer.write(result);
        writer.flush();
        writer.close();
    }

    private void writeActivity(String packageName, String className, String baseName, String entity, String itemEntity, String dir, boolean forceBuild) throws IOException{
        String fileName = baseName + "Activity.java";
        String path = dir + fileName;
        File file = new File(path);
        if(!forceBuild &&file.exists()){
            return;
        }

        String initWight = "";
        if(isNotTextEmpty(entity)){
            initWight = initWight
                    +  "        setSmartRefreshLayout(refreshLayout);\n";
            if(isNotTextEmpty(itemEntity)){
                initWight = initWight
                        +  "        adapter = new CommonAdapter<" + itemEntity + ">(getViewContext(), R.layout.item_" + baseName.toLowerCase() + ", list) {\n"
                        +  "            @Override\n"
                        +  "            protected void convert(ViewHolder holder, " + itemEntity + " refreshEntity, int position) {\n\n"
                        +  "            }\n"
                        +  "        };\n"
                        +  "        recyclerView.setAdapter(adapter);\n";
            }
        }
        String initParam = "";
        if(isNotTextEmpty(entity)){
            initParam +=    "    @BindView(R.id.refresh_" + baseName.toLowerCase() + ")\n"
                    + "    SmartRefreshLayout refreshLayout;\n\n";
            if(isNotTextEmpty(itemEntity)){
                initParam +=    "    @BindView(R.id.rv_" + baseName.toLowerCase() + ")\n"
                        + "    MSRecyclerView recyclerView;\n\n"
                        + "    private List<" + itemEntity + "> list = new ArrayList<>();\n"
                        + "    private CommonAdapter<"+ itemEntity +"> adapter;\n\n"
                ;
            }
        }

        String content = initParam;

        content = content + createMethod("    ",new String[]{"@Override"}, baseName+"Contract.Presenter",
                "initPresenter", null, null,"        return new " + baseName + "Presenter();")

                + createMethod("    ",new String[]{"@Override"}, "void",
                "setRootView", new String[]{"Bundle savedInstanceState"}, null,"        setContentView(R.layout.activity_" + baseName.toLowerCase() + ");")

                + createMethod("    ",new String[]{"@Override"}, "void",
                "initWidget", null, null, initWight);

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String builder = "package " + packageName + ";\n" +
                "import android.os.Bundle;\n" +
                "import " + ROOT_PACKAGE + ".R;\n" +
                "import com.musheng.android.common.mvp.BaseActivity;\n";

        if(isNotTextEmpty(entity)){
            builder = builder +
                    "import butterknife.BindView;\n" +
                    "import com.scwang.smartrefresh.layout.SmartRefreshLayout;\n";
            if(isNotTextEmpty(itemEntity)){
                builder = builder +
                        "import java.util.ArrayList;\n" +
                        "import java.util.List;\n" +
                        "import com.musheng.android.view.MSRecyclerView;\n" +
                        "import com.musheng.android.view.recyclerview.CommonAdapter;\n" +
                        "import com.musheng.android.view.recyclerview.base.ViewHolder;\n" +
                        "import " + ENTITY_PACKAGE + "." + itemEntity +";\n";
            }
        }
        builder = builder +
                "import com.alibaba.android.arouter.facade.annotation.Route;\n\n" +
                "@Route(path = " + baseName + "Router.PATH)\n"+
                "public class " + baseName + "Activity extends BaseActivity<" + className + ".Presenter> implements "+ className + ".View {\n\n" +
                content +
                "}\n";
        writer.write(builder);
        writer.flush();
        writer.close();
    }

    private void writeFragment(String packageName, String className, String baseName, String entity, String itemEntity, String dir, boolean forceBuild) throws IOException{
        String fileName = baseName + "Fragment.java";
        String path = dir + fileName;
        File file = new File(path);
        if(!forceBuild &&file.exists()){
            return;
        }

        String initWight = "";
        if(isNotTextEmpty(entity)){
            initWight = initWight
                    +  "        setSmartRefreshLayout(refreshLayout);\n";
            if(isNotTextEmpty(itemEntity)){
                initWight = initWight
                        +  "        adapter = new CommonAdapter<" + itemEntity + ">(getViewContext(), R.layout.item_" + baseName.toLowerCase() + ", list) {\n"
                        +  "            @Override\n"
                        +  "            protected void convert(ViewHolder holder, " + itemEntity + " refreshEntity, int position) {\n\n"
                        +  "            }\n"
                        +  "        };\n"
                        +  "        recyclerView.setAdapter(adapter);\n";
            }
        }
        String initParam = "";
        if(isNotTextEmpty(entity)){
            initParam +=    "    @BindView(R.id.refresh_" + baseName.toLowerCase() + ")\n"
                    + "    SmartRefreshLayout refreshLayout;\n\n";
            if(isNotTextEmpty(itemEntity)){
                initParam +=    "    @BindView(R.id.rv_" + baseName.toLowerCase() + ")\n"
                        + "    MSRecyclerView recyclerView;\n\n"
                        + "    private List<" + itemEntity + "> list = new ArrayList<>();\n"
                        + "    private CommonAdapter<"+ itemEntity +"> adapter;\n\n"
                ;
            }
        }

        String content = initParam;

        content = content + createMethod("    ",new String[]{"@Override"}, baseName+"Contract.Presenter",
                "initPresenter", null, null,"        return new " + baseName + "Presenter();")

                + createMethod("    ",new String[]{"@Override"}, "View",
                "getRootView", new String[]{"LayoutInflater inflater", "ViewGroup container", "Bundle savedInstanceState"},
                null,"        return inflater.inflate(R.layout.fragment_" + baseName.toLowerCase() + ", container, false);")

                + createMethod("    ",new String[]{"@Override"}, "void",
                "initWidget", null, null, initWight);

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String builder = "package " + packageName + ";\n"
                + createImport("android.os.Bundle")
                + createImport("android.view.LayoutInflater")
                + createImport("android.view.View")
                + createImport("android.view.ViewGroup")
                + createImport(ROOT_PACKAGE + ".R")
                + createImport("com.musheng.android.common.mvp.BaseFragment");

        if(isNotTextEmpty(entity)){
            builder = builder +
                    "import butterknife.BindView;\n" +
                    "import com.scwang.smartrefresh.layout.SmartRefreshLayout;\n";
            if(isNotTextEmpty(itemEntity)){
                builder = builder +
                        "import java.util.ArrayList;\n" +
                        "import java.util.List;\n" +
                        "import com.musheng.android.view.MSRecyclerView;\n" +
                        "import com.musheng.android.view.recyclerview.CommonAdapter;\n" +
                        "import com.musheng.android.view.recyclerview.base.ViewHolder;\n" +
                        "import " + ENTITY_PACKAGE + "." + itemEntity +";\n";
            }
        }
        builder = builder
                + "\npublic class " + baseName + "Fragment extends BaseFragment<" + className + ".Presenter> implements "+ className + ".View {\n\n" +
                content + "}\n";
        writer.write(builder);
        writer.flush();
        writer.close();
    }

    private void writeLayout(String baseName, String entity, String itemEntity, String filepath, boolean forceBuild) throws IOException {

        File file = new File(filepath);
        if(!forceBuild && file.exists()){
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        String result = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                +"<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
                +"    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n"
                +"    android:orientation=\"vertical\"\n"
                +"    android:background=\"@color/white\"\n"
                +"    android:layout_width=\"match_parent\"\n"
                +"    android:layout_height=\"match_parent\">\n\n"
                +"    <com.lianhe.app.mall.view.MSTopBar\n"
                +"        android:id=\"@+id/top_" + baseName.toLowerCase() +"\"\n"
                +"        android:layout_width=\"match_parent\"\n"
                +"        android:layout_height=\"wrap_content\"\n"
                +"        app:ms_top_title=\"\"\n"
                +"    />\n\n";

        if(isNotTextEmpty(entity)){
            result = result +
                    "    <com.scwang.smartrefresh.layout.SmartRefreshLayout\n" +
                    "        android:id=\"@+id/refresh_" + baseName.toLowerCase() +"\"\n" ;
            if(!isNotTextEmpty(itemEntity)){
                result = result +
                        "        app:srlEnableLoadMore=\"false\"\n";
            }
            result = result +
                    "        android:layout_width=\"match_parent\"\n" +
                    "        android:layout_height=\"match_parent\">\n\n";
            if(isNotTextEmpty(itemEntity)){
                result = result +
                        "        <com.musheng.android.view.MSRecyclerView\n" +
                        "            android:id=\"@+id/rv_" + baseName.toLowerCase() + "\"\n" +
                        "            android:layout_width=\"match_parent\"\n" +
                        "            android:layout_height=\"match_parent\" />\n";
            }
            result = result +
                    "    </com.scwang.smartrefresh.layout.SmartRefreshLayout>\n\n";
        }

        result = result
                +"</LinearLayout>\n";

        writer.write(result);
        writer.flush();
        writer.close();
    }

    private void writeItemLayout(String filepath, boolean forceBuild) throws IOException {
        File file = new File(filepath);
        if(!forceBuild && file.exists()){
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String builder = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                +"<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
                +"    android:orientation=\"vertical\"\n"
                +"    android:background=\"@color/white\"\n"
                +"    android:layout_width=\"match_parent\"\n"
                +"    android:layout_height=\"wrap_content\">\n"
                +"</LinearLayout>\n";
        writer.write(builder);
        writer.flush();
        writer.close();
    }
}
