package cn.org.alan.exam.model.vo.score;

import cn.org.alan.exam.utils.excel.ExcelExport;
import lombok.Data;


@Data
public class ExportScoreVO {

    @ExcelExport("姓名")
    private String realName;
    @ExcelExport("班级")
    private String gradeName;
    @ExcelExport("分数")
    private Double score;
    @ExcelExport(value = "名次",sort = 1)
    private Integer ranking;
}
