package io.github.llm.error;

/**
 * @author kepler
 * 通用模块错误码枚举
 */
public enum ExceptionCode {
    // llm相关
    LLM_RESULT_VALID("llm_result_valid", "LLM响应异常"),
    LLM_SEND_HTTP("llm_send_http", "LLM请求异常"),


    ;
    private final String code;
    private final String desc;

    ExceptionCode(String code, String name) {
        this.code = code;
        this.desc = name;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
