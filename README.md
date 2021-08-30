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

### 1.4 Dependencies
#### 1.4.1. Dependency Injection
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


#### 1.4.2. Dependencies and Configuration in Detail
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

#### 1.4.3. Using depends-on
The depends-on attribute can explicitly force one or more beans to be initialized before the bean using this element is initialized.

    <bean id="beanOne" class="ExampleBean" depends-on="manager,accountDao">
        <property name="manager" ref="manager" />
    </bean>
    
    <bean id="manager" class="ManagerBean" />
    <bean id="accountDao" class="x.y.jdbc.JdbcAccountDao" />

#### 1.4.4. Lazy-initialized Beans
All singleton beans as part of the initialization process. Generally, this pre-instantiation is desirable, because errors in the configuration or surrounding environment are discovered immediately.
A lazy-initialized bean tells the IoC container to create a bean instance when it is first requested.

    <bean id="lazy" class="com.something.ExpensiveToCreateBean" lazy-init="true"/>
    <bean name="not.lazy" class="com.something.AnotherBean"/>

However, when a lazy-initialized bean is a dependency of a singleton bean that is not lazy-initialized, the ApplicationContext creates the lazy-initialized bean at startup, because it must satisfy the singleton’s dependencies.

You can also control lazy-initialization at the container level 

    <beans default-lazy-init="true">
        <!-- no beans will be pre-instantiated... -->
    </beans>

#### 1.4.5. Autowiring Collaborators
You can let Spring resolve collaborators (other beans) automatically for your bean by inspecting the contents of the ApplicationContext.
* Autowiring can significantly reduce the need to specify properties or constructor arguments
* Autowiring can update a configuration as your objects evolve 
  
You can specify the autowire mode for a bean definition with the autowire attribute of the <bean/> element.

Autowiring modes:
* no - (Default) No autowiring.
* byName - Spring looks for a bean with the same name as the property that needs to be autowired. Bean definition is set to autowire by name and it contains a master property (that is, it has a setMaster(..) method), Spring looks for a bean definition named master and uses it to set the property.
* byType - Lets a property be autowired if exactly one bean of the property type exists in the container. If more than one exists, a fatal exception is thrown, which indicates that you may not use byType autowiring for that bean. If there are no matching beans, nothing happens (the property is not set).
* constructor - Analogous to byType but applies to constructor arguments. If there is not exactly one bean of the constructor argument type in the container, a fatal error is raised.

Limitations and Disadvantages of Autowiring:
* Explicit dependencies in property and constructor-arg settings always override autowiring. You cannot autowire simple properties such as primitives, Strings, and Classes (and arrays of such simple properties).
* Autowiring is less exact than explicit wiring.
* Wiring information may not be available to tools that may generate documentation from a Spring container.
* Multiple bean definitions within the container may match the type specified by the setter method or constructor argument to be autowired

Excluding a Bean from Autowiring
n Spring’s XML format, set the `autowire-candidate` attribute of the `<bean/>` element to `false`. 

#### 1.4.6. Method Injection
When a singleton bean needs to collaborate with another bean, problem arises when the bean lifecycles are different.
A solution is to forego some inversion of control. You can make bean A aware of the container by implementing the ApplicationContextAware interface, and by making a getBean("B") call to the container ask for (a typically new) bean B instance every time bean A needs it.

    public class CommandManager implements ApplicationContextAware {
    
        private ApplicationContext applicationContext;
    
        public Object process(Map commandState) {
            // grab a new instance of the appropriate Command
            Command command = createCommand();
            // set the state on the (hopefully brand new) Command instance
            command.setState(commandState);
            return command.execute();
        }
    
        protected Command createCommand() {
            // notice the Spring API dependency!
            return this.applicationContext.getBean("command", Command.class);
        }
    
        public void setApplicationContext(
                ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }

Lookup Method Injection

Lookup method injection is the ability of the container to override methods on container-managed beans and return the lookup result for another named bean in the container.

    public abstract class CommandManager {
    
        public Object process(Object commandState) {
            // grab a new instance of the appropriate Command interface
            Command command = createCommand();
            // set the state on the (hopefully brand new) Command instance
            command.setState(commandState);
            return command.execute();
        }
    
        // okay... but where is the implementation of this method?
        protected abstract Command createCommand();
    }
.

    <!-- a stateful bean deployed as a prototype (non-singleton) -->
    <bean id="myCommand" class="fiona.apple.AsyncCommand" scope="prototype">
        <!-- inject dependencies here as required -->
    </bean>
    
    <!-- commandProcessor uses statefulCommandHelper -->
    <bean id="commandManager" class="fiona.apple.CommandManager">
        <lookup-method name="createCommand" bean="myCommand"/>
    </bean>

Alternatively, within the annotation-based component model, you can declare a lookup method through the @Lookup annotation.

    public abstract class CommandManager {
    
        public Object process(Object commandState) {
            Command command = createCommand();
            command.setState(commandState);
            return command.execute();
        }
    
        @Lookup("myCommand")
        protected abstract Command createCommand();
    }

Arbitrary Method Replacement

With XML-based configuration metadata, you can use the replaced-method element to replace an existing method implementation with another, for a deployed bean. 

    <bean id="myValueCalculator" class="x.y.z.MyValueCalculator">
        <!-- arbitrary method replacement -->
        <replaced-method name="computeValue" replacer="replacementComputeValue">
            <arg-type>String</arg-type>
        </replaced-method>
    </bean>
    
    <bean id="replacementComputeValue" class="a.b.c.ReplacementComputeValue"/>

### 1.5 Bean scopes

* singleton
* prototype - Scopes a single bean definition to any number of object instances.
* request - Scopes a single bean definition to the lifecycle of a single HTTP request.
* session - Scopes a single bean definition to the lifecycle of an HTTP Session.
* application - Scopes a single bean definition to the lifecycle of a ServletContext.
* websocket - Scopes a single bean definition to the lifecycle of a WebSocket.

#### 1.5.4. Request, Session, Application, and WebSocket Scopes
The request, session, application, and websocket scopes are available only if you use a web-aware Spring ApplicationContext implementation (such as XmlWebApplicationContext). If you use these scopes with regular Spring IoC containers, such as the ClassPathXmlApplicationContext, an IllegalStateException that complains about an unknown bean scope is thrown.

##### Initial Web Configuration

To support the scoping of beans at the request, session, application, and websocket levels (web-scoped beans), some minor initial configuration is required before you define your beans.

If you access scoped beans within Spring Web MVC, in effect, within a request that is processed by the Spring DispatcherServlet, no special setup is necessary.

For Servlet 3.0+, this can be done programmatically by using the WebApplicationInitializer interface.

DispatcherServlet, RequestContextListener, and RequestContextFilter all do exactly the same thing, namely bind the HTTP request object to the Thread that is servicing that request. 

##### Application Scope
This is somewhat similar to a Spring singleton bean but differs in two important ways: It is a singleton per ServletContext, not per Spring 'ApplicationContext' (for which there may be several in any given web application), and it is actually exposed and therefore visible as a ServletContext attribute.

##### Scoped Beans as Dependencies
If you want to inject (for example) an HTTP request-scoped bean into another bean of a longer-lived scope, you may choose to inject an AOP proxy in place of the scoped bean.

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    https://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/aop
    https://www.springframework.org/schema/aop/spring-aop.xsd">
    
        <!-- an HTTP Session-scoped bean exposed as a proxy -->
        <bean id="userPreferences" class="com.something.UserPreferences" scope="session">
            <!-- instructs the container to proxy the surrounding bean -->
            <aop:scoped-proxy/> 
        </bean>
    
        <!-- a singleton-scoped bean injected with a proxy to the above bean -->
        <bean id="userService" class="com.something.SimpleUserService">
            <!-- a reference to the proxied userPreferences bean -->
            <property name="userPreferences" ref="userPreferences"/>
        </bean>
    </beans>

#### 1.5.5. Custom Scopes
The bean scoping mechanism is extensible. You can define your own scopes or even redefine existing scopes, although the latter is considered bad practice and you cannot override the built-in singleton and prototype scopes.

The Scope interface has four methods to get objects from the scope, remove them from the scope, and let them be destroyed.

After you write and test one or more custom Scope implementations, you need to make the Spring container aware of your new scopes.
This method is declared on the ConfigurableBeanFactory interface, which is available through the BeanFactory property on most of the concrete ApplicationContext implementations that ship with Spring.

    Scope threadScope = new SimpleThreadScope();
    beanFactory.registerScope("thread", threadScope);
.

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    https://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/aop
    https://www.springframework.org/schema/aop/spring-aop.xsd">
    
        <bean class="org.springframework.beans.factory.config.CustomScopeConfigurer">
            <property name="scopes">
                <map>
                    <entry key="thread">
                        <bean class="org.springframework.context.support.SimpleThreadScope"/>
                    </entry>
                </map>
            </property>
        </bean>
    
        <bean id="thing2" class="x.y.Thing2" scope="thread">
            <property name="name" value="Rick"/>
            <aop:scoped-proxy/>
        </bean>
    
        <bean id="thing1" class="x.y.Thing1">
            <property name="thing2" ref="thing2"/>
        </bean>
    
    </beans>

### 1.6. Customizing the Nature of a Bean
#### 1.6.1. Lifecycle Callbacks
To interact with the container’s management of the bean lifecycle, you can implement the Spring InitializingBean and DisposableBean interfaces.

##### Initialization Callbacks
The org.springframework.beans.factory.InitializingBean interface lets a bean perform initialization work after the container has set all necessary properties on the bean

    <bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>
.

    public class ExampleBean {
    
        public void init() {
            // do some initialization work
        }
    }

or

    <bean id="exampleInitBean" class="examples.AnotherExampleBean"/>
.

    public class AnotherExampleBean implements InitializingBean {
    
        @Override
        public void afterPropertiesSet() {
            // do some initialization work
        }
    }


##### Destruction Callbacks

    void destroy() throws Exception;    
.

    <bean id="exampleInitBean" class="examples.ExampleBean" destroy-method="cleanup"/>

or

    <bean id="exampleInitBean" class="examples.AnotherExampleBean"/>
.

    public class AnotherExampleBean implements DisposableBean {
    
        @Override
        public void destroy() {
            // do some destruction work (like releasing pooled connections)
        }
    }

##### Default Initialization and Destroy Methods
    <beans default-init-method="init">
    
        <bean id="blogService" class="com.something.DefaultBlogService">
            <property name="blogDao" ref="blogDao" />
        </bean>
    
    </beans>

##### Combining Lifecycle Mechanisms
You have three options for controlling bean lifecycle behavior:
* The InitializingBean and DisposableBean callback interfaces
* Custom init() and destroy() methods
* The @PostConstruct and @PreDestroy annotations. You can combine these mechanisms to control a given bean.

Multiple lifecycle mechanisms configured for the same bean, with different initialization methods, are called as follows:
* Methods annotated with @PostConstruct
* afterPropertiesSet() as defined by the InitializingBean callback interface
* A custom configured init() method

Destroy methods are called in the same order:
* Methods annotated with @PreDestroy
* destroy() as defined by the DisposableBean callback interface
* A custom configured destroy() method

##### Startup and Shutdown Callbacks
Any Spring-managed object may implement the Lifecycle interface. Then, when the ApplicationContext itself receives start and stop signals (for example, for a stop/restart scenario at runtime), it cascades those calls to all Lifecycle implementations defined within that context. 

You may only know that objects of a certain type should start prior to objects of another type. In those cases, the SmartLifecycle interface defines another option, namely the getPhase() method as defined on its super-interface, Phased.
SmartLifecycle and whose getPhase() method returns Integer.MIN_VALUE would be among the first to start and the last to stop.

##### Shutting Down the Spring IoC Container Gracefully in Non-Web Applications
To register a shutdown hook, call the registerShutdownHook() method that is declared on the ConfigurableApplicationContext interface.

#### 1.6.2. ApplicationContextAware and BeanNameAware
Object instance that implements the org.springframework.context.ApplicationContextAware interface, the instance is provided with a reference to that ApplicationContext can programmatically manipulate the ApplicationContext.

#### 1.6.3. Other Aware Interfaces
Spring offers a wide range of Aware callback interfaces that let beans indicate to the container that they require a certain infrastructure dependency
* ApplicationContextAware
* ApplicationEventPublisherAware
* BeanClassLoaderAware
* BeanFactoryAware
* BeanNameAware
* LoadTimeWeaverAware
* MessageSourceAware
* NotificationPublisherAware
* ResourceLoaderAware
* ServletConfigAware
* ServletContextAware

### 1.7. Bean Definition Inheritance
A child bean definition inherits configuration data from a parent definition

    <bean id="inheritedTestBean" abstract="true"
        class="org.springframework.beans.TestBean">
        <property name="name" value="parent"/>
        <property name="age" value="1"/>
    </bean>
    
    <bean id="inheritsWithDifferentClass"
        class="org.springframework.beans.DerivedTestBean"
        parent="inheritedTestBean" init-method="initialize">  
        <property name="name" value="override"/>
        <!-- the age property value of 1 will be inherited from parent -->
    </bean>

### 1.8. Container Extension Points
The Spring IoC container can be extended by plugging in implementations of special integration interfaces.

#### 1.8.1. Customizing Beans by Using a BeanPostProcessor
The BeanPostProcessor interface defines callback methods that you can implement to provide your own (or override the container’s default) instantiation logic, dependency resolution logic, and so forth.
You can configure multiple BeanPostProcessor instances, and you can control the order in which these BeanPostProcessor instances run by setting the order property.

    public class InstantiationTracingBeanPostProcessor implements BeanPostProcessor {
    
        // simply return the instantiated bean as-is
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            return bean; // we could potentially return any object reference here...
        }
    
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            System.out.println("Bean '" + beanName + "' created : " + bean.toString());
            return bean;
        }
    }

#### 1.8.2. Customizing Configuration Metadata with a BeanFactoryPostProcessor
BeanFactoryPostProcessor read the configuration metadata and potentially change it before the container instantiates any beans other than BeanFactoryPostProcessor instances.

    <bean class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer">
        <property name="locations" value="classpath:com/something/jdbc.properties"/>
    </bean>
    
    <bean id="dataSource" destroy-method="close"
        class="org.apache.commons.dbcp.BasicDataSource">
        <property name="driverClassName" value="${jdbc.driverClassName}"/>
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

#### 1.8.3. Customizing Instantiation Logic with a FactoryBean
The FactoryBean<T> interface provides three methods:
* T getObject(): Returns an instance of the object this factory creates. The instance can possibly be shared, depending on whether this factory returns singletons or prototypes.
* boolean isSingleton(): Returns true if this FactoryBean returns singletons or false otherwise. The default implementation of this method returns true.
* Class<?> getObjectType(): Returns the object type returned by the getObject() method or null if the type is not known in advance.

### 1.9. Annotation-based Container Configuration
* @Required
* @Autowired
* @Primary
* @Qualifiers
* @Resource
* @Value
* @PostConstruct
* @PreDestroy

### 1.10. Classpath Scanning and Managed Components
#### 1.10.1. @Component and Further Stereotype Annotations

#### 1.10.3. Automatically Detecting Classes and Registering Bean Definitions
To autodetect these classes and register the corresponding beans, you need to add @ComponentScan to your @Configuration class, where the basePackages attribute is a common parent package for the two classes. 

    @Configuration
    @ComponentScan(basePackages = "org.example")
    public class AppConfig  {
    // ...
    }

#### 1.10.4. Using Filters to Customize Scanning
* annotation (default)
* assignable
* aspectj
* regex
* custom


    @Configuration
    @ComponentScan(basePackages = "org.example",
    includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*Stub.*Repository"),
    excludeFilters = @Filter(Repository.class))
    public class AppConfig {
    ...
    }

#### 1.10.5. Defining Bean Metadata within Components

    @Component
    public class FactoryMethodComponent {
    
        @Bean
        @Qualifier("public")
        public TestBean publicInstance() {
            return new TestBean("publicInstance");
        }
    
        public void doWork() {
            // Component method implementation omitted
        }
    }

#### 1.10.6. Naming Autodetected Components

    @Service("myMovieLister")
    public class SimpleMovieLister {
    // ...
    }

#### 1.10.7. Providing a Scope for Autodetected Components

    @Scope("prototype")
    @Repository
    public class MovieFinderImpl implements MovieFinder {
    // ...
    }

### 1.11. Using JSR 330 Standard Annotations
#### 1.11.1. Dependency Injection with @Inject and @Named
Instead of @Autowired, you can use @javax.inject.Inject as follows:


    import javax.inject.Inject;
    
    public class SimpleMovieLister {
    
        private MovieFinder movieFinder;
    
        @Inject
        public void setMovieFinder(MovieFinder movieFinder) {
            this.movieFinder = movieFinder;
        }
    
        public void listMovies() {
            this.movieFinder.findMovies(...);
            // ...
        }
    }

#### 1.11.2. @Named and @ManagedBean: Standard Equivalents to the @Component Annotation
Instead of @Component, you can use @javax.inject.Named or javax.annotation.ManagedBean, as the following example shows:

    import javax.inject.Inject;
    import javax.inject.Named;
    
    @Named("movieListener")  // @ManagedBean("movieListener") could be used as well
    public class SimpleMovieLister {
    
        private MovieFinder movieFinder;
    
        @Inject
        public void setMovieFinder(MovieFinder movieFinder) {
            this.movieFinder = movieFinder;
        }
    
        // ...
    }

### 1.12. Java-based Container Configuration
#### 1.12.1. Basic Concepts: @Bean and @Configuration

    @Configuration
    public class AppConfig {
    
        @Bean
        public MyService myService() {
            return new MyServiceImpl();
        }
    }

The preceding AppConfig class is equivalent to the following Spring <beans/> XML.

#### 1.12.2. Instantiating the Spring Container by Using AnnotationConfigApplicationContext
This versatile ApplicationContext implementation is capable of accepting not only @Configuration classes as input but also plain @Component classes and classes annotated with JSR-330 metadata.

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MyServiceImpl.class, Dependency1.class, Dependency2.class);
        MyService myService = ctx.getBean(MyService.class);
        myService.doStuff();
    }

You can instantiate an AnnotationConfigApplicationContext by using a no-arg constructor and then configure it by using the register() method.

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class, OtherConfig.class);
        ctx.register(AdditionalConfig.class);
        ctx.refresh();
        MyService myService = ctx.getBean(MyService.class);
        myService.doStuff();
    }

#### 1.12.3. Using the @Bean Annotation
@Bean is a method-level annotation and a direct analog of the XML <bean/> element.

    @Configuration
    public class AppConfig {
    
        @Bean
        public TransferServiceImpl transferService() {
            return new TransferServiceImpl();
        }
    }

Bean Dependencies

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }

Receiving Lifecycle Callbacks

Specifying Bean Scope

Customizing Bean Naming

    @Bean(name = "myThing")
    public Thing thing() {
        return new Thing();
    }

Bean Aliasing

    @Bean({"dataSource", "subsystemA-dataSource", "subsystemB-dataSource"})
    public DataSource dataSource() {
        // instantiate, configure and return DataSource bean...
    }

Bean Description

    @Bean
    @Description("Provides a basic example of a bean")
    public Thing thing() {
        return new Thing();
    }

#### 1.12.4. Using the @Configuration annotation
Injecting Inter-bean Dependencies

This method of declaring inter-bean dependencies works only when the @Bean method is declared within a @Configuration class.

    @Configuration
    public class AppConfig {
    
        @Bean
        public BeanOne beanOne() {
            return new BeanOne(beanTwo());
        }
    
        @Bean
        public BeanTwo beanTwo() {
            return new BeanTwo();
        }
    }

Further Information About How Java-based Configuration Works Internally

clientDao() has been called once in clientService1() and once in clientService2().
This is where the magic comes in: All @Configuration classes are subclassed at startup-time with CGLIB. In the subclass, the child method checks the container first for any cached (scoped) beans before it calls the parent method and creates a new instance.

    @Configuration
    public class AppConfig {
    
        @Bean
        public ClientService clientService1() {
            ClientServiceImpl clientService = new ClientServiceImpl();
            clientService.setClientDao(clientDao());
            return clientService;
        }
    
        @Bean
        public ClientService clientService2() {
            ClientServiceImpl clientService = new ClientServiceImpl();
            clientService.setClientDao(clientDao());
            return clientService;
        }
    
        @Bean
        public ClientDao clientDao() {
            return new ClientDaoImpl();
        }
    }

#### 1.12.5. Composing Java-based Configurations
Using the @Import Annotation

    @Configuration
    public class ConfigA {
    
        @Bean
        public A a() {
            return new A();
        }
    }
    
    @Configuration
    @Import(ConfigA.class)
    public class ConfigB {
    
        @Bean
        public B b() {
            return new B();
        }
    }

@Configuration Class-centric Use of XML with @ImportResource

    @Configuration
    @ImportResource("classpath:/com/acme/properties-config.xml")
    public class AppConfig {
    
        @Value("${jdbc.url}")
        private String url;
    
        @Value("${jdbc.username}")
        private String username;
    
        @Value("${jdbc.password}")
        private String password;
    
        @Bean
        public DataSource dataSource() {
            return new DriverManagerDataSource(url, username, password);
        }
    }

### 1.13. Environment Abstraction
Two key aspects of the application environment: profiles and properties.

#### 1.13.1. Bean Definition Profiles
Bean definition profiles provide a mechanism in the core container that allows for registration of different beans in different environments.

    @Configuration
    @Profile("development")
    public class StandaloneDataConfig {
    
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:com/bank/config/sql/schema.sql")
                .addScript("classpath:com/bank/config/sql/test-data.sql")
                .build();
        }
    }
.

    @Configuration
    @Profile("production")
    public class JndiDataConfig {
    
        @Bean(destroyMethod="")
        public DataSource dataSource() throws Exception {
            Context ctx = new InitialContext();
            return (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
        }
    }

You can use @Profile as a meta-annotation for the purpose of creating a custom composed annotation.

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Profile("production")
    public @interface Production {
    }

@Profile can also be declared at the method level

    @Configuration
    public class AppConfig {
    
        @Bean("dataSource")
        @Profile("development") 
        public DataSource standaloneDataSource() {
            return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:com/bank/config/sql/schema.sql")
                .addScript("classpath:com/bank/config/sql/test-data.sql")
                .build();
        }
    
        @Bean("dataSource")
        @Profile("production") 
        public DataSource jndiDataSource() throws Exception {
            Context ctx = new InitialContext();
            return (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
        }
    }

Activating a Profile

    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.getEnvironment().setActiveProfiles("development");
    ctx.register(SomeConfig.class, StandaloneDataConfig.class, JndiDataConfig.class);
    ctx.refresh();

#### 1.13.2. PropertySource Abstraction

We see a high-level way of asking Spring whether the my-property property is defined for the current environment.

    ApplicationContext ctx = new GenericApplicationContext();
    Environment env = ctx.getEnvironment();
    boolean containsMyProperty = env.containsProperty("my-property");
    System.out.println("Does my environment contain the 'my-property' property? " + containsMyProperty);

#### 1.13.3. Using @PropertySource

    @Configuration
    @PropertySource("classpath:/com/myco/app.properties")
    public class AppConfig {
    
        @Autowired
        Environment env;
    
        @Bean
        public TestBean testBean() {
            TestBean testBean = new TestBean();
            testBean.setName(env.getProperty("testbean.name"));
            return testBean;
        }
    }

### 1.14. Registering a LoadTimeWeaver
The LoadTimeWeaver is used by Spring to dynamically transform classes as they are loaded into the Java virtual machine (JVM).

### 1.15. Additional Capabilities of the ApplicationContext
To enhance BeanFactory functionality in a more framework-oriented style, the context package also provides the following functionality:

* Access to messages in i18n-style, through the `MessageSource` interface.
* Access to resources, such as URLs and files, through the `ResourceLoader` interface.
* Event publication, namely to beans that implement the `ApplicationListener` interface, through the use of the `ApplicationEventPublisher` interface.
* Loading of multiple (hierarchical) contexts, letting each be focused on one particular layer, such as the web layer of an application, through the `HierarchicalBeanFactory` interface.

#### 1.15.1. Internationalization using MessageSource
The ApplicationContext interface extends an interface called MessageSource and, therefore, provides internationalization (“i18n”) functionality.

    <beans>
        <bean id="messageSource"
                class="org.springframework.context.support.ResourceBundleMessageSource">
            <property name="basenames">
                <list>
                    <value>format</value>
                    <value>exceptions</value>
                    <value>windows</value>
                </list>
            </property>
        </bean>
    </beans>
.

    # in format.properties
    message=Alligators rock!

    # in exceptions.properties
    argument.required=The {0} argument is required.

    public static void main(String[] args) {
        MessageSource resources = new ClassPathXmlApplicationContext("beans.xml");
        String message = resources.getMessage("message", null, "Default", Locale.ENGLISH);
        System.out.println(message);
    }

    Output: Alligators rock!

#### 1.15.2. Standard and Custom Events
* ContextRefreshedEvent
* ContextStartedEvent
* ContextStoppedEvent
* ContextClosedEvent
* RequestHandledEvent
* ServletRequestHandledEvent

You can also create and publish your own custom events. The following example shows a simple class that extends Spring’s ApplicationEvent base class:

    public class BlockedListEvent extends ApplicationEvent {
    
        private final String address;
        private final String content;
    
        public BlockedListEvent(Object source, String address, String content) {
            super(source);
            this.address = address;
            this.content = content;
        }
    
        // accessor and other methods...
    }

To publish a custom ApplicationEvent, call the publishEvent() method on an ApplicationEventPublisher.

    publisher.publishEvent(new BlockedListEvent(this, address, content));

To receive the custom ApplicationEvent, you can create a class that implements ApplicationListener and register it as a Spring bean. The following example shows such a class:

    public class BlockedListNotifier implements ApplicationListener<BlockedListEvent> {
    
        private String notificationAddress;
    
        public void setNotificationAddress(String notificationAddress) {
            this.notificationAddress = notificationAddress;
        }
    
        public void onApplicationEvent(BlockedListEvent event) {
            // notify appropriate parties via notificationAddress...
        }
    }

Annotation-based Event Listeners

You can register an event listener on any method of a managed bean by using the @EventListener annotation. 

    public class BlockedListNotifier {
    
        private String notificationAddress;
    
        public void setNotificationAddress(String notificationAddress) {
            this.notificationAddress = notificationAddress;
        }
    
        @EventListener
        public void processBlockedListEvent(BlockedListEvent event) {
            // notify appropriate parties via notificationAddress...
        }
    }
.

    @EventListener({ContextStartedEvent.class, ContextRefreshedEvent.class})
    public void handleContextStart() {
        // ...
    }
.

    @EventListener(condition = "#blEvent.content == 'my-event'")
    public void processBlockedListEvent(BlockedListEvent blEvent) {
        // notify appropriate parties via notificationAddress...
    }

Asynchronous Listeners

    @EventListener
    @Async
    public void processBlockedListEvent(BlockedListEvent event) {
        // BlockedListEvent is processed in a separate thread
    }

Ordering Listeners

    @EventListener
    @Order(42)
    public void processBlockedListEvent(BlockedListEvent event) {
        // notify appropriate parties via notificationAddress...
    }

Generic Events

    @EventListener
    public void onPersonCreated(EntityCreatedEvent<Person> event) {
        // ...
    }

#### 1.15.3. Convenient Access to Low-level Resources
You can configure a bean deployed into the application context to implement the special callback interface, `ResourceLoaderAware`, to be automatically called back at initialization time with the application context itself passed in as the `ResourceLoader`. 

#### 1.15.4. Application Startup Tracking
The AbstractApplicationContext (and its subclasses) is instrumented with an ApplicationStartup, which collects StartupStep data about various startup phases:
* application context lifecycle (base packages scanning, config classes management)
* beans lifecycle (instantiation, smart initialization, post processing)
* application events processing

Here is an example of instrumentation in the AnnotationConfigApplicationContext:

    // create a startup step and start recording
    StartupStep scanPackages = this.getApplicationStartup().start("spring.context.base-packages.scan");
    // add tagging information to the current step
    scanPackages.tag("packages", () -> Arrays.toString(basePackages));
    // perform the actual phase we're instrumenting
    this.scanner.scan(basePackages);
    // end the current step
    scanPackages.end();

#### 1.15.5. Convenient ApplicationContext Instantiation for Web Applications
You can register an ApplicationContext by using the ContextLoaderListener, as the following example shows:

    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/daoContext.xml /WEB-INF/applicationContext.xml</param-value>
    </context-param>
    
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

#### 1.15.6. Deploying a Spring ApplicationContext as a Java EE RAR File

### 1.16. The BeanFactory
#### 1.16.1. BeanFactory or ApplicationContext?


| Feature | BeanFactory | ApplicationContext |
| ------------- | ------------- | ------------- |
| Bean instantiation/wiring | Yes | Yes |
| Integrated lifecycle management | No | Yes |
| Automatic BeanPostProcessor registration | No | Yes |
| Automatic BeanFactoryPostProcessor registration | No | Yes |
| Convenient MessageSource access (for internalization) | No | Yes |
| Built-in ApplicationEvent publication mechanism | No | Yes |

## 2. Resources
### 2.1. Introduction
Java’s standard java.net.URL class and standard handlers for various URL prefixes, unfortunately, are not quite adequate enough for all access to low-level resources.

### 2.2. The Resource Interface
Spring’s Resource interface located in the org.springframework.core.io. package is meant to be a more capable interface for abstracting access to low-level resources. 

    public interface Resource extends InputStreamSource {
    
        boolean exists();
    
        boolean isReadable();
    
        boolean isOpen();
    
        boolean isFile();
    
        URL getURL() throws IOException;
    
        URI getURI() throws IOException;
    
        File getFile() throws IOException;
    
        ReadableByteChannel readableChannel() throws IOException;
    
        long contentLength() throws IOException;
    
        long lastModified() throws IOException;
    
        Resource createRelative(String relativePath) throws IOException;
    
        String getFilename();
    
        String getDescription();
    }

Some of the most important methods from the Resource interface are:
* getInputStream(): Locates and opens the resource, returning an InputStream for reading from the resource. It is expected that each invocation returns a fresh InputStream. It is the responsibility of the caller to close the stream.
* exists(): Returns a boolean indicating whether this resource actually exists in physical form.
* isOpen(): Returns a boolean indicating whether this resource represents a handle with an open stream. If true, the InputStream cannot be read multiple times and must be read once only and then closed to avoid resource leaks. Returns false for all usual resource implementations, with the exception of InputStreamResource.
* getDescription(): Returns a description for this resource, to be used for error output when working with the resource. This is often the fully qualified file name or the actual URL of the resource.

### 2.3. Built-in Resource Implementations
* UrlResource - wraps a java.net.URL
* ClassPathResource - Resource implementation supports resolution as a java.io.File
* FileSystemResource - Resource implementation for java.io.File
* PathResource - Resource implementation for java.nio.file.Path
* ServletContextResource - Resource implementation for ServletContext
* InputStreamResource - Resource implementation for a given InputStream.
* ByteArrayResource - Resource implementation for a given byte array

### 2.4. The ResourceLoader Interface
Against a ClassPathXmlApplicationContext, that code returns a ClassPathResource. If the same method were run against a FileSystemXmlApplicationContext instance, it would return a FileSystemResource. For a WebApplicationContext, it would return a ServletContextResource

    Resource template = ctx.getResource("some/resource/path/myTemplate.txt");

    Resource template = ctx.getResource("classpath:some/resource/path/myTemplate.txt");

    Resource template = ctx.getResource("file:///some/resource/path/myTemplate.txt");

    Resource template = ctx.getResource("https://myhost.com/resource/path/myTemplate.txt");

| Prefix | Example | Explanation |
| ------------- | ------------- | ------------- |
| classpath: | classpath:com/myapp/config.xml | Loaded from the classpath. |
| file: | file:///data/config.xml | Loaded as a URL from the filesystem. See also FileSystemResource Caveats. |
| https: | https://myserver/logo.png | Loaded as a URL. |
| (none) | /data/config.xml | Depends on the underlying ApplicationContext. |

### 2.5. The ResourcePatternResolver Interface
The ResourcePatternResolver interface is an extension to the ResourceLoader interface which defines a strategy for resolving a location pattern (for example, an Ant-style path pattern) into Resource objects.

    public interface ResourcePatternResolver extends ResourceLoader {
    
        String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
    
        Resource[] getResources(String locationPattern) throws IOException;
    }

### 2.6. The ResourceLoaderAware Interface
The ResourceLoaderAware interface is a special callback interface which identifies components that expect to be provided a ResourceLoader reference.

    public interface ResourceLoaderAware {
    
        void setResourceLoader(ResourceLoader resourceLoader);
    }

### 2.7. Resources as Dependencies

    package example;
    
    public class MyBean {
    
        private Resource template;
    
        public setTemplate(Resource template) {
            this.template = template;
        }
    
        // ...
    }

    <bean id="myBean" class="example.MyBean">
        <property name="template" value="some/resource/path/myTemplate.txt"/>
    </bean>
.

    @Component
    public class MyBean {
    
        private final Resource template;
    
        public MyBean(@Value("${template.path}") Resource template) {
            this.template = template;
        }
    
        // ...
    }
.

    @Component
    public class MyBean {
    
        private final Resource[] templates;
    
        public MyBean(@Value("${templates.path}") Resource[] templates) {
            this.templates = templates;
        }
    
        // ...
    }

## 2.8. Application Contexts and Resource Paths
### 2.8.1. Constructing Application Contexts

    ApplicationContext ctx = new ClassPathXmlApplicationContext("conf/appContext.xml");

    ApplicationContext ctx = new FileSystemXmlApplicationContext("conf/appContext.xml");

    ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:conf/appContext.xml");

    ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"services.xml", "repositories.xml"}, MessengerService.class);

### 2.8.3. FileSystemResource Caveats
A FileSystemResource that is not attached to a FileSystemApplicationContext (that is, when a FileSystemApplicationContext is not the actual ResourceLoader) treats absolute and relative paths as you would expect.

    ApplicationContext ctx = new FileSystemXmlApplicationContext("conf/context.xml");
    equivalent:
    ApplicationContext ctx = new FileSystemXmlApplicationContext("/conf/context.xml");

## 3. Validation, Data Binding, and Type Conversion
### 3.1. Validation by Using Spring’s Validator Interface

    public class Person {
    
        private String name;
        private int age;
    
        // the usual getters and setters...
    }

The next example provides validation behavior for the Person class by implementing the following two methods of the org.springframework.validation.Validator interface:
* supports(Class): Can this Validator validate instances of the supplied Class?
* validate(Object, org.springframework.validation.Errors): Validates the given object and, in case of validation errors, registers those with the given Errors object.

    public class PersonValidator implements Validator {
    
        /**
         * This Validator validates only Person instances
         */
        public boolean supports(Class clazz) {
            return Person.class.equals(clazz);
        }
    
        public void validate(Object obj, Errors e) {
            ValidationUtils.rejectIfEmpty(e, "name", "name.empty");
            Person p = (Person) obj;
            if (p.getAge() < 0) {
                e.rejectValue("age", "negativevalue");
            } else if (p.getAge() > 110) {
                e.rejectValue("age", "too.darn.old");
            }
        }
    }

### 3.2. Resolving Codes to Error Messages
The MessageCodesResolver determines which error codes the Errors interface registers. By default, the DefaultMessageCodesResolver is used, which (for example) not only registers a message with the code you gave but also registers messages that include the field name you passed to the reject method.

### 3.3. Bean Manipulation and the BeanWrapper
The way the BeanWrapper works is partly indicated by its name: it wraps a bean to perform actions on that bean, such as setting and retrieving properties.

#### 3.3.1. Setting and Getting Basic and Nested Properties
Setting and getting properties is done through the setPropertyValue and getPropertyValue overloaded method variants of BeanWrapper.

| Expression | Explanation |
| ---- | ---- |
| name | Indicates the property name that corresponds to the getName() or isName() and setName(..) methods. |
| account.name |Indicates the nested property name of the property account that corresponds to (for example) the getAccount().setName() or getAccount().getName() methods. |
| account[2] | Indicates the third element of the indexed property account. Indexed properties can be of type array, list, or other naturally ordered collection. |
| account[COMPANYNAME] | Indicates the value of the map entry indexed by the COMPANYNAME key of the account Map property. |

    BeanWrapper company = new BeanWrapperImpl(new Company());
    // setting the company name..
    company.setPropertyValue("name", "Some Company Inc.");
    // ... can also be done like this:
    PropertyValue value = new PropertyValue("name", "Some Company Inc.");
    company.setPropertyValue(value);
    
    // ok, let's create the director and tie it to the company:
    BeanWrapper jim = new BeanWrapperImpl(new Employee());
    jim.setPropertyValue("name", "Jim Stravinsky");
    company.setPropertyValue("managingDirector", jim.getWrappedInstance());
    
    // retrieving the salary of the managingDirector through the company
    Float salary = (Float) company.getPropertyValue("managingDirector.salary");

#### 3.3.2. Built-in PropertyEditor Implementations
Spring uses the concept of a PropertyEditor to effect the conversion between an Object and a String.

A couple of examples where property editing is used in Spring:
* Setting properties on beans is done by using PropertyEditor implementations. When you use String as the value of a property of some bean that you declare in an XML file, Spring (if the setter of the corresponding property has a Class parameter) uses ClassEditor to try to resolve the parameter to a Class object.
* Parsing HTTP request parameters in Spring’s MVC framework is done by using all kinds of PropertyEditor implementations that you can manually bind in all subclasses of the CommandController.


    public class SomethingBeanInfo extends SimpleBeanInfo {
    
        public PropertyDescriptor[] getPropertyDescriptors() {
            try {
                final PropertyEditor numberPE = new CustomNumberEditor(Integer.class, true);
                PropertyDescriptor ageDescriptor = new PropertyDescriptor("age", Something.class) {
                    @Override
                    public PropertyEditor createPropertyEditor(Object bean) {
                        return numberPE;
                    }
                };
                return new PropertyDescriptor[] { ageDescriptor };
            }
            catch (IntrospectionException ex) {
                throw new Error(ex.toString());
            }
        }
    }

Registering Additional Custom PropertyEditor Implementations

    <bean class="org.springframework.beans.factory.config.CustomEditorConfigurer">
        <property name="customEditors">
            <map>
                <entry key="example.ExoticType" value="example.ExoticTypeEditor"/>
            </map>
        </property>
    </bean>

Using PropertyEditorRegistrar

    package com.foo.editors.spring;
    
    public final class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar {
    
        public void registerCustomEditors(PropertyEditorRegistry registry) {
    
            // it is expected that new PropertyEditor instances are created
            registry.registerCustomEditor(ExoticType.class, new ExoticTypeEditor());
    
            // you could register as many custom property editors as are required here...
        }
    }

### 3.4. Spring Type Conversion
#### 3.4.1. Converter SPI

    package org.springframework.core.convert.support;
    
    final class StringToInteger implements Converter<String, Integer> {
    
        public Integer convert(String source) {
            return Integer.valueOf(source);
        }
    }

#### 3.4.2. Using ConverterFactory

    package org.springframework.core.convert.support;
    
    final class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {
    
        public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
            return new StringToEnumConverter(targetType);
        }
    
        private final class StringToEnumConverter<T extends Enum> implements Converter<String, T> {
    
            private Class<T> enumType;
    
            public StringToEnumConverter(Class<T> enumType) {
                this.enumType = enumType;
            }
    
            public T convert(String source) {
                return (T) Enum.valueOf(this.enumType, source.trim());
            }
        }
    }

#### 3.4.3. Using GenericConverter

    package org.springframework.core.convert.converter;
    
    public interface GenericConverter {
    
        public Set<ConvertiblePair> getConvertibleTypes();
    
        Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
    }

Using ConditionalGenericConverter

    public interface ConditionalConverter {
    
        boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);
    }
    
    public interface ConditionalGenericConverter extends GenericConverter, ConditionalConverter {
    }

#### 3.4.4. The ConversionService API

    package org.springframework.core.convert;
    
    public interface ConversionService {
    
        boolean canConvert(Class<?> sourceType, Class<?> targetType);
    
        <T> T convert(Object source, Class<T> targetType);
    
        boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);
    
        Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
    }

#### 3.4.5. Configuring a ConversionService

    <bean id="conversionService"
            class="org.springframework.context.support.ConversionServiceFactoryBean">
        <property name="converters">
            <set>
                <bean class="example.MyCustomConverter"/>
            </set>
        </property>
    </bean>

#### 3.4.6. Using a ConversionService Programmatically

    @Service
    public class MyService {
    
        public MyService(ConversionService conversionService) {
            this.conversionService = conversionService;
        }
    
        public void doIt() {
            this.conversionService.convert(...)
        }
    }

### 3.5. Spring Field Formatting
In general, you can use the Converter SPI when you need to implement general-purpose type conversion logic — for example, for converting between a java.util.Date and a Long. You can use the Formatter SPI when you work in a client environment (such as a web application) and need to parse and print localized field values.

#### 3.5.1. The Formatter SPI

    package org.springframework.format;
    
    public interface Formatter<T> extends Printer<T>, Parser<T> {
    }
.

    public interface Printer<T> {
    
        String print(T fieldValue, Locale locale);
    }
.

    import java.text.ParseException;
    
    public interface Parser<T> {
    
        T parse(String clientValue, Locale locale) throws ParseException;
    }
Example Formatter implementation:

    package org.springframework.format.datetime;
    
    public final class DateFormatter implements Formatter<Date> {
    
        private String pattern;
    
        public DateFormatter(String pattern) {
            this.pattern = pattern;
        }
    
        public String print(Date date, Locale locale) {
            if (date == null) {
                return "";
            }
            return getDateFormat(locale).format(date);
        }
    
        public Date parse(String formatted, Locale locale) throws ParseException {
            if (formatted.length() == 0) {
                return null;
            }
            return getDateFormat(locale).parse(formatted);
        }
    
        protected DateFormat getDateFormat(Locale locale) {
            DateFormat dateFormat = new SimpleDateFormat(this.pattern, locale);
            dateFormat.setLenient(false);
            return dateFormat;
        }
    }

#### 3.5.2. Annotation-driven Formatting

    package org.springframework.format;
    
    public interface AnnotationFormatterFactory<A extends Annotation> {
    
        Set<Class<?>> getFieldTypes();
    
        Printer<?> getPrinter(A annotation, Class<?> fieldType);
    
        Parser<?> getParser(A annotation, Class<?> fieldType);
    }
.

    public final class NumberFormatAnnotationFormatterFactory
    implements AnnotationFormatterFactory<NumberFormat> {
    
        public Set<Class<?>> getFieldTypes() {
            return new HashSet<Class<?>>(asList(new Class<?>[] {
                Short.class, Integer.class, Long.class, Float.class,
                Double.class, BigDecimal.class, BigInteger.class }));
        }
    
        public Printer<Number> getPrinter(NumberFormat annotation, Class<?> fieldType) {
            return configureFormatterFrom(annotation, fieldType);
        }
    
        public Parser<Number> getParser(NumberFormat annotation, Class<?> fieldType) {
            return configureFormatterFrom(annotation, fieldType);
        }
    
        private Formatter<Number> configureFormatterFrom(NumberFormat annotation, Class<?> fieldType) {
            if (!annotation.pattern().isEmpty()) {
                return new NumberStyleFormatter(annotation.pattern());
            } else {
                Style style = annotation.style();
                if (style == Style.PERCENT) {
                    return new PercentStyleFormatter();
                } else if (style == Style.CURRENCY) {
                    return new CurrencyStyleFormatter();
                } else {
                    return new NumberStyleFormatter();
                }
            }
        }
    }
Annotate fields with @NumberFormat, as the following example shows:

    public class MyModel {
    
        @NumberFormat(style=Style.CURRENCY)
        private BigDecimal decimal;
    }

#### 3.5.3. The FormatterRegistry SPI
The FormatterRegistry is an SPI for registering formatters and converters. 

    package org.springframework.format;
    
    public interface FormatterRegistry extends ConverterRegistry {
    
        void addPrinter(Printer<?> printer);
    
        void addParser(Parser<?> parser);
    
        void addFormatter(Formatter<?> formatter);
    
        void addFormatterForFieldType(Class<?> fieldType, Formatter<?> formatter);
    
        void addFormatterForFieldType(Class<?> fieldType, Printer<?> printer, Parser<?> parser);
    
        void addFormatterForFieldAnnotation(AnnotationFormatterFactory<? extends Annotation> annotationFormatterFactory);
    }

#### 3.5.4. The FormatterRegistrar SPI
FormatterRegistrar is an SPI for registering formatters and converters through the FormatterRegistry. 
    
    package org.springframework.format;
    
    public interface FormatterRegistrar {
    
        void registerFormatters(FormatterRegistry registry);
    }

### 3.6. Configuring a Global Date and Time Format
By default, date and time fields not annotated with @DateTimeFormat are converted from strings by using the DateFormat.SHORT.

To do that, ensure that Spring does not register default formatters. Instead, register formatters manually with the help of:
* org.springframework.format.datetime.standard.DateTimeFormatterRegistrar
* org.springframework.format.datetime.DateFormatterRegistrar

    @Configuration
    public class AppConfig {
    
        @Bean
        public FormattingConversionService conversionService() {
    
            // Use the DefaultFormattingConversionService but do not register defaults
            DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService(false);
    
            // Ensure @NumberFormat is still supported
            conversionService.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());
    
            // Register JSR-310 date conversion with a specific global format
            DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
            registrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyyMMdd"));
            registrar.registerFormatters(conversionService);
    
            // Register date conversion with a specific global format
            DateFormatterRegistrar registrar = new DateFormatterRegistrar();
            registrar.setFormatter(new DateFormatter("yyyyMMdd"));
            registrar.registerFormatters(conversionService);
    
            return conversionService;
        }
    }


### 3.7. Java Bean Validation
#### 3.7.1. Overview of Bean Validation

    public class PersonForm {
    
        @NotNull
        @Size(max=64)
        private String name;
    
        @Min(0)
        private int age;
    }

#### 3.7.2. Configuring a Bean Validation Provider

    import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
    
    @Configuration
    public class AppConfig {
    
        @Bean
        public LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
    }

Injecting a Validator

    import org.springframework.validation.Validator;
    
    @Service
    public class MyService {
    
        @Autowired
        private Validator validator;
    }

Configuring Custom Constraints

Each bean validation constraint consists of two parts:
* A @Constraint annotation that declares the constraint and its configurable properties.
* An implementation of the javax.validation.ConstraintValidator interface that implements the constraint’s behavior.


    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy=MyConstraintValidator.class)
    public @interface MyConstraint {
    }
.

    import javax.validation.ConstraintValidator;
    
    public class MyConstraintValidator implements ConstraintValidator {
    
        @Autowired;
        private Foo aDependency;
    
        // ...
    }

Spring-driven Method Validation

    import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
    
    @Configuration
    public class AppConfig {
    
        @Bean
        public MethodValidationPostProcessor validationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

#### 3.7.3. Configuring a DataBinder
Since Spring 3, you can configure a DataBinder instance with a Validator. Once configured, you can invoke the Validator by calling binder.validate(). Any validation Errors are automatically added to the binder’s BindingResult.

    Foo target = new Foo();
    DataBinder binder = new DataBinder(target);
    binder.setValidator(new FooValidator());
    
    // bind to the target object
    binder.bind(propertyValues);
    
    // validate the target object
    binder.validate();
    
    // get BindingResult that includes any validation errors
    BindingResult results = binder.getBindingResult();