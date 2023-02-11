package com.steel.product.application.dao;

import com.steel.product.application.entity.QualityTemplateEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QualityTemplateRepository extends JpaRepository<QualityTemplateEntity, Integer> {

	QualityTemplateEntity findByTemplateId(Integer templateId);

	@Query("select distinct q from QualityTemplateEntity q where templateName in (select distinct templateName from QualityTemplateEntity where templateId= :templateId) ")
	List<QualityTemplateEntity> findByTemplateName(@Param("templateId") Integer templateId);

	@Query("select distinct q from QualityTemplateEntity q where q.templateName = :templateName and q.stageName = :stageName ")
	 QualityTemplateEntity findByTemplateNameAndStageName(@Param("templateName") String templateName,
			@Param("stageName") String stageName);

	@Query("select distinct q from QualityTemplateEntity q where templateName in (select distinct templateName from QualityTemplateEntity where 1=1) ")
	List<QualityTemplateEntity> findAllTemplates();

	void deleteByTemplateName(@Param("templateName") String templateName);

}
