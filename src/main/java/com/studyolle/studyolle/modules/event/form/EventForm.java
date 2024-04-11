package com.studyolle.studyolle.modules.event.form;

import com.studyolle.studyolle.commons.BaseForm;
import com.studyolle.studyolle.modules.event.EventType;
import com.studyolle.studyolle.modules.study.Study;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EventForm extends BaseForm {

    @NotBlank
    @Length(max = 50)
    private String title;

    private String description;

    @Builder.Default
    private EventType eventType = EventType.FCFS;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endEnrollmentDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    @Min( 2 )
    @Builder.Default
    private Integer limitOfEnrollments = 2;

    private Study study;

}
