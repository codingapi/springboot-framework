package com.codingapi.springboot.permission.db.template;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lorne
 * @since 1.0.0
 */
public class SQLTemplateContext {

    private final List<SQLTemplate> templates = new ArrayList<>();

    private static SQLTemplateContext context;

    private SQLTemplateContext() {

    }

    public static SQLTemplateContext getInstance() {
        if(context==null){
            synchronized (SQLTemplateContext.class){
                if(context==null){
                    context = new SQLTemplateContext();
                }
            }
        }
        return context;
    }

    protected void addSQLTemplates(List<SQLTemplate> sqlTemplateList){
        templates.addAll(sqlTemplateList);
    }


    public SQLTemplate template(String driverName){
        for(SQLTemplate template : templates){
            if(template.match(driverName)){
                return template;
            }
        }
        return null;
    }
}
