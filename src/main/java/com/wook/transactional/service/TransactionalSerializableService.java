package com.wook.transactional.service;

import com.wook.transactional.dto.SimpleDto;
import com.wook.transactional.repository.SimpleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionalSerializableService {
    @Autowired
    SimpleRepository simpleRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Void getAllAndGetAllAndUpdateAndGetAllBySimpleData(Integer originSimpleData, Integer newSimpleData) throws Exception {
        System.out.println("Size of first query: " + simpleRepository.findAll().size());
        Thread.sleep(1000 * 3);
        System.out.println("Size of second query: " + simpleRepository.findAll().size());
        Thread.sleep(1000 * 5);
        System.out.println("Updated size: " + simpleRepository.updateBySimpleData(originSimpleData, newSimpleData));
        System.out.println("Size of fourth query: " + simpleRepository.findAll().size());

        return null;
    }
}
