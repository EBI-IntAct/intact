# Introduction #

This is an introduction to the core API of IntAct (aka. intact-core), more specifically introducing users to a central concept: IntactContext.
intact-core javadoc can be found [here](http://www.ebi.ac.uk/~intact/apidocs/intact-core/).

# Table of Content #



# IntactContext #

## Description ##

> If there was only one object you should know about in the IntAct API, [IntactContext](http://www.ebi.ac.uk/~intact/apidocs/intact-core/uk/ac/ebi/intact/core/context/IntactContext.html) would be the one as everything starts from it. It allows you to:
    * get access to the IntAct configuration
    * initialize the connection to the underlying database,
    * get access to the stored data,
    * create or alter data,
    * get access to the underlying Spring context.

## Initialization Examples ##

> In order to quickly configure the IntAct API you can rely on its default configuration. It will create a stand-alone in-memory database using [H2](http://www.h2.org).

> The way to initialize the API is as follow:

```
IntactContext.initStandaloneContextInMemory();
```

> You have to bear in mind when running this initialization that IntAct has a set of configuration files (aka. standalone configuration) that takes care of initializing the database and the API with default configuration values. Now obviously, this is fine when you are toying with IntAct as whenever you stop the API, the database will simply not be available anymore.
> So now what you have to do it to create the IntAct database schema into a relational database of your choice (so far we support Oracle, !PostgreSQL and H2) and configure IntAct to use it. The following steps cover this.


## Spring ##

> ### Support ###

> [Spring](http://static.springsource.org/spring/docs/2.5.x/reference/introduction.html#introduction-overview) is a framework that delivers a large amount of functionalities to enterprise Java applications, of which, IntAct 2.0 is heavily relying on a few such as:
    * [Inversion of control container](http://static.springsource.org/spring/docs/2.5.x/reference/beans.html#beans-introduction)
    * [Aspect Oriented Programming](http://static.springsource.org/spring/docs/2.5.x/reference/aop.html#aop-introduction)
    * [Abstraction layer over our Object Relational Mapping (JPA/Hibernate)](http://static.springsource.org/spring/docs/2.5.x/reference/orm.html#orm-jpa).

### Configuration ###

> In order to customize you local installation of IntAct, you will have to override the default IntAct configuration with your own settings. The default configuration can be seen below:
    * [intact.spring.xml](http://code.google.com/p/intact/source/browse/repo/core/trunk/intact-core/src/main/resources/META-INF/intact.spring.xml)
> Just bear in mind that intact.spring.xml is always loaded irrespectively of the configuration you may set. You can always override this configuration by defining your own bean with the same id.
> And whenever using the standalone initialization you will load the following spring configuration:
    * [jpa-standalone.spring.xml](http://code.google.com/p/intact/source/browse/repo/core/trunk/intact-core/src/main/resources/META-INF/standalone/jpa-standalone.spring.xml)
> These configuration files consist of beans declaration that are used and relied on by the IntAct API to access the data in the underlying database.
> Note that any beans that has been configured using Spring can be accessed using the IntactContext. In the example below we are retrieving the default institution currently configured:

```
IntactConfiguration config = (IntactConfiguration)IntactContext.getCurrentInstance().getSpringContext().getBean( "intactConfig" );
System.out.println( config.getDefaultInstitution().getShorltabel() );
```

The main beans you should be aware of in intact.spring.xml are the following:
  * intactInitializer
  * userContext
  * intactConfig

However, unless you want to tweak the way the data is handled when storing it into the database, the default should suffice. Hence, the only thing you would have to set to meet the criteria of the above use case are the following beans:

  * intactConfig



### Configuring Your Local Intact Node ###

> So, now that we know what the IntAct configuration is made of, we can try configure our own. So let's assume we are installing IntAct following these settings:
  * the institution is called "Random Discovery LTD"
  * we are aiming at storing our interaction data in an Oracle database
  * we want our local object identifiers to be prefixed with "RD:"

> First of all, let's create a new spring configuration file in src/main/resources/META-INF and let's call it rd.spring.xml

> Then, let's create a bean definition for our own institution:

```
    <bean id="randomDiscovery" class="uk.ac.ebi.intact.model.util.InstitutionFactoryBean">
        <property name="name" value="randomdiscovery"/>
        <property name="description" value="Cambridge, Discovery road"/>
        <property name="miAc" value="MI:9999"/>
        <property name="pubmed" value="123456778"/>
        <property name="url" value="http://www.random-discovery.com"/>
    </bean>
```

> Now we can override the default institution configuration by defining the 'defaultInstitution' property of bean 'intactConfig':

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.0.xsd
           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">

    <bean id="randomDiscovery" class="uk.ac.ebi.intact.model.util.InstitutionFactoryBean">
        <property name="name" value="randomdiscovery"/>
        <property name="description" value="Cambridge, Discovery road"/>
        <property name="miAc" value="MI:9999"/>
        <property name="pubmed" value="123456778"/>
        <property name="url" value="http://www.random-discovery.com"/>
    </bean>

    <bean id="intactConfig" class="uk.ac.ebi.intact.core.config.IntactConfiguration">
        <property name="acPrefix" value="RD:"/>
        <property name="defaultInstitution" ref="randomDiscovery"/>
    </bean>

</beans>
```

> _Note_: the miAc and the publication are set to random value. You can find in the [PSI-MI Ontology](http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI) already defined interaction databases.

> Now, in order to start the API using this configuration you only need to use the IntactContext and give it the above defined spring file:

```
IntactContext.initContext(new String[] {"/META-INF/rd.spring.xml"});
```

> Now the last step to get your local node setup is to configure the database, this is going to involve writing some more spring configuration and we are going to do it in a separate file in src/META-INF/rd-jpa.spring.xml
In the example below we are setting properties of bean 'intactCoreDataSource' with the specifics of the database connection :
    * server: blackhole.rd.com
    * port: 1531
    * username: admin
    * password: adm1n99

> As well as bean 'jpaVendorAdapter' with the [database dialect](https://www.hibernate.org/hib_docs/v3/api/org/hibernate/dialect/package-summary.html) that we aim at using:
    * Oracle: org.hibernate.dialect.Oracle9Dialect

The resulting file is as follow:

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
           http://www.springframework.org/schema/context
           http://www.springframework.org/schema/context/spring-context-2.5.xsd
           http://www.springframework.org/schema/tx
           http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">

    <bean class="org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor"/>

    <bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
        <property name="dataSource" ref="intactCoreDataSource"/>
    </bean>

    <tx:annotation-driven transaction-manager="transactionManager"/>

    <bean class="org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor"/>

    <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <property name="persistenceUnitName" value="intact-core-default"/>
        <property name="dataSource" ref="intactCoreDataSource"/>
        
        <property name="jpaProperties">
            <props>
                <prop key="hibernate.generate_statistics">true</prop>
                <prop key="hibernate.format_sql">false</prop>
                <prop key="hibernate.hbm2ddl.auto">none</prop>
            </props>
        </property>

        <property name="jpaVendorAdapter">
            <bean class="uk.ac.ebi.intact.core.config.hibernate.IntactHibernateJpaVendorAdapter">
                <property name="databasePlatform" value="org.hibernate.dialect.Oracle9Dialect"/>
                <property name="showSql" value="false"/>
                <property name="generateDdl" value="false"/>
            </bean>
        </property>
    </bean>

    <bean id="intactCoreDataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="oracle.jdbc.OracleDriver"/>
        <property name="url" value="oracle:thin:@blackhole.rd.com:1531"/>
        <property name="username" value="admin"/>
        <property name="password" value="adm1n99"/>
    </bean>

</beans>
```

Now we only need to include this file when initializing IntactContext, like so:

```
IntactContext.initContext(new String[] {"/META-INF/rd.spring.xml", "/META-INF/rd-jpa.spring.xml"});
```

## Using IntactContext in Web Applications ##