package com.codingapi.springboot.permission.db.template;

import java.util.List;

public class SQLTemplateInitializer {

    public SQLTemplateInitializer(List<SQLTemplate> templateList) {
        SQLTemplateContext.getInstance().addSQLTemplates(templateList);
    }

}
