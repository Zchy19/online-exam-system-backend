package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.Certificate;
import cn.org.alan.exam.model.form.cretificate.CertificateForm;
import cn.org.alan.exam.model.vo.certificate.MyCertificateVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;


public interface ICertificateService extends IService<Certificate> {

    
    
    Result<String> addCertificate(CertificateForm certificateForm);


    
    
    Result<IPage<Certificate>> pagingCertificate(Integer pageNum, Integer pageSize, String certificateName, String certificationUnit);


    
    
    Result<String> updateCertificate(CertificateForm certificateForm);

    
    Result<String> deleteCertificate(Integer id);

    
    Result<IPage<MyCertificateVO>> getMyCertificatePaging(Integer pageNum, Integer pageSize, String examName);
}
