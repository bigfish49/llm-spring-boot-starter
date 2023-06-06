package io.github.llm.client.core;

import lombok.Getter;
import lombok.Setter;
import reactor.netty.resources.ConnectionProvider;

/**
 * @author Kepler
 * http client 配置
 * 包括 连接池、超时、重试配置
 */
@Getter
@Setter
public class HttpClientOptions {

    /**
     * LLM remote url
     */
    private String remoteUrl;

    /**
     * LLM api key
     */
    private String apiKey;

    /**
     * 连接超时默认10s
     */
    private Integer connectTimeout = 10000;

    /**
     * The response timeout is the time we wait to receive a response after sending a request.
     */
    private Integer responseTimeout = 10000;

    private Boolean soKeepAlive = true;

    /**
     *  enabled keep-alive checks to probe after 5 minutes of being idle
     *  set in seconds
     */
    private Integer tcpKeepIdle = 300;

    /**
     * at 60 seconds intervals.
     */
    private Integer tcpKeepInterval = 60;

    /**
     * set the maximum number of probes before the connection dropping to 8
     */
    private Integer tcpKeepCnt = 8;

    /**
     * the total maximum connections available in the connection pool
     * suppose tps = 1000/s, server responds be 0.2s on average, max connection = 200
     */
    private Integer maxConnections;

    /**
     * 重试次数
     */
    private Integer retryCount = 3;

    /**
     * 构造连接池
     */
    public ConnectionProvider constructConnProvider(){
        ConnectionProvider.Builder builder = ConnectionProvider
                .builder("llm-web-client-conn-pool");
        if (maxConnections != null) {
            builder.maxConnections(maxConnections);
        }
        return builder.build();
    }

}
