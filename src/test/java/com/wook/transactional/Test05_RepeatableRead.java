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
public class Test05_RepeatableRead {
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
    void repeatableRead() throws Exception {
        final Integer simpleData = 100;
        SimpleDto simpleDto = SimpleDto.builder().simpleData(simpleData).build();
        simpleDto = simpleRepository.save(simpleDto);
        int id = simpleDto.getId();
        // 정합성을 보여 RepeatableRead 에서는 Non Repeatable Read 현상이 방지됨을 알 수 있다.
        Future future = executorService.submit(() -> transactionalRepeatableReadService.getTwiceByValue(simpleData, 1));

        Integer newSimpleData = 200;
        simpleDto.setSimpleData(newSimpleData);
        simpleRepository.save(simpleDto);
        System.out.println("updated to 200");

        future.get();
    }

}
