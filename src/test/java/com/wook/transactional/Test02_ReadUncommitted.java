package com.wook.transactional;

import com.wook.transactional.dto.SimpleDto;
import com.wook.transactional.repository.SimpleRepository;
import com.wook.transactional.service.TransactionalReadUncommittedService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class Test02_ReadUncommitted {
    @Autowired
    SimpleRepository simpleRepository;
    @Autowired
    TransactionalReadUncommittedService transactionalReadUncommittedService;

    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @BeforeEach
    void setUp() {
        simpleRepository.deleteAll();
    }

    @Test
    void readUncommitted() throws Exception {
        Integer simpleData = 100;
        SimpleDto simpleDto = SimpleDto.builder().simpleData(simpleData).build();
        // Transaction 중 실패하여 save는 최종적으로 반영되지 않는다.
        executorService.submit(() -> transactionalReadUncommittedService.exceptionAfterTimeSaveWithTransactional(simpleDto, 1));
        Thread.sleep(100);
        // 하지만 save와 exception 사이에는 조회된다.
        int size1 = transactionalReadUncommittedService.getSimpleDtoBySimpleData(simpleData).size();
        System.out.println("find by simpleData result before exception: " + size1);
        Assertions.assertEquals(1, size1);

        Thread.sleep(1000 * 2);
        // exception 이후에는 반영되지 않았다.
        int size2 = transactionalReadUncommittedService.getSimpleDtoBySimpleData(simpleData).size();
        System.out.println("find by simpleData result after exception: " + size2);
        Assertions.assertEquals(0, size2);
    }
}
