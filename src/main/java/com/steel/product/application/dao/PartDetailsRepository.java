package com.steel.product.application.dao;

import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.PartDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartDetailsRepository extends JpaRepository<PartDetails, Integer> {

    List<PartDetails> getByPartDetailsId(String partDetailsId);

    @Query("select pd from PartDetails pd left join fetch pd.instructions ins left join fetch ins.inwardId where pd.partDetailsId = :partDetailsId")
    public List<PartDetails> findAllByPartDetailsId(@Param("partDetailsId") String partDetailsId);



}
