package com.wook.transactional;

import com.wook.transactional.dto.SimpleDto;
import com.wook.transactional.repository.SimpleRepository;
import com.wook.transactional.service.TransactionalReadCommittedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootTest
public class Test04_ReadCommitted_NonRepetableRead {
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
        simpleDto = simpleRepository.save(simpleDto);
        int id = simpleDto.getId();
        // NonRepeatable Read 현상으로 값이 다를 거로 추측되지만 실제론 정합성을 보인다.
        Future future = executorService.submit(() -> transactionalReadCommittedService.getTwiceById(id, 1));

        simpleDto.setSimpleData(200);
        simpleRepository.save(simpleDto);
        System.out.println("updated to 200");

        future.get();
    }

    @Test
    void readCommittedFindByValue() throws Exception {
        final Integer simpleData = 100;
        SimpleDto simpleDto = SimpleDto.builder().simpleData(simpleData).build();
        simpleDto = simpleRepository.save(simpleDto);
        // NonRepeatable Read 현상으로 동일 Transaction 내에서 다른 select 결과를 보인다.
        Future future = executorService.submit(() -> transactionalReadCommittedService.getTwiceByValue(simpleData, 1));

        Integer newSimpleData = 200;
        simpleDto.setSimpleData(newSimpleData);
        simpleRepository.save(simpleDto);
        System.out.println("updated to 200");

        future.get();
    }

    @Test
    void readCommittedFindByIdWithEm() {
        final Integer simpleData = 100;
        SimpleDto simpleDto = SimpleDto.builder().simpleData(simpleData).build();
        simpleDto = simpleRepository.save(simpleDto);
        int id = simpleDto.getId();
        executorService.submit(() -> transactionalReadCommittedService.getTwiceByIdUsingEm(id, 1));

        transactionalReadCommittedService.findByIdUsingEm(id);
        Integer newSimpleData = 200;
        simpleDto.setSimpleData(newSimpleData);
        simpleRepository.save(simpleDto);
        System.out.println("updated to 200");
        System.out.println("simpleData on another transaction: " + simpleRepository.findById(id).get().getSimpleData());
    }

    @Test
    void readCommittedFindByIdClearEm() throws Exception {
        final Integer simpleData = 100;
        SimpleDto simpleDto = SimpleDto.builder().simpleData(simpleData).build();
        simpleDto = simpleRepository.save(simpleDto);
        int id = simpleDto.getId();
        // NonRepeatable Read 현상으로 동일 Transaction 내에서 다른 select 결과를 보인다.
        // 이로 추측해보아 JPA Entity Manager에서 id에 대한 Repeatablae Read를 보장하는 무언가가 있음을 알 수 있다.
        Future future = executorService.submit(() -> transactionalReadCommittedService.getTwiceByIdUsingAndClearEm(id, 1));

        transactionalReadCommittedService.findByIdUsingEm(id);
        Integer newSimpleData = 200;
        simpleDto.setSimpleData(newSimpleData);
        simpleRepository.save(simpleDto);
        System.out.println("updated to 200");
        future.get();
    }
}
