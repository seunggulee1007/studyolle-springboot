package com.studyolle.studyolle.modules.study;

import com.studyolle.studyolle.commons.BaseForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudyForm extends BaseForm {

    @NotBlank
    @Length(min = 2, max = 20)
    @Pattern( regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$")
    private String path;

    @NotBlank
    @Length(max = 50)
    private String title;

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    private String fullDescription;

}
