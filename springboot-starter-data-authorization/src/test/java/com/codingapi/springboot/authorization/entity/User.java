package com.codingapi.springboot.authorization.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    public User(String name, LocalDate birthDate, String address, String idCard, String phone) {
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
        this.idCard = idCard;
        this.phone = phone;
    }
}
