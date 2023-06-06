package cn.llm.client.core;

import cn.hutool.core.bean.BeanUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Kepler
 * LLM client 配置
 * 包含 模型超参、鉴权参数
 */
@Getter
@Setter
public class LLMClientOptions extends HttpClientOptions {

    private Boolean enabled;

    public Map<String, Object> toMap() {
        return BeanUtil.beanToMap(this);
    }

}
