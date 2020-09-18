package com.musheng.android.common.objectbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author      : MuSheng
 * CreateDate  : 2020/9/17 11:46
 * Description :
 */
public class XCollections {

    private static HashMap<Class, XTranslate> translationMap = new HashMap<>();

    public static List translate(List list){
        List newList = new ArrayList();
        if(list != null){
            for(Object item : list){
                if(item instanceof XTranslation){
                    newList.add(((XTranslation) item).translate());
                }
                if(translationMap.containsKey(item.getClass())){
                    newList.add(translationMap.get(item.getClass()).translate(item));
                }
            }
        }
        return newList;
    }

    public static void registerTranslation(Class type, XTranslate translation){
        translationMap.put(type, translation);
    }

}
