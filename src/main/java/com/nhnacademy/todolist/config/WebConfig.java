package com.nhnacademy.todolist.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.nhnacademy.todolist.interceptor.AuthCheckInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = {com.nhnacademy.todolist.controller.ControllerBase.class})
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware {
    private final ObjectMapper objectMapper;

    private final XmlMapper xmlMapper;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public WebConfig(@Qualifier("objectMapper") ObjectMapper objectMapper, @Qualifier("xmlMapper") XmlMapper xmlMapper) {
        this.objectMapper = objectMapper;
        this.xmlMapper = xmlMapper;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converters.stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .findFirst()
                .orElse(null);
        if (Objects.nonNull(jsonConverter)) {
            jsonConverter.setObjectMapper(objectMapper);
        }

        MappingJackson2XmlHttpMessageConverter xmlHttpMessageConverter = (MappingJackson2XmlHttpMessageConverter) converters.stream()
                .filter(MappingJackson2XmlHttpMessageConverter.class::isInstance)
                .findFirst()
                .orElse(null);
        if (Objects.nonNull(xmlHttpMessageConverter)) {
            xmlHttpMessageConverter.setObjectMapper(xmlMapper);
        }

    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
        thymeleafViewResolver.setTemplateEngine(springTemplateEngine());
        thymeleafViewResolver.setCharacterEncoding("UTF-8");
        thymeleafViewResolver.setOrder(1);
        thymeleafViewResolver.setViewNames(new String[]{"*"});
        return thymeleafViewResolver;
    }

    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(springResourceTemplateResolver());
        return templateEngine;
    }

    public SpringResourceTemplateResolver springResourceTemplateResolver() {
        SpringResourceTemplateResolver springResourceTemplateResolver = new SpringResourceTemplateResolver();
        springResourceTemplateResolver.setApplicationContext(applicationContext);
        springResourceTemplateResolver.setCharacterEncoding("UTF-8");
        springResourceTemplateResolver.setPrefix("/WEB-INF/views/");
        springResourceTemplateResolver.setSuffix(".html");
        springResourceTemplateResolver.setCacheable(false);
        springResourceTemplateResolver.setTemplateMode(TemplateMode.HTML);
        return springResourceTemplateResolver;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(thymeleafViewResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.addRedirectViewController("/favicon.ico", "/resources/favicon.ico");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(true)
                .parameterName("format")
                .ignoreAcceptHeader(true)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthCheckInterceptor()).addPathPatterns("/api/**");
    }
}
