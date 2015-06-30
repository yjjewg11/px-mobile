package com.company.news.json;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.springframework.stereotype.Component;

import com.company.news.rest.RestConstants;

@Component  
public class JsonTimestampSerializer extends JsonSerializer<Timestamp> {   
       
    @Override  
    public void serialize(Timestamp value, JsonGenerator jgen,   
            SerializerProvider provider) throws IOException,   
            JsonProcessingException {   
        SimpleDateFormat formatter = new SimpleDateFormat(RestConstants.SimpleTimestampFormat);   
        String formattedDate = formatter.format(value);   
        jgen.writeString(formattedDate);   
    }   
}  