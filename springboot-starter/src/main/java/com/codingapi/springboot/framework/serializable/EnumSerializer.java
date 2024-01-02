package com.codingapi.springboot.framework.serializable;

import com.codingapi.springboot.framework.em.IEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class EnumSerializer extends JsonSerializer<IEnum> {

    @Override
    public void serialize(IEnum iEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(iEnum.getCode());
    }
}
