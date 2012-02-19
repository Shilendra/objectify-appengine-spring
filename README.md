Objectify App Engine Spring
===========================

This project contains a Spring <a href="http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/beans/factory/FactoryBean.html">FactoryBean</a> to integrate <a href="http://code.google.com/p/objectify-appengine">Objectify</a> with the <a href="http://www.springsource.com/developer/spring">Spring Framework</a>.

First, download the latest <a href="http://github.com/downloads/marceloverdijk/objectify-appengine-spring/objectify-appengine-spring-1.1.1.jar">objectify-appengine-spring-1.1.jar</a> and include it your application's classpath.

Maven users should add the following dependency instead:

    <dependency>
      <groupId>com.googlecode.objectify-appengine-spring</groupId>
      <artifactId>objectify-appengine-spring</artifactId>
      <version>1.1.1</version>
    </dependency>

Next, configure the ObjectifyFactoryBean class in your Spring application context. The simplest way to use this class is to provide a basePackage and it will scan for classes annotated with javax.perjavax.persistence.Entity or com.googlecode.objectify.annotation.Entity. Found classes will then be registered in the <a href="http://objectify-appengine.googlecode.com/svn/trunk/javadoc/com/googlecode/objectify/ObjectifyFactory.html">ObjectifyFactory</a>. 

Example configuration:

    <bean class="com.googlecode.objectify.spring.ObjectifyFactoryBean" p:basePackage="com.mycompany.domain" />

Multiple basePackages can be provided as well:

    <bean class="com.googlecode.objectify.spring.ObjectifyFactoryBean" p:basePackage="com.mycompany.domain;com.othercompany.domain" />

Alternatively it is also possible to explicitly define classes to be registered in the <a href="http://objectify-appengine.googlecode.com/svn/trunk/javadoc/com/googlecode/objectify/ObjectifyFactory.html">ObjectifyFactory</a>. This approach is useful when you are concerned by App Engine's cold startup times and are not using the context:component-scan option to autodetect classes.

Example configuration:

    <bean class="com.googlecode.objectify.spring.ObjectifyFactoryBean">
      <property name="classes">
        <list>
          <value>com.mycompany.domain.Person</value>
          <value>com.mycompany.domain.Address</value>
        </list>
      </property>
    </bean>

After the ObjectifyFactoryBean is configured, the ObjectifyFactory can be injected in other classes like:

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestMethod;
    
    import com.googlecode.objectify.Objectify;
    import com.googlecode.objectify.ObjectifyFactory;
    
    @Controller
    @RequestMapping("/cars")
    public class CarController {
    
      @Autowired private ObjectifyFactory objectifyFactory;
      
      @RequestMapping(method = RequestMethod.GET)
      public String list(Model model) {
      
        Objectify ofy = objectifyFactory.begin();
        List<Person> persons = ofy.query(Person.class).list();
        
        model.addAttribute("persons", persons);
      }
    }

    
Authors
-------

**Marcel Overdijk**

+ marcel@overdijk.me
+ http://twitter.com/marceloverdijk
+ http://github.com/marceloverdijk


Copyright and License
---------------------

Copyright 2012 Marcel Overdijk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
