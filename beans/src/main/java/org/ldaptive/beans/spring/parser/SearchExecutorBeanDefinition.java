package org.ldaptive.beans.spring.parser;

import org.ldaptive.SearchExecutor;
import org.ldaptive.SearchFilter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Parser for <pre>search-executor</pre> elements.
 */
public class SearchExecutorBeanDefinition extends AbstractSingleBeanDefinitionParser
{


    @Override
    protected String resolveId(
            final Element element,
            // CheckStyle:IllegalTypeCheck OFF
            final AbstractBeanDefinition definition,
            // CheckStyle:IllegalTypeCheck ON
            final ParserContext parserContext)
            throws BeanDefinitionStoreException
    {
        final String idAttrValue = element.getAttribute("id");
        return StringUtils.hasText(idAttrValue) ? idAttrValue : "search-executor";
    }


    @Override
    protected Class<?> getBeanClass(final Element element)
    {
        return SearchExecutor.class;
    }


    @Override
    protected void doParse(
            final Element element,
            final ParserContext context,
            final BeanDefinitionBuilder builder)
    {
        builder.addPropertyValue("baseDn", element.getAttribute("baseDn"));
        if (element.hasAttribute("searchFilter")) {
            final BeanDefinitionBuilder filter = BeanDefinitionBuilder.genericBeanDefinition(SearchFilter.class);
            filter.addPropertyValue("filter", element.getAttribute("searchFilter"));
            builder.addPropertyValue("searchFilter", filter.getBeanDefinition());
        }
        if (element.hasAttribute("returnAttributes")) {
            builder.addPropertyValue("returnAttributes", element.getAttribute("returnAttributes"));
        }
        builder.addPropertyValue("searchScope", element.getAttribute("searchScope"));
        builder.addPropertyValue("timeLimit", element.getAttribute("timeLimit"));
        builder.addPropertyValue("sizeLimit", element.getAttribute("sizeLimit"));
        if (element.hasAttribute("binaryAttributes")) {
            builder.addPropertyValue("binaryAttributes", element.getAttribute("binaryAttributes"));
        }
        builder.addPropertyValue("sortBehavior", element.getAttribute("sortBehavior"));
    }
}
