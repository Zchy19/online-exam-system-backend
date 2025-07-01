package cn.org.alan.exam.model.form.question;

import cn.org.alan.exam.common.exception.ServiceRuntimeException;
import cn.org.alan.exam.model.entity.Option;
import cn.org.alan.exam.utils.excel.ExcelImport;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class QuestionExcelFrom {
    @ExcelImport(value = "试题类型",required = true)
    private Integer quType;
    @ExcelImport(value = "题干",required = true,unique = true)
    private String content;
    @ExcelImport(value = "解析")
    private String analysis;
    
    
    @ExcelImport(value = "题干图片")
    private String image;
    
    @ExcelImport(value = "选项一内容")
    private String option1;
    @ExcelImport(value = "选项一是否正确")
    private Integer righted1;
    
    @ExcelImport(value = "选项一图片")
    private String image1;
    
    @ExcelImport(value = "选项二内容")
    private String option2;
    @ExcelImport(value = "选项二是否正确")
    private Integer righted2;
    
    @ExcelImport(value = "选项二图片")
    private String image2;
    
    @ExcelImport(value = "选项三内容")
    private String option3;
    @ExcelImport(value = "选项三是否正确")
    private Integer righted3;
    
    @ExcelImport(value = "选项三图片")
    private String image3;
    
    @ExcelImport(value = "选项四内容")
    private String option4;
    @ExcelImport(value = "选项四是否正确")
    private Integer righted4;
    
    @ExcelImport(value = "选项四图片")
    private String image4;
    
    @ExcelImport(value = "选项五内容")
    private String option5;
    @ExcelImport(value = "选项五是否正确")
    private Integer righted5;
    
    @ExcelImport(value = "选项五图片")
    private String image5;
    
    @ExcelImport(value = "选项六内容")
    private String option6;
    @ExcelImport(value = "选项六是否正确")
    private Integer righted6;
    
    @ExcelImport(value = "选项六图片")
    private String image6;


    
    public static List<QuestionFrom> converterQuestionFrom(List<QuestionExcelFrom> questionExcelFroms){
        List<QuestionFrom> list = new ArrayList<>(300);
        for (QuestionExcelFrom questionExcelFrom : questionExcelFroms) {
            QuestionFrom questionFrom = new QuestionFrom();
            questionFrom.setContent(questionExcelFrom.getContent());
            questionFrom.setQuType(questionExcelFrom.getQuType());
            questionFrom.setAnalysis(questionExcelFrom.getAnalysis());
            
            questionFrom.setImage(questionExcelFrom.getImage());
            
            List<Option> options = new ArrayList<>();
            
            
            if (questionExcelFrom.getQuType() != 4) { 
                
                if (questionExcelFrom.getOption1() != null && !questionExcelFrom.getOption1().isEmpty() 
                    && questionExcelFrom.getRighted1() == null) {
                    String errorMsg = String.format("导入错误 - 题干为「%s」的试题：选项一内容存在但未设置是否正确，请检查Excel文件中对应行的「选项一是否正确」列", 
                            questionExcelFrom.getContent());
                    throw new ServiceRuntimeException(errorMsg);
                }
                
                if (questionExcelFrom.getOption2() != null && !questionExcelFrom.getOption2().isEmpty() 
                    && questionExcelFrom.getRighted2() == null) {
                    String errorMsg = String.format("导入错误 - 题干为「%s」的试题：选项二内容存在但未设置是否正确，请检查Excel文件中对应行的「选项二是否正确」列", 
                            questionExcelFrom.getContent());
                    throw new ServiceRuntimeException(errorMsg);
                }
                
                if (questionExcelFrom.getOption3() != null && !questionExcelFrom.getOption3().isEmpty() 
                    && questionExcelFrom.getRighted3() == null) {
                    String errorMsg = String.format("导入错误 - 题干为「%s」的试题：选项三内容存在但未设置是否正确，请检查Excel文件中对应行的「选项三是否正确」列", 
                            questionExcelFrom.getContent());
                    throw new ServiceRuntimeException(errorMsg);
                }
                
                if (questionExcelFrom.getOption4() != null && !questionExcelFrom.getOption4().isEmpty() 
                    && questionExcelFrom.getRighted4() == null) {
                    String errorMsg = String.format("导入错误 - 题干为「%s」的试题：选项四内容存在但未设置是否正确，请检查Excel文件中对应行的「选项四是否正确」列", 
                            questionExcelFrom.getContent());
                    throw new ServiceRuntimeException(errorMsg);
                }
                
                if (questionExcelFrom.getOption5() != null && !questionExcelFrom.getOption5().isEmpty() 
                    && questionExcelFrom.getRighted5() == null) {
                    String errorMsg = String.format("导入错误 - 题干为「%s」的试题：选项五内容存在但未设置是否正确，请检查Excel文件中对应行的「选项五是否正确」列", 
                            questionExcelFrom.getContent());
                    throw new ServiceRuntimeException(errorMsg);
                }
                
                if (questionExcelFrom.getOption6() != null && !questionExcelFrom.getOption6().isEmpty() 
                    && questionExcelFrom.getRighted6() == null) {
                    String errorMsg = String.format("导入错误 - 题干为「%s」的试题：选项六内容存在但未设置是否正确，请检查Excel文件中对应行的「选项六是否正确」列", 
                            questionExcelFrom.getContent());
                    throw new ServiceRuntimeException(errorMsg);
                }
            }
            
            if (questionExcelFrom.getOption1() != null && !questionExcelFrom.getOption1().isEmpty()) {
                Option option = new Option();
                option.setContent(questionExcelFrom.getOption1());
                option.setIsRight(questionExcelFrom.getRighted1());
                
                option.setImage(questionExcelFrom.getImage1());
                options.add(option);
            }
            if (questionExcelFrom.getOption2() != null && !questionExcelFrom.getOption2().isEmpty()) {
                Option option = new Option();
                option.setContent(questionExcelFrom.getOption2());
                option.setIsRight(questionExcelFrom.getRighted2());
                
                option.setImage(questionExcelFrom.getImage2());
                options.add(option);
            }
            if (questionExcelFrom.getOption3() != null && !questionExcelFrom.getOption3().isEmpty()) {
                Option option = new Option();
                option.setContent(questionExcelFrom.getOption3());
                option.setIsRight(questionExcelFrom.getRighted3());
                
                option.setImage(questionExcelFrom.getImage3());
                options.add(option);
            }
            if (questionExcelFrom.getOption4() != null && !questionExcelFrom.getOption4().isEmpty()) {
                Option option = new Option();
                option.setContent(questionExcelFrom.getOption4());
                option.setIsRight(questionExcelFrom.getRighted4());
                
                option.setImage(questionExcelFrom.getImage4());
                options.add(option);
            }
            if (questionExcelFrom.getOption5() != null && !questionExcelFrom.getOption5().isEmpty()) {
                Option option = new Option();
                option.setContent(questionExcelFrom.getOption5());
                option.setIsRight(questionExcelFrom.getRighted5());
                
                option.setImage(questionExcelFrom.getImage5());
                options.add(option);
            }
            if (questionExcelFrom.getOption6() != null && !questionExcelFrom.getOption6().isEmpty()) {
                Option option = new Option();
                option.setContent(questionExcelFrom.getOption6());
                option.setIsRight(questionExcelFrom.getRighted6());
                
                option.setImage(questionExcelFrom.getImage6());
                options.add(option);
            }

            questionFrom.setOptions(options);
            list.add(questionFrom);
        }
        return list;
    }
}
