package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.Certificate;
import cn.org.alan.exam.model.vo.certificate.MyCertificateVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;

import java.util.List;



public interface CertificateMapper extends BaseMapper<Certificate> {

    
    Page<MyCertificateVO> selectMyCertificate(Page<MyCertificateVO> myCertificateVOPage, Integer pageNum, Integer pageSize, Integer userId, String examName);

    
    IPage<Certificate> selectCertificatePage(IPage<Certificate> page, Integer userId, String certificateName, String certificationUnit);

}
