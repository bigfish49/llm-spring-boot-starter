package io.github.llm.error;

/**
 * 业务异常
 * @see ExceptionCode
 * @author kepler
 */
public class LLMException extends RuntimeException {

    private final String code;

    public LLMException(ExceptionCode code) {
        super(code.getDesc());
        this.code = code.getCode();
    }

    public LLMException(ExceptionCode code, String msg) {
        super(msg);
        this.code = code.getCode();
    }

    public LLMException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}


