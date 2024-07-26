package com.wook.transactional.service;

import com.wook.transactional.dto.SimpleDto;
import com.wook.transactional.repository.SimpleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionalRepeatableReadService {
    @PersistenceContext
    EntityManager entityManager;
    private final SimpleRepository simpleRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Void getTwiceByValue(Integer simpleData, Integer seconds) throws Exception {
        System.out.println("simpleData before update on transaction 1: " + simpleRepository.findBySimpleData(simpleData));
        Thread.sleep(1000 * seconds);
        System.out.println("simpleData after update on transaction 1: " + simpleRepository.findBySimpleData(simpleData));

        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Void getAllAndGetAllAndUpdateAndGetAllBySimpleData(Integer originSimpleData, Integer newSimpleData) throws Exception {
        System.out.println("Size of first query: " + simpleRepository.findAll().size());
        Thread.sleep(1000 * 1);
        System.out.println("Size of second query: " + simpleRepository.findAll().size());
        System.out.println("Updated size: " + simpleRepository.updateBySimpleData(originSimpleData, newSimpleData));
        System.out.println("Size of fourth query: " + simpleRepository.findAll().size());

        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Void getSimpleDtoTwiceBySimpleDataGreaterThan(Integer simpleData) throws Exception {
        System.out.println("Size of first query: " + simpleRepository.findBySimpleDataGreaterThanEqual(simpleData).size());
        Thread.sleep(1000 * 5);
        System.out.println("Size of second query: " + simpleRepository.findBySimpleDataGreaterThanEqual(simpleData).size());

        return null;
    }
    public SimpleDto findByIdUsingEm(Integer id) {
        return entityManager.createQuery("SELECT s FROM SimpleDto s WHERE s.id = :id", SimpleDto.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Transactional
    public SimpleDto save(SimpleDto simpleDto) {
        return simpleRepository.save(simpleDto);
    }
}
