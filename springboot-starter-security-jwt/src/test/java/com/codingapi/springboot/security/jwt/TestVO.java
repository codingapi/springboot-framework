package com.codingapi.springboot.security.jwt;

import com.codingapi.springboot.framework.serializable.JsonSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TestVO implements JsonSerializable {

    private String name;
}
