package com.company.news.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.springframework.stereotype.Component;

import com.company.news.rest.RestConstants;

@Component  
public class JsonDateSerializer extends JsonSerializer<Date> {   
       
    @Override  
    public void serialize(Date value, JsonGenerator jgen,   
            SerializerProvider provider) throws IOException,   
            JsonProcessingException {   
        SimpleDateFormat formatter = new SimpleDateFormat(RestConstants.SimpleDateFormat);   
        String formattedDate = formatter.format(value);   
        jgen.writeString(formattedDate);   
    }   
}  