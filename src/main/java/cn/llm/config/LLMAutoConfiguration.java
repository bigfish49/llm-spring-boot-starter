package cn.llm.config;

import cn.llm.client.ERNIEBot;
import cn.llm.client.GLM;
import cn.llm.client.OpenAI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.resources.ConnectionProvider;

/**
 * @author Kepler
 * 自动配置llm client
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LLMProperties.class)
public class LLMAutoConfiguration {

    private static final String POOL_NAME = "llmConnectionProvider";

    @Autowired
    private LLMProperties llmProperties;

    @Bean(destroyMethod = "dispose")
    @ConditionalOnMissingBean(name = {POOL_NAME})
    @ConditionalOnProperty(value = "spring.griffin.client.useGlobalPool", matchIfMissing = true)
    public ConnectionProvider llmConnectionProvider() {
        return llmProperties.getClient().constructConnProvider();
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConditionalOnMissingBean(name = {"ernieBot", "ernieBotClient"})
    @ConditionalOnProperty(value = "spring.llm.client.ernieBot.enabled", matchIfMissing = true)
    public ERNIEBot ernieBot(@Autowired(required = false)
                             @Qualifier(POOL_NAME) ConnectionProvider connectionProvider) {
        ERNIEBot llm = new ERNIEBot(llmProperties.getClient().getErnieBot());
        if (null != connectionProvider) {
            llm.setConnProvider(connectionProvider);
        }
        return llm;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConditionalOnMissingBean(name = {"glm", "glmClient"})
    @ConditionalOnProperty(value = "spring.llm.client.glm.enabled")
    public GLM glm(@Autowired(required = false)
                   @Qualifier(POOL_NAME) ConnectionProvider connectionProvider) {
        GLM llm = new GLM(llmProperties.getClient().getGlm());
        if (null != connectionProvider) {
            llm.setConnProvider(connectionProvider);
        }
        return llm;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConditionalOnMissingBean(name = {"openai", "openaiClient"})
    @ConditionalOnProperty(value = "spring.llm.client.openai.enabled")
    public OpenAI openai(@Autowired(required = false)
                         @Qualifier(POOL_NAME) ConnectionProvider connectionProvider) {
        OpenAI llm = new OpenAI(llmProperties.getClient().getOpenai());
        if (null != connectionProvider) {
            llm.setConnProvider(connectionProvider);
        }
        return llm;
    }

}
