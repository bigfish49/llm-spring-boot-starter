package cn.llm.core;

import java.io.Serializable;

/**
 * Server-Sent Events (SSE) is a server push technology enabling a client
 * to receive automatic updates from a server via an HTTP connection,
 * and describes how servers can initiate data transmission towards clients
 * once an initial client connection has been established.
 *
 * SSE的接口返回值必须实现本接口
 */
public interface Streamable extends Serializable {

    /**
     * 前端EventSource需要判断消息是否终止
     */
    boolean isEnd();

}
