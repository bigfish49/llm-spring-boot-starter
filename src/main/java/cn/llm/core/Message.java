package cn.llm.core;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Kepler
 * 多轮对话
 * 不同大模型多轮对话API都会接收类似结构的数据, 字段名称稍有差异
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    private String role;

    private String content;

    public enum RoleEnum {
        USER("user"),
        AI("assistant"),
        // 文心暂不支持system
        SYSTEM("system");

        private final String name;

        RoleEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 快速生成一个AI message
     */
    public static Message ofAI(String content) {
        return new Message(RoleEnum.AI.name, content);
    }

    /**
     * 快速生成一个用户 message
     */
    public static Message ofUser(String content) {
        return new Message(RoleEnum.USER.name, content);
    }

    /**
     * 快速生成一个多轮对话
     */
    public static List<Message> ofDialog(String prompt, Message... chatHistory) {
        Message msg = Message.ofUser(prompt);
        if (ArrayUtil.isEmpty(chatHistory)) {
            return Collections.singletonList(msg);
        }
        List<Message> dialog = ListUtil.toLinkedList(chatHistory);
        dialog.add(msg);
        return dialog;
    }

    @Override
    public String toString() {
        return this.role + ": " + this.content + "\n";
    }
}
