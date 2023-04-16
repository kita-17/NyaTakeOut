package com.watson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watson.entity.EmployeeEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<EmployeeEntity> {
}
