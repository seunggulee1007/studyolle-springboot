package com.studyolle.studyolle.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorsDto {

    private int code;

    private String defaultMessage;

    private String rejectedValue;

    private String field;

    private String objectName;

}
