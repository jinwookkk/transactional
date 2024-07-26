package com.wook.transactional.service;

import com.wook.transactional.dto.SimpleDto;
import com.wook.transactional.repository.SimpleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionalReadUncommittedService {
    private final SimpleRepository simpleRepository;

    @Transactional
    public SimpleDto save(SimpleDto simpleDto) {
        return simpleRepository.save(simpleDto);
    }


    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Void exceptionAfterTimeSaveWithTransactional(SimpleDto simpleDto, int seconds) throws Exception {
        simpleRepository.save(simpleDto);
        System.out.println("saved");
        Thread.sleep(1000 * seconds);

        System.out.println("throw exception");
        throw new IllegalArgumentException();
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public List<SimpleDto> getSimpleDtoBySimpleData(Integer simpleData) {
        return simpleRepository.findBySimpleData(simpleData);
    }
}
