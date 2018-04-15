package me.codetalk.util;

import java.io.IOException;
import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 
 * 用法:
    @JsonProperty("update_time")
	@JsonSerialize(nullsUsing=NullTimestampSerializer.class)
	public Timestamp getCreateDate() {
		return createDate;
	}
 * 
 * @author guobxu
 *
 */
public class NullTimestampSerializer extends JsonSerializer<Timestamp>{
	 
    public void serialize(Timestamp ts, JsonGenerator jsonGen, SerializerProvider serProv)
                                            throws IOException, JsonProcessingException {
    	if(ts == null) jsonGen.writeNumber(0);
    }
}