/* Copyright 2010 Marcel Overdijk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.objectify.spring;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.ObjectifyFactory;
import com.mycompany.domain.Car;
import com.mycompany.domain.Person;
import com.othercompany.domain.Insurance;

public class ObjectifyFactoryBeanTest {

    private ObjectifyFactoryBean factory = null;
    
    @Before
    public void setUp() {
        factory = new ObjectifyFactoryBean();
    }
    
    @Test
    public void testGetObjectWithJavaPersisitenceEntity() throws Exception {
        factory.setBasePackage("com.mycompany.domain");
        factory.afterPropertiesSet();
        
        ObjectifyFactory objectifyFactory = factory.getObject();
        
        Class<?> clazz = Car.class; // Car is annotated with javax.persistence.Entity
        assertNotNull(objectifyFactory.getMetadata(clazz));
        assertNotNull(clazz.getAnnotation(javax.persistence.Entity.class));
    }
    
    @Test
    public void testGetObjectWithObjectifyEntity() throws Exception {
        factory.setBasePackage("com.othercompany.domain");
        factory.afterPropertiesSet();
        
        ObjectifyFactory objectifyFactory = factory.getObject();
        
        Class<?> clazz = Insurance.class; // Insurance is annotated with com.googlecode.objectify.annotation.Entity
        assertNotNull(objectifyFactory.getMetadata(clazz));
        assertNotNull(clazz.getAnnotation(com.googlecode.objectify.annotation.Entity.class));
    }
    
    @Test
    public void testGetObjectWithBasePackage() throws Exception {
        factory.setBasePackage("com.mycompany.domain");
        factory.afterPropertiesSet();
        
        ObjectifyFactory objectifyFactory = factory.getObject();
        
        assertNotNull(objectifyFactory.getMetadata(Car.class));
        assertNotNull(objectifyFactory.getMetadata(Person.class));
    }
    
    @Test
    public void testGetObjectWithBasePackages() throws Exception {
        factory.setBasePackage("com.mycompany.domain;com.othercompany.domain;com.notexistingcompany.domain");
        factory.afterPropertiesSet();
        
        ObjectifyFactory objectifyFactory = factory.getObject();
        
        assertNotNull(objectifyFactory.getMetadata(Car.class));
        assertNotNull(objectifyFactory.getMetadata(Person.class));
        assertNotNull(objectifyFactory.getMetadata(Insurance.class));
    }
    
    @Test
    public void testGetObjectWithClasses() throws Exception {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(Car.class);
        classes.add(Person.class);
        classes.add(Insurance.class);
        
        factory.setClasses(classes);
        factory.afterPropertiesSet();
        
        ObjectifyFactory objectifyFactory = factory.getObject();
        
        assertNotNull(objectifyFactory.getMetadata(Car.class));
        assertNotNull(objectifyFactory.getMetadata(Person.class));
        assertNotNull(objectifyFactory.getMetadata(Insurance.class));
    }
    
    @Test
    public void testGetObjectWithBasePackageAndClasses() throws Exception {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(Insurance.class);
        
        factory.setBasePackage("com.mycompany.domain");
        factory.setClasses(classes);
        factory.afterPropertiesSet();
        
        ObjectifyFactory objectifyFactory = factory.getObject();
        
        assertNotNull(objectifyFactory.getMetadata(Car.class));
        assertNotNull(objectifyFactory.getMetadata(Person.class));
        assertNotNull(objectifyFactory.getMetadata(Insurance.class));
    }
    
    @Test
    public void testGetObjectType() {
        assertTrue(factory.getObjectType().isAssignableFrom(ObjectifyFactory.class));
    }
    
    @Test
    public void testIsSingleton() {
        assertTrue(factory.isSingleton());
    }
}
