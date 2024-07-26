package com.wook.transactional;

import com.wook.transactional.dto.SimpleDto;
import com.wook.transactional.repository.SimpleRepository;
import com.wook.transactional.service.TransactionalReadCommittedService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class Test03_ReadCommitted {
    @Autowired
    SimpleRepository simpleRepository;
    @Autowired
    TransactionalReadCommittedService transactionalReadCommittedService;
    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @BeforeEach
    void setUp() {
        simpleRepository.deleteAll();
    }

    @Test
    void readCommitted() throws Exception {
        Integer simpleData = 100;
        SimpleDto simpleDto = SimpleDto.builder().simpleData(simpleData).build();
        // Transaction 중 실패하여 save는 최종적으로 반영되지 않는다.
        executorService.submit(() -> transactionalReadCommittedService.exceptionAfterTimeSaveWithTransactional(simpleDto, 1));
        Thread.sleep(100);
        // commit 되지 않았으므로 exception 이전이어도 새로운 transaction에서 조회되지 않는다.
        int size1 = transactionalReadCommittedService.getSimpleDtoBySimpleData(simpleData).size();
        System.out.println("find by simpleData result before exception: " + size1);
        Assertions.assertEquals(0, size1);

        Thread.sleep(1000 * 2);
        // exception 이후에도 반영되지 않았다.
        int size2 = transactionalReadCommittedService.getSimpleDtoBySimpleData(simpleData).size();
        System.out.println("find by simpleData result after exception: " + size2);
        Assertions.assertEquals(0, size2);
    }
}
