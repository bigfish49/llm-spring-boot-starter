package cn.llm.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;


/**
 * jackson utils
 */
public class JacksonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtil.class);

    public static final ObjectMapper MAPPER;

    private static final ObjectMapper IGNORE_NULL_VALUE_MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(FAIL_ON_EMPTY_BEANS, false);
        // 允许Json字符串包含注释，如：{id:6677654,name:小佳}
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        // 允许使用非双引号属性名字，如：{id:6677654,name:小佳}
        MAPPER.configure(ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许使用单引号属性名字，如：{'id':'6677654','name':'小佳'}
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
        MAPPER.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), false);
        MAPPER.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 设置默认Date格式化时间
        MAPPER.configOverride(Date.class).setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd HH:mm:ss"));
        // 配置默认时区为系统时区
        MAPPER.setTimeZone(TimeZone.getDefault());

        IGNORE_NULL_VALUE_MAPPER = MAPPER.copy();
        // 空值不输出
        IGNORE_NULL_VALUE_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private JacksonUtil() {
    }

    public static String toStr(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            LOGGER.error("Failed to convert object to json string, object: {}", o, e);
            return null;
        }
    }

    public static String toStr(Object o, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            LOGGER.error("Failed to convert object to json string, object: {}", o, e);
            return null;
        }
    }

    public static String toStrIgnoreNullValue(Object o) {
        try {
            return IGNORE_NULL_VALUE_MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            LOGGER.error("Failed to convert object to json string ignore null value, object: {}", o, e);
            return null;
        }
    }

    public static String toStrPretty(Object o) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (Exception e) {
            LOGGER.error("object to json string error: {}", e.getMessage());
            return null;
        }
    }

    public static void writeObj(OutputStream out, Object value)
            throws IOException {
        MAPPER.writeValue(out, value);
    }

    public static <T> T str2Obj(String s, Class<T> valueType)
            throws IOException {
        return MAPPER.readValue(s, valueType);
    }

    public static <T> T str2ObjRuntimeErr(String s, Class<T> valueType) {
        try {
            return MAPPER.readValue(s, valueType);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("str to obj failed", e);
        }
    }

    /**
     * 字符串转换成对象
     * 例如：
     * <pre>
     *     {@code JsonUtil.str2ObjRuntimeErr("[1,2,3]", new TypeReference<List<Integer>>(){} );}
     *     {@code JsonUtil.str2ObjRuntimeErr("""{"a": 1, "b": 2}""", new TypeReference<Map<String, Integer>>(){} );}
     * </pre>
     * @param in 字符串
     * @param typeReference 类型
     * @return 对象
     * @param <T> 对象
     */
    public static <T> T str2ObjRuntimeErr(String in, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(in, typeReference);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("str to object failed", e);
        }
    }

    public static <T> T readString(String in, Class<T> valueType) throws IOException {
        return MAPPER.readValue(in, valueType);
    }

    public static <T> T readString(String in, Class<T> valueType, ObjectMapper objectMapper) throws IOException {
        return objectMapper.readValue(in, valueType);
    }

    /**
     * 解析字符串为类对象，解析出错抛出 运行时异常
     *
     * @throws RuntimeException 解析出错抛出
     */
    public static <T> T readStrRuntimeErr(String in, Class<T> valueType) {
        try {
            return MAPPER.readValue(in, valueType);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    String.format("str parse to targetClass failed, targetClass: %s, str: %s", valueType, in),
                    e);
        }
    }

    /**
     * 解析字符串为类对象，解析出错时，抛出运行时异常.
     *
     * @throws RuntimeException 解析出错抛出
     */
    public static <T> T readStr2GenericObj(String in, Class<T> outerType, Class<?>... innerTypes) {
        try {
            return MAPPER.readValue(in, MAPPER.getTypeFactory().constructParametricType(outerType, innerTypes));
        } catch (JsonProcessingException e) {
            List<String> list = new ArrayList<>();
            for (Class<?> innerType : innerTypes) {
                String name = innerType.getName();
                list.add(name);
            }
            throw new IllegalArgumentException(
                    String.format("str parse to targetClass failed, targetOuterClass: %s, " +
                                    "targetInnerClass: %s, str: %s", outerType.getName(),
                            list, in),
                    e);
        }
    }

    public static <T> List<T> readListString(String in, Class<T> valueType) throws IOException {
        return MAPPER.readerForListOf(valueType).readValue(in);
    }

    public static <T> T readStringElseNull(String in, Class<T> valueType) {
        return readStringElseNull(in, valueType, MAPPER);
    }

    public static <T> T readStringElseNull(String in, Class<T> valueType, ObjectMapper objectMapper) {
        try {
            return StringUtils.hasText(in) ?
                    objectMapper.readValue(in, valueType) : null;
        } catch (JsonProcessingException e) {
            LOGGER.error("read String to Object failed, str: {}, class: {}", in, valueType, e);
            return null;
        }
    }


    public static <T> T toObject(String json, Class<T> clazz) {
        if (!StringUtils.hasText(json) || clazz == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.error("toObject error ={}.", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 反序列化为List
     *
     * @param json  json string
     * @param clazz type
     * @param <T>   general
     * @return list data
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (!StringUtils.hasText(json) || clazz == null) {
            return Collections.emptyList();
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
            return MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            LOGGER.error("toList error ={}.", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
