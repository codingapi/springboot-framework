package com.codingapi.springboot.authorization.mask;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ColumnMaskContext {

    private final List<ColumnMask> columnMasks;

    private ColumnMaskContext() {
        this.columnMasks = new ArrayList<>();
    }

    public void addColumnMask(ColumnMask columnMask) {
        this.columnMasks.add(columnMask);
    }

    @Getter
    private final static ColumnMaskContext instance = new ColumnMaskContext();


    public <T> T mask(T value) {
        for (ColumnMask columnMask : columnMasks) {
            if (columnMask.support(value)) {
                return (T)columnMask.mask(value);
            }
        }
        return value;
    }


}
