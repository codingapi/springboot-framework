package com.codingapi.example.domain;

import com.codingapi.springboot.flow.data.IBindData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Leave implements IBindData {

    /**
     * 请假ID
     */
    private long id;

    /**
     * 请假原因
     */
    private String desc;

    /**
     * 请假用户
     */
    private User user;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;

    /**
     * 获取请假天数
     *
     * @return 请假天数
     */
    public int getLeaveDays() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long from = format.parse(startDate).getTime();
            long to = format.parse(endDate).getTime();
            return (int) ((to - from) / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            return 0;
        }
    }
}
