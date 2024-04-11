package com.studyolle.studyolle.modules.study;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.zone.Zone;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.beans.BeanUtils.copyProperties;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class StudyDto {

    private Long id;

    @Builder.Default
    private Set<Account> managers = new HashSet<>();

    @Builder.Default
    private Set<Account> members = new HashSet<>();

    private String path;

    private String title;

    private String shortDescription;

    private String fullDescription;

    private String image;

    @Builder.Default
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    private String redirectUrl;

    private Account account;

    public StudyDto ( Study study) {
        copyProperties(study, this);
    }

}
