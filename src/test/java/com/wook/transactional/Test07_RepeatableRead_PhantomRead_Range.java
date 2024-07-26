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
public class Test07_RepeatableRead_PhantomRead_Range {
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
    void repeatableRead_PhantomRead_Range() throws Exception {
        final Integer simpleData = 666;
        SimpleDto simpleDto = SimpleDto.builder().simpleData(simpleData).build();
        simpleDto = simpleRepository.save(simpleDto);
        System.out.println("inserted id:" + simpleRepository.save(simpleDto).getId());

        // Transaction 1
        /*
        Range 쿼리여서 Phantom Read가 발생해야 하지만 JPA 의 영향으로 발생하지 않는다.
        아래 쿼리를 직접 MySQL에 실행하면 현상을 확인할 수 있다.
        BEGIN;
        SELECT simple_data FROM simple_table WHERE simple_data > 17;
            BEGIN;
            INSERT INTO simple_table VALUES (1, 20);
            COMMIT;
        SELECT simple_data FROM simple_table WHERE simple_data > 17;
        COMMIT;
         */
        Future future = executorService.submit(() -> transactionalRepeatableReadService.getSimpleDtoTwiceBySimpleDataGreaterThan(0));

        Thread.sleep(500);

        // Transaction 2
        SimpleDto simpleDto2 = SimpleDto.builder().simpleData(simpleData + 1).build();
        System.out.println("inserted id:" + simpleRepository.save(simpleDto2).getId());

        future.get();
    }
}
