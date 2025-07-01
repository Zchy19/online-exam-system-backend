package cn.org.alan.exam.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ExamState {
    ONGOING(0, "考试中"),
    SUBMITTED(1, "已交卷"),
    
    ;
    
    private final int code;
    private final String desc;
}