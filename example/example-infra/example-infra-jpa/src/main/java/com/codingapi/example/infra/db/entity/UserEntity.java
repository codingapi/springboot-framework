package com.codingapi.example.infra.db.entity;

import com.codingapi.example.domain.user.entity.UserMetric;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(unique = true)
    private String username;

    private String password;

    private boolean isFlowManager;

    private long entrustOperatorId;

    private String entrustOperatorName;

    private long createTime;

    public UserMetric getUserMetric() {
        return new UserMetric(name,username,password);
    }
}
