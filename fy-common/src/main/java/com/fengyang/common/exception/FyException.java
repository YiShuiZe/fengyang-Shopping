package com.fengyang.common.exception;

import com.fengyang.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FyException extends RuntimeException{

    private ExceptionEnum exceptionEnum;
}
