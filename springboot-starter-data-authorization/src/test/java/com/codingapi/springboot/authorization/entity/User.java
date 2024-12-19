package com.codingapi.springboot.authorization.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "t_user")
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private LocalDate birthDate;

    private String address;

    private String idCard;

    private String phone;

    private long unitId;

    private long departId;

    public User(String name, LocalDate birthDate, String address, String idCard, String phone,Depart depart) {
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
        this.idCard = idCard;
        this.phone = phone;
        this.unitId = depart.getUnitId();
        this.departId = depart.getId();
    }
}
