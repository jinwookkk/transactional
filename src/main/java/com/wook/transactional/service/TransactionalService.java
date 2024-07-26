package com.wook.transactional.service;

import com.wook.transactional.dto.SimpleDto;
import com.wook.transactional.repository.SimpleRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionalService {
    private final SimpleRepository simpleRepository;

    @Transactional
    public Void exceptionAfterSaveWithTransactional(SimpleDto simpleDto) {
        simpleRepository.save(simpleDto);
        throw new IllegalArgumentException();
    }
}
