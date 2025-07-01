package cn.org.alan.exam.model.form.cretificate;


import cn.org.alan.exam.common.group.CertificateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class CertificateForm {

    
    private Integer id;

    
    @NotBlank(message = "证书名称不能为空",groups = CertificateGroup.CertificateInsertGroup.class)
    private String certificateName;

    
    private String image;

    
    @NotBlank(message = "发证单位不能为空",groups = CertificateGroup.CertificateInsertGroup.class)
    private String certificationNuit;

}
