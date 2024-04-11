package com.studyolle.studyolle.modules.study.event;

import com.studyolle.studyolle.modules.study.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyUpdateEvent {

    private final Study study;

    private final String message;

}
