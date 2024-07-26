package com.wook.transactional.repository;

import com.wook.transactional.dto.SimpleDto;
import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Table(name = "simple_table")
public interface SimpleRepository extends JpaRepository<SimpleDto, Integer> {
    List<SimpleDto> findBySimpleData(Integer simpleData);


    List<SimpleDto> findBySimpleDataGreaterThanEqual(Integer simpleData);

    @Modifying
    @Query(value = "UPDATE SimpleDto t SET t.simpleData = :newSimpleData WHERE t.simpleData = :originSimpleData")
    int updateBySimpleData(Integer originSimpleData, Integer newSimpleData);
}
