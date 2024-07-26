package com.wook.transactional;

import com.wook.transactional.dto.SimpleDto;
import com.wook.transactional.repository.SimpleRepository;
import com.wook.transactional.service.TransactionalRepeatableReadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootTest
public class Test06_RepeatableRead_PhantomRead_Update {
    @Autowired
    SimpleRepository simpleRepository;
    @Autowired
    TransactionalRepeatableReadService transactionalRepeatableReadService;
    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @BeforeEach
    void setUp() {
        simpleRepository.deleteAll();
    }

    @Test
    void repeatableRead_PhantomRead_Update() throws Exception {
        final Integer simpleData = 689;

        // Transaction 1
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        // Repeatable Read에 의해 조회 결과가 없음에도 Update가 되며 이후 조회가 가능해진다.
        Future future = executorService.submit(() -> transactionalRepeatableReadService.getAllAndGetAllAndUpdateAndGetAllBySimpleData(simpleData, simpleData + 1));
        Thread.sleep(500);

        // Transaction 2
        SimpleDto simpleDto = SimpleDto.builder().simpleData(simpleData).build();
        simpleDto = simpleRepository.save(simpleDto);
        System.out.println("inserted id:" + simpleDto.getId());

        future.get();
    }
}
