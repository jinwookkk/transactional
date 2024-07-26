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
public class TransactionalReadCommittedService {
    @PersistenceContext
    EntityManager entityManager;
    private final SimpleRepository simpleRepository;

    @Transactional
    public SimpleDto save(SimpleDto simpleDto) {
        return simpleRepository.save(simpleDto);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Void exceptionAfterTimeSaveWithTransactional(SimpleDto simpleDto, int seconds) {
        simpleRepository.save(simpleDto);
        System.out.println("saved");
        try {
            Thread.sleep(1000 * seconds);
        } catch (Exception e) {

        }

        System.out.println("throw exception");
        throw new IllegalArgumentException();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<SimpleDto> getSimpleDtoBySimpleData(Integer simpleData) {
        return simpleRepository.findBySimpleData(simpleData);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Void getTwiceByIdUsingEm(Integer id, Integer seconds) throws Exception {
        System.out.println("simpleData before update on transaction 1: " + findByIdUsingEm(id));
        Thread.sleep(1000 * seconds);
        System.out.println("simpleData after update on transaction 1: " + findByIdUsingEm(id));

        return null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Void getTwiceByIdUsingAndClearEm(Integer id, Integer seconds) {
        System.out.println("simpleData before update on transaction 1: " + findByIdUsingEm(id));
        try {
            Thread.sleep(1000 * seconds);
        } catch (Exception e) {

        }
        entityManager.clear(); // Clear 추가 !!
        System.out.println("simpleData after update on transaction 1: " + findByIdUsingEm(id));

        return null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SimpleDto findByIdUsingEm(Integer id) {
        return entityManager.createQuery("SELECT s FROM SimpleDto s WHERE s.id = :id", SimpleDto.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Void getTwiceById(Integer id, Integer seconds) throws Exception {
        System.out.println("simpleData before update on transaction 1: " + simpleRepository.findById(id).get().getSimpleData());
        Thread.sleep(1000 * seconds);
        System.out.println("simpleData after update on transaction 1: " + simpleRepository.findById(id).get().getSimpleData());

        return null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Void getTwiceByValue(Integer simpleData, Integer seconds) throws Exception {
        System.out.println("simpleData before update on transaction 1: " + simpleRepository.findBySimpleData(simpleData));
        Thread.sleep(1000 * seconds);
        System.out.println("simpleData after update on transaction 1: " + simpleRepository.findBySimpleData(simpleData));

        return null;
    }
}
