package com.codingapi.springboot.permission.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lorne
 * @since 1.0.0
 */
abstract class BaseAnalyzerFilter implements SqlAnalyzerFilter{

    protected String[] split(Pattern pattern,String sql){
        Matcher matcher =  pattern.matcher(sql);
        if (matcher.find()){
            String values =  matcher.group(0);
            if(values!=null){
                values = values.trim();
                values = values.substring(1,values.length()-1);
                return values.split(",");
            }
        }
        return null;
    }

    protected List<String> find(Pattern pattern,String sql){
        Matcher matcher =  pattern.matcher(sql);
        List<String> list = new ArrayList<>();
        while (matcher.find()){
            int count = matcher.groupCount();
            for(int i=0;i<count;i++){
                list.add(matcher.group(i).trim());
            }
        }
        return list;
    }

    protected boolean match(Pattern pattern,String sql){
        Matcher matcher =  pattern.matcher(sql);
        return matcher.find();
    }




}
