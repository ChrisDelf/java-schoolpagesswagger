package com.lambdaschool.school;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableJpaAuditing
@SpringBootApplication
public class SchoolApplication
{

    public static void main(String[] args)
    {

        ApplicationContext ctx = SpringApplication.run(SchoolApplication.class, args);

        // we have to disable dispatcherServlet , we have to find the bean to disable it
        // Spring cannot find the bean it would happily run anyways, so make sure you have it spelt correctly.
        DispatcherServlet dispatcher = (DispatcherServlet)ctx.getBean("dispatcherServlet");
        dispatcher.setThrowExceptionIfNoHandlerFound(true);// we are setting it so we handle it ourselves.


    }

}
