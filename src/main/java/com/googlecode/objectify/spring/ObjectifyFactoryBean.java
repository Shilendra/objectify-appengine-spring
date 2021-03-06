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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.googlecode.objectify.ObjectifyFactory;

/**
 * {@link org.springframework.beans.factory.FactoryBean} that creates an 
 * {@link com.googlecode.objectify.ObjectifyFactory}.
 *
 * <p>The simplest way to use this class is to provide a basePackage and it will 
 * scan for classes annotated with {@link javax.perjavax.persistence.Entity} or
 * {@link com.googlecode.objectify.annotation.Entity}. Found classes will then 
 * be registered in the ObjectifyFactory. 
 *
 * <p>Example configuration:
 * 
 * <pre class="code"> &lt;bean class="com.googlecode.objectify.spring.ObjectifyFactoryBean"
 *   p:basePackage="com.mycompany.domain" /&gt;</pre>
 *
 * <p>Multiple basePackages can be provided as well:
 * 
 * <pre class="code"> &lt;bean class="com.googlecode.objectify.spring.ObjectifyFactoryBean"
 *   p:basePackage="com.mycompany.domain;com.othercompany.domain" /&gt;</pre>
 *
 * <p>Alternatively it is also possible to explicitly define classes to be
 * registered in the ObjectifyFactory. This approach is useful when you are 
 * concerned by App Engine's cold startup times and are not using the 
 * context:component-scan option to autodetect classes.  
 *
 * <p>Example configuration:
 *
 * <pre class="code"> &lt;bean class="com.googlecode.objectify.spring.ObjectifyFactoryBean"&gt;
 *   &lt;property name="classes"&gt;
 *     &lt;list&gt;
 *       &lt;value&gt;com.mycompany.domain.Car&lt;/value&gt;
 *       &lt;value&gt;com.mycompany.domain.Person&lt;/value&gt;
 *     &lt;/list&gt;
 *  &lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * @author Marcel Overdijk
 * @see #setBasePackage(String)
 * @see #setClasses(List)
 * @see com.googlecode.objectify.ObjectifyFactory
 * @see javax.perjavax.persistence.Entity
 */
public class ObjectifyFactoryBean implements FactoryBean<ObjectifyFactory>, InitializingBean {
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    private ObjectifyFactory objectifyFactory;
    
    private String basePackage;
    private List<Class<?>> classes;
    
    public ObjectifyFactory getObject() throws Exception {
        return this.objectifyFactory;
    }
    
    public Class<? extends ObjectifyFactory> getObjectType() {
        return ObjectifyFactory.class;
    }
    
    public boolean isSingleton() {
        return true;
    }
    
    public void afterPropertiesSet() throws Exception {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Initialization started");
        }
        long startTime = System.currentTimeMillis();
        
        if (classes == null) {
            classes = new ArrayList<Class<?>>();
        }
        
        if (basePackage != null && basePackage.length() > 0) {
            classes.addAll(doScan());
        }
        
        this.objectifyFactory = new ObjectifyFactory();
        
        for (Class<?> clazz : classes) {
            this.objectifyFactory.register(clazz);
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Registered entity class [" + clazz.getName() + "]");
            }
        }
        
        if (this.logger.isInfoEnabled()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            this.logger.info("Initialization completed in " + elapsedTime + " ms");
        }
    }
    
    protected List<Class<?>> doScan() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String[] basePackages = StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        for (String basePackage : basePackages) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Scanning package [" + basePackage + "]");
            }
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(com.googlecode.objectify.annotation.Entity.class));
            scanner.addIncludeFilter(new AnnotationTypeFilter(javax.persistence.Entity.class));
            Set<BeanDefinition> candidates = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
                Class<?> clazz = ClassUtils.resolveClassName(candidate.getBeanClassName(), ClassUtils.getDefaultClassLoader());
                classes.add(clazz);
            }            
        }
        return classes;
    }
    
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
    
    public void setClasses(List<Class<?>> classes) {
        this.classes = classes;
    }
}
