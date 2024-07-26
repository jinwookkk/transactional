package com.wook.transactional;

import com.wook.transactional.dto.SimpleDto;
import com.wook.transactional.repository.SimpleRepository;
import com.wook.transactional.service.TransactionalService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Test01_Basic {
    @Autowired
    SimpleRepository simpleRepository;
    @Autowired
    TransactionalService transactionalService;

    @BeforeEach
    void setUp() {
        simpleRepository.deleteAll();
    }

    @Test
    void basicCodeWithoutTransactional() {
        try {
            SimpleDto simpleDto = SimpleDto.builder().simpleData(1).build();
            simpleRepository.save(simpleDto);
            throw new IllegalArgumentException();
        } catch (Exception e) {

        }

        // save가 정상적으로 반영된다.
        Assertions.assertEquals(1, simpleRepository.findAll().size());
        System.out.println("size after transaction: " + simpleRepository.findAll().size());
    }

    @Test
    void basicCodeWithTransactional() {
        try {
            SimpleDto simpleDto = SimpleDto.builder().simpleData(10).build();
            transactionalService.exceptionAfterSaveWithTransactional(simpleDto);
        } catch (Exception e) {

        }

        // Transaction 중 실패하여, save는 반영되지 않는다.
        Assertions.assertEquals(0, simpleRepository.findAll().size());
        System.out.println("size after transaction: " + simpleRepository.findAll().size());
    }
}
