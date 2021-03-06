/* See LICENSE for licensing and NOTICE for copyright. */
package org.ldaptive.beans.spring.parser;

import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.PooledConnectionFactory;
import org.ldaptive.SearchConnectionValidator;
import org.ldaptive.SearchRequest;
import org.ldaptive.pool.IdlePruneStrategy;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

/**
 * Common implementation for all connection factories.
 *
 * @author Middleware Services
 */
public abstract class AbstractConnectionFactoryBeanDefinitionParser extends AbstractConnectionConfigBeanDefinitionParser
{


  /**
   * Creates a default connection factory.
   *
   * @param  builder  bean definition builder to set properties on, may be null
   * @param  element  containing configuration
   * @param  includeConnectionInitializer  whether to include a connection initializer
   *
   * @return  default connection factory bean definition builder
   */
  protected BeanDefinitionBuilder parseDefaultConnectionFactory(
    final BeanDefinitionBuilder builder,
    final Element element,
    final boolean includeConnectionInitializer)
  {
    BeanDefinitionBuilder factory = builder;
    if (factory == null) {
      factory = BeanDefinitionBuilder.genericBeanDefinition(DefaultConnectionFactory.class);
    }
    factory.addPropertyValue(
      "connectionConfig",
      parseConnectionConfig(null, element, includeConnectionInitializer).getBeanDefinition());
    return factory;
  }


  /**
   * Creates a pooled connection factory.
   *
   * @param  builder  bean definition builder to set properties on, may be null
   * @param  name  of the connection pool
   * @param  element  containing configuration
   * @param  includeConnectionInitializer  whether to include a connection initializer
   *
   * @return  pooled connection factory bean definition builder
   */
  protected BeanDefinitionBuilder parsePooledConnectionFactory(
    final BeanDefinitionBuilder builder,
    final String name,
    final Element element,
    final boolean includeConnectionInitializer)
  {
    BeanDefinitionBuilder pool = builder;
    if (pool == null) {
      pool = BeanDefinitionBuilder.genericBeanDefinition(PooledConnectionFactory.class);
    }
    pool.addPropertyValue("name", name);
    pool.addPropertyValue(
      "defaultConnectionFactory",
      parseDefaultConnectionFactory(null, element, includeConnectionInitializer).getBeanDefinition());

    pool.addPropertyValue("minPoolSize", element.getAttribute("minPoolSize"));
    pool.addPropertyValue("maxPoolSize", element.getAttribute("maxPoolSize"));
    pool.addPropertyValue("validateOnCheckOut", element.getAttribute("validateOnCheckOut"));
    pool.addPropertyValue("validatePeriodically", element.getAttribute("validatePeriodically"));

    final BeanDefinitionBuilder blockWaitTime =  BeanDefinitionBuilder.rootBeanDefinition(
      AbstractBeanDefinitionParser.class,
      "parseDuration");
    blockWaitTime.addConstructorArgValue(element.getAttribute("blockWaitTime"));
    pool.addPropertyValue("blockWaitTime", blockWaitTime.getBeanDefinition());


    pool.addPropertyValue("failFastInitialize", element.getAttribute("failFastInitialize"));

    final BeanDefinitionBuilder pruneStrategy = BeanDefinitionBuilder.genericBeanDefinition(IdlePruneStrategy.class);
    final BeanDefinitionBuilder prunePeriod =  BeanDefinitionBuilder.rootBeanDefinition(
      AbstractBeanDefinitionParser.class,
      "parseDuration");
    prunePeriod.addConstructorArgValue(element.getAttribute("prunePeriod"));
    final BeanDefinitionBuilder idleTime =  BeanDefinitionBuilder.rootBeanDefinition(
      AbstractBeanDefinitionParser.class,
      "parseDuration");
    idleTime.addConstructorArgValue(element.getAttribute("idleTime"));
    pruneStrategy.addPropertyValue("prunePeriod", prunePeriod.getBeanDefinition());
    pruneStrategy.addPropertyValue("idleTime", idleTime.getBeanDefinition());
    pool.addPropertyValue("pruneStrategy", pruneStrategy.getBeanDefinition());

    final BeanDefinitionBuilder validator = BeanDefinitionBuilder.genericBeanDefinition(
      SearchConnectionValidator.class);
    final BeanDefinitionBuilder validatePeriod =  BeanDefinitionBuilder.rootBeanDefinition(
      AbstractBeanDefinitionParser.class,
      "parseDuration");
    validatePeriod.addConstructorArgValue(element.getAttribute("validatePeriod"));
    final BeanDefinitionBuilder validateTimeout =  BeanDefinitionBuilder.rootBeanDefinition(
      AbstractBeanDefinitionParser.class,
      "parseDuration");
    validateTimeout.addConstructorArgValue(element.getAttribute("validateTimeout"));
    validator.addPropertyValue("validatePeriod", validatePeriod.getBeanDefinition());
    validator.addPropertyValue("validateTimeout", validateTimeout.getBeanDefinition());
    if (element.hasAttribute("validateDn") && element.hasAttribute("validateFilter")) {
      final BeanDefinitionBuilder searchRequest = BeanDefinitionBuilder.genericBeanDefinition(SearchRequest.class);
      searchRequest.addPropertyValue("baseDn", element.getAttribute("validateDn"));
      final BeanDefinitionBuilder filter =  BeanDefinitionBuilder.rootBeanDefinition(
        SearchOperationBeanDefinitionParser.class,
        "parseFilter");
      filter.addConstructorArgValue(element.getAttribute("validateFilter"));
      searchRequest.addPropertyValue("filter", filter.getBeanDefinition());
      validator.addPropertyValue("searchRequest", searchRequest.getBeanDefinition());
    }
    pool.addPropertyValue("validator", validator.getBeanDefinition());

    if (element.hasAttribute("ldapUrl")) {
      pool.setInitMethodName("initialize");
    } else {
      logger.info("No ldapUrl attribute found for element {}, pool not initialized.", name);
    }
    return pool;
  }
}
