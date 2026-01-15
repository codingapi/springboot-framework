package com.codingapi.springboot.framework.dto.offset.context;

import com.codingapi.springboot.framework.dto.offset.ICurrentOffset;
import com.codingapi.springboot.framework.dto.offset.ICurrentPageOffset;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

public class CurrentPageOffsetContext implements ICurrentPageOffset {

    @Getter
    private static final CurrentPageOffsetContext instance = new CurrentPageOffsetContext();

    private CurrentPageOffsetContext(){}

    @Setter
    private ICurrentPageOffset currentPageOffset = new ICurrentPageOffset(){
        @Override
        public int getCurrentPage(ICurrentOffset target, int current) {
            if(target instanceof PageRequest){
                return current;
            }
            return current - 1;
        }
    };

    public int getCurrentPage(ICurrentOffset target,int current) {
        return currentPageOffset.getCurrentPage(target,current);
    }

}
