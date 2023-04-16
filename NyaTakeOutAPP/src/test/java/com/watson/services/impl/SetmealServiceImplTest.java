package com.watson.services.impl;

import com.watson.common.Result;
import com.watson.dto.SetmealDTO;
import com.watson.mapper.SetmealMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class SetmealServiceImplTest {

    @Resource
    private SetmealServiceImpl setmealService;

    @Resource
    private SetmealMapper setmealMapper;

    @Test
    void onFetchData() {
        List<SetmealDTO> setmealList = setmealMapper.getSetmealList(1L, null, false);
        log.info("{}",setmealList);
    }
}