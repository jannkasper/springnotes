package com.springnotes.domain;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class Teacher implements ApplicationContextAware {

    @Autowired
    private ApplicationContext context;
    private List<Course> courses = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @PostConstruct
    public void postConstruct() {
        if (context.containsBean("math")) {
            Course math = context.getBean("math", Course.class);
            courses.add(math);
        }
        if (context.containsBean("physics")) {
            Course physics = context.getBean("physics", Course.class);
            courses.add(physics);
        }
    }



    public List<Course> getCourses() {
        return courses;
    }
}
