package com.wook.transactional;

import com.wook.transactional.dto.SimpleDto;
import com.wook.transactional.repository.SimpleRepository;
import com.wook.transactional.service.TransactionalSerializableService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class Test08_Serializable {
    @Autowired
    SimpleRepository simpleRepository;
    @Autowired
    TransactionalSerializableService transactionalSerializableService;
    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @BeforeEach
    void setUp() {
        simpleRepository.deleteAll();
    }

    @Test
    void serializable() throws Exception {
        final Integer simpleData = 689;

        // Transaction 1
        // PhantomRead 현상은 없으나 Transaction 2가 Transaction 1이 끝나기를 기다려 오래 걸린다.
        executorService.submit(() -> transactionalSerializableService.getAllAndGetAllAndUpdateAndGetAllBySimpleData(simpleData, simpleData + 1));
        Thread.sleep(1000);

        // Transaction 2
        SimpleDto simpleDto = SimpleDto.builder().simpleData(simpleData).build();
        simpleDto = simpleRepository.save(simpleDto);
        System.out.println("inserted id:" + simpleDto.getId());
    }
}
