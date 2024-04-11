package com.studyolle.studyolle.modules.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

import static org.springframework.beans.BeanUtils.copyProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyDescriptionForm {

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;

    public StudyDescriptionForm(Study study) {
        copyProperties(study, this);
    }

}
