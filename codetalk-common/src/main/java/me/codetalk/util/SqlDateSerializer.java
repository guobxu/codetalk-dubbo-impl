package me.codetalk.util;

import java.io.IOException;
import java.sql.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 将java.sql.Date转换为Long
 * @author guobxu
 *
 */
public class SqlDateSerializer extends JsonSerializer<Date>{
 
    public void serialize(Date dt, JsonGenerator jsonGen, SerializerProvider serProv)
                                            throws IOException, JsonProcessingException {
        jsonGen.writeNumber(dt.getTime());
    }
}



