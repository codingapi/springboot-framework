package com.codingapi.example.infra.flow.form;

import com.codingapi.example.domain.leave.entity.Leave;
import com.codingapi.springboot.flow.bind.IBindData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveForm implements IBindData {
    private long id;
    private String desc;
    private int days;
    private String username;
    private long createTime;


    public Leave toLeave() {
        Leave leave = new Leave();
        leave.setId(id);
        leave.setUsername(username);
        leave.setCreateTime(createTime);
        leave.setDays(days);
        leave.setDesc(desc);
        return leave;
    }

}
