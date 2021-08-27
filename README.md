# Core Technologies

## 1. The IoC Container

### 1.1. Introduction to the Spring IoC Container and Beans
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

### 1.2. Container Overview
The `org.springframework.context.ApplicationContext` interface represents the Spring IoC container and is responsible for instantiating, configuring, and assembling the beans.
The configuration metadata is represented in XML, Java annotations, or Java code. 
It lets you express the objects that compose your application and the rich interdependencies between those objects.


The following diagram shows a high-level view of how Spring works.
Your application classes are combined with configuration metadata so that, after the ApplicationContext is created and initialized
![img.png](img.png)

#### 1.2.1. Configuration Metadata
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

### 1.3. Bean Overview
Bean definitions are represented as BeanDefinition objects, which contain (among other information) the following metadata:
* A package-qualified class name: typically, the actual implementation class of the bean being defined.
* Bean behavioral configuration elements, which state how the bean should behave in the container (scope, lifecycle callbacks, and so forth).
* References to other beans that are needed for the bean to do its work. These references are also called collaborators or dependencies.
* Other configuration settings to set in the newly created object — for example, the size limit of the pool or the number of connections to use in a bean that manages a connection pool.

This metadata translates to a set of properties that make up each bean definition. The following table describes these properties:
* Class
* Name
* Scope
* Constructor arguments
* Properties
* Autowiring mode
* Lazy initialization mode
* Initialization method
* Destruction method

#### 1.3.1 Naming Beans
Every bean has one or more identifiers. These identifiers must be unique within the container that hosts the bean. A bean usually has only one identifier.
In XML-based configuration metadata, you use the `id` attribute (alphanumeric) and the `name` attribute.
you can also specify them in the `name` attribute, separated by a comma (,), semicolon (;), or white space.
Motivations for not supplying a name are related to using inner beans and autowiring collaborators.

###### Aliasing a Bean outside the Bean Definition
It is sometimes desirable to introduce an alias for a bean that is defined elsewhere. This is commonly the case in large systems where configuration is split amongst each subsystem.

    <alias name="fromName" alias="toName"/>

#### 1.3.2 Instantiating Beans

A bean definition is essentially a recipe for creating one or more objects. The container looks at the recipe for a named bean when asked and uses the configuration metadata encapsulated by that bean definition to create (or acquire) an actual object.

You can use the Class property in one of two ways:
* to specify the bean class to be constructed
* to specify the actual class containing the static factory method that is invoked to create the object

Static nested class, they can be separated by a dollar sign ($) or a dot (.) `com.example.SomeThing$OtherThing`.

#### Instantiation with a Static Factory Method
Specify the class that contains the static factory method and an attribute named factory-method to specify the name of the factory method itself.

    <bean id="clientService"
    class="examples.ClientService"
    factory-method="createInstance"/>

#### Instantiation by Using an Instance Factory Method
To use this mechanism, leave the class attribute empty and, in the factory-bean attribute, specify the name of a bean in the current (or parent or ancestor) container.

    <!-- the factory bean, which contains a method called createInstance() -->
    <bean id="serviceLocator" class="examples.DefaultServiceLocator">
        <!-- inject any dependencies required by this locator bean -->
    </bean>
    
    <!-- the bean to be created via the factory bean -->
    <bean id="clientService"
    factory-bean="serviceLocator"
    factory-method="createClientServiceInstance"/>

#### Determining a Bean’s Runtime Type
The recommended way to find out about the actual runtime type of a particular bean is a BeanFactory.getType call for the specified bean name. This takes all of the above cases into account and returns the type of object that a BeanFactory.getBean call is going to return for the same bean name.

## 1.4 Dependencies
### 1.4.1. Dependency Injection
Dependency injection (DI) is a process whereby objects define their dependencies only through constructor arguments, arguments to a factory method, or properties that are set on the object instance after it is constructed or returned from a factory method.
The container then injects those dependencies when it creates the bean. 

#### Constructor Argument Resolution
Assuming that the ThingTwo and ThingThree classes are not related by inheritance, no potential ambiguity exists.

    <beans>
        <bean id="beanOne" class="x.y.ThingOne">
            <constructor-arg ref="beanTwo"/>
            <constructor-arg ref="beanThree"/>
        </bean>
    
        <bean id="beanTwo" class="x.y.ThingTwo"/>
    
        <bean id="beanThree" class="x.y.ThingThree"/>
    </beans>

Constructor argument type matching

    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg type="int" value="7500000"/>
        <constructor-arg type="java.lang.String" value="42"/>
    </bean>

Constructor argument index

    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg index="0" value="7500000"/>
        <constructor-arg index="1" value="42"/>
    </bean>

Constructor argument name

    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg name="years" value="7500000"/>
        <constructor-arg name="ultimateAnswer" value="42"/>
    </bean>
_

    package examples;
    
    public class ExampleBean {
    
        // Fields omitted
    
        @ConstructorProperties({"years", "ultimateAnswer"})
        public ExampleBean(int years, String ultimateAnswer) {
            this.years = years;
            this.ultimateAnswer = ultimateAnswer;
        }
    }

#### Setter-based Dependency Injection
Good rule of thumb to use constructors for mandatory dependencies and setter methods or configuration methods for optional dependencies.

The following example shows a class that can only be dependency-injected by using pure setter injection

Dependency Resolution Process
* The ApplicationContext is created and initialized with configuration metadata that describes all the beans
* Dependencies are provided to the bean, when the bean is actually created.
* Each property or constructor argument is an actual definition of the value to set, or a reference to another bean in the container.
* Each property or constructor argument that is a value is converted from its specified format to the actual type of that property or constructor argument. By default, Spring can convert a value supplied in string format to all built-in types, such as int, long, String, boolean, and so forth.

If you configure beans for classes A and B to be injected into each other, the Spring IoC container detects this circular reference at runtime, and throws a BeanCurrentlyInCreationException.


### 1.4.2. Dependencies and Configuration in Detail
Straight Values (Primitives, Strings, and so on)

    <bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <!-- results in a setDriverClassName(String) call -->
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/mydb"/>
        <property name="username" value="root"/>
        <property name="password" value="misterkaoli"/>
    </bean>
.

    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    https://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource"
            destroy-method="close"
            p:driverClassName="com.mysql.jdbc.Driver"
            p:url="jdbc:mysql://localhost:3306/mydb"
            p:username="root"
            p:password="misterkaoli"/>
    
    </beans>
#### The idref element
The idref element is simply an error-proof way to pass the id (a string value - not a reference) 

    <bean id="theTargetBean" class="..."/>
    
    <bean id="theClientBean" class="...">
        <property name="targetName">
            <idref bean="theTargetBean"/>
        </property>
    </bean>

.

    <bean id="theTargetBean" class="..." />
    
    <bean id="client" class="...">
        <property name="targetName" value="theTargetBean"/>
    </bean>

#### References to Other Beans (Collaborators)
The `ref` element is the final element inside a <constructor-arg/> or <property/>

    <!-- in the parent context -->
    <bean id="accountService" class="com.something.SimpleAccountService">
        <!-- insert dependencies as required as here -->
    </bean>
    
    <!-- in the child (descendant) context -->
    <bean id="accountService" <!-- bean name is the same as the parent bean -->
        class="org.springframework.aop.framework.ProxyFactoryBean">
        <property name="target">
        <ref parent="accountService"/> <!-- notice how we refer to the parent bean -->
        </property>
        <!-- insert other configuration and dependencies as required here -->
    </bean>

#### Inner Beans
A <bean/> element inside the <property/> or <constructor-arg/> elements defines an inner bean


    <bean id="outer" class="...">
        <!-- instead of using a reference to a target bean, simply define the target bean inline -->
        <property name="target">
            <bean class="com.example.Person"> <!-- this is the inner bean -->
                <property name="name" value="Fiona Apple"/>
                <property name="age" value="25"/>
            </bean>
        </property>
    </bean>

#### Collections
The <list/>, <set/>, <map/>, and <props/> elements

    <bean id="moreComplexObject" class="example.ComplexObject">
        <!-- results in a setAdminEmails(java.util.Properties) call -->
        <property name="adminEmails">
            <props>
                <prop key="administrator">administrator@example.org</prop>
                <prop key="support">support@example.org</prop>
                <prop key="development">development@example.org</prop>
            </props>
        </property>
        <!-- results in a setSomeList(java.util.List) call -->
        <property name="someList">
            <list>
                <value>a list element followed by a reference</value>
                <ref bean="myDataSource" />
            </list>
        </property>
        <!-- results in a setSomeMap(java.util.Map) call -->
        <property name="someMap">
            <map>
                <entry key="an entry" value="just some string"/>
                <entry key ="a ref" value-ref="myDataSource"/>
            </map>
        </property>
        <!-- results in a setSomeSet(java.util.Set) call -->
        <property name="someSet">
            <set>
                <value>just some string</value>
                <ref bean="myDataSource" />
            </set>
        </property>
    </bean>

The value of a map key or value, or a set value, can also be any of the following elements:
`bean | ref | idref | list | set | map | props | value | null
`

#### Collection Merging
The child Properties collection’s value set inherits all property elements from the parent <props/>, and the child’s value for the support value overrides the value in the parent collection.
    
    <beans>
        <bean id="parent" abstract="true" class="example.ComplexObject">
            <property name="adminEmails">
                <props>
                    <prop key="administrator">administrator@example.com</prop>
                    <prop key="support">support@example.com</prop>
                </props>
            </property>
        </bean>
        <bean id="child" parent="parent">
            <property name="adminEmails">
                <!-- the merge is specified on the child collection definition -->
                <props merge="true">
                    <prop key="sales">sales@example.com</prop>
                    <prop key="support">support@example.co.uk</prop>
                </props>
            </property>
        </bean>
    <beans>

#### Null and Empty String Values

    <bean class="ExampleBean">
        <property name="email" value=""/>
    </bean>
.

    <bean class="ExampleBean">
        <property name="email">
            <null/>
        </property>
    </bean>

#### XML Shortcut with the p-namespace

    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    https://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <bean name="john-classic" class="com.example.Person">
            <property name="name" value="John Doe"/>
            <property name="spouse" ref="jane"/>
        </bean>
    
        <bean name="john-modern"
            class="com.example.Person"
            p:name="John Doe"
            p:spouse-ref="jane"/>
    
        <bean name="jane" class="com.example.Person">
            <property name="name" value="Jane Doe"/>
        </bean>
    </beans>

#### XML Shortcut with the c-namespace
Allows inlined attributes for configuring the constructor arguments rather then nested constructor-arg elements.

    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:c="http://www.springframework.org/schema/c"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    https://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <bean id="beanTwo" class="x.y.ThingTwo"/>
        <bean id="beanThree" class="x.y.ThingThree"/>
    
        <!-- traditional declaration with optional argument names -->
        <bean id="beanOne" class="x.y.ThingOne">
            <constructor-arg name="thingTwo" ref="beanTwo"/>
            <constructor-arg name="thingThree" ref="beanThree"/>
            <constructor-arg name="email" value="something@somewhere.com"/>
        </bean>
    
        <!-- c-namespace declaration with argument names -->
        <bean id="beanOne" class="x.y.ThingOne" c:thingTwo-ref="beanTwo"
            c:thingThree-ref="beanThree" c:email="something@somewhere.com"/>
    
    </beans>

You can use fallback to the argument indexes, as follows:

    <!-- c-namespace index declaration -->
    <bean id="beanOne" class="x.y.ThingOne" c:_0-ref="beanTwo" c:_1-ref="beanThree"
    c:_2="something@somewhere.com"/>

#### Compound Property Names
The something bean has a fred property, which has a bob property, which has a sammy property, and that final sammy property is being set to a value of 123

    <bean id="something" class="things.ThingOne">
        <property name="fred.bob.sammy" value="123" />
    </bean>

### 1.4.3. Using depends-on
The depends-on attribute can explicitly force one or more beans to be initialized before the bean using this element is initialized.

    <bean id="beanOne" class="ExampleBean" depends-on="manager,accountDao">
        <property name="manager" ref="manager" />
    </bean>
    
    <bean id="manager" class="ManagerBean" />
    <bean id="accountDao" class="x.y.jdbc.JdbcAccountDao" />

### 1.4.4. Lazy-initialized Beans
All singleton beans as part of the initialization process. Generally, this pre-instantiation is desirable, because errors in the configuration or surrounding environment are discovered immediately.
A lazy-initialized bean tells the IoC container to create a bean instance when it is first requested.

    <bean id="lazy" class="com.something.ExpensiveToCreateBean" lazy-init="true"/>
    <bean name="not.lazy" class="com.something.AnotherBean"/>

However, when a lazy-initialized bean is a dependency of a singleton bean that is not lazy-initialized, the ApplicationContext creates the lazy-initialized bean at startup, because it must satisfy the singleton’s dependencies.

You can also control lazy-initialization at the container level 

    <beans default-lazy-init="true">
        <!-- no beans will be pre-instantiated... -->
    </beans>

### 1.4.5. Autowiring Collaborators









