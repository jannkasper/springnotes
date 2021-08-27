# Core Technologies

### 1. The IoC Container

###### 1.1. Introduction to the Spring IoC Container and Beans
Inversion of Control (IoC) - IoC is also known as dependency injection (DI). 
It is a process whereby objects define their dependencies (that is, the other objects they work with) 
only through constructor arguments, arguments to a factory method, or properties that are set on 
the object instance after it is constructed or returned from a factory method.
The container then injects those dependencies when it creates the bean.
This process is fundamentally the inverse (hence the name, Inversion of Control) 
of the bean itself controlling the instantiation or location of its dependencies 
by using direct construction of classes or a mechanism such as the Service Locator pattern.

The `org.springframework.beans` and `org.springframework.context` packages are the basis for Spring Framework’s IoC container.
`BeanFactory` provides the configuration framework and basic functionality, 
and the `ApplicationContext` adds more enterprise-specific functionality. 
The ApplicationContext is a complete superset of the BeanFactory and is used exclusively in this chapter in descriptions of Spring’s IoC container.

###### 1.2. Container Overview
The `org.springframework.context.ApplicationContext` interface represents the Spring IoC container and is responsible for instantiating, configuring, and assembling the beans.
The configuration metadata is represented in XML, Java annotations, or Java code. 
It lets you express the objects that compose your application and the rich interdependencies between those objects.


The following diagram shows a high-level view of how Spring works.
Your application classes are combined with configuration metadata so that, after the ApplicationContext is created and initialized
![img.png](img.png)

###### 1.2.1. Configuration Metadata
Configuration metadata is traditionally supplied in a simple and intuitive XML format.

XML-based configuration metadata configures these beans as `<bean/>` elements inside a top-level `<beans/>` element. 
Java configuration typically uses `@Bean`-annotated methods within a `@Configuration` class.

The following example shows the basic structure of XML-based configuration metadata:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    https://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <bean id="..." class="...">  
            <!-- collaborators and configuration for this bean go here -->
        </bean>
    
        <bean id="..." class="...">
            <!-- collaborators and configuration for this bean go here -->
        </bean>
    
        <!-- more bean definitions go here -->
    
    </beans>

Composing XML-based Configuration Metadata

    <beans>
    <import resource="services.xml"/>
    <import resource="resources/messageSource.xml"/>
    <import resource="/resources/themeSource.xml"/>
    
        <bean id="bean1" class="..."/>
        <bean id="bean2" class="..."/>
    </beans>

###### 1.2.3 Using the Container

Instantiating a Container

`ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");`

The ApplicationContext lets you read bean definitions and access them, as the following example shows:

    // create and configure beans
    ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
    
    // retrieve configured instance
    PetStoreService service = context.getBean("petStore", PetStoreService.class);
    
    // use configured instance
    List<String> userList = service.getUsernameList();

The most flexible variant is GenericApplicationContext in combination with reader delegates — for example, with XmlBeanDefinitionReader for XML files, as the following example shows:

    GenericApplicationContext context = new GenericApplicationContext();
    new XmlBeanDefinitionReader(context).loadBeanDefinitions("services.xml", "daos.xml");
    context.refresh();





