package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.em.FlowButtonType;
import lombok.Getter;
import lombok.Setter;

/**
 * 流程按钮
 */
@Setter
@Getter
public class FlowButton {

    /**
     * 编号
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 样式
     */
    private String style;
    /**
     * 事件类型
     */
    private FlowButtonType type;
    /**
     * 自定义事件内容 （后端脚本）
     */
    private String groovy;
    /**
     * 排序
     */
    private int order;
}
