package com.nhnacademy.todolist.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

public class WebTestConfig {
    private WebTestConfig(){
        throw new IllegalStateException("Web Test Config Class!");
    }

    public static ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        //pretty print json
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        //value -> null 무시
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //LocalDate, LocalDateTime support jsr310
        objectMapper.registerModule(new JavaTimeModule());
        //timestamp 출력을 disable. -> 문자열형태로 출력
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    public static XmlMapper xmlMapper(){
        XmlMapper xmlMapper = new XmlMapper();
        //pretty print xml
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        //value -> null 무시
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //LocalDate, LocalDateTime support jsr310
        xmlMapper.registerModule(new JavaTimeModule());
        //timestamp 출력을 disable. -> 문자열형태로 출력
        xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return xmlMapper;
    }

    public static MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
        return new MappingJackson2HttpMessageConverter(objectMapper());
    }

    public static MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter(){
        return new MappingJackson2XmlHttpMessageConverter(xmlMapper());
    }
}
