package com.studyolle.studyolle.modules.study.event;

import com.studyolle.studyolle.commons.AppProperties;
import com.studyolle.studyolle.infra.mail.EmailMessage;
import com.studyolle.studyolle.infra.mail.EmailService;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.AccountPredicates;
import com.studyolle.studyolle.modules.account.AccountRepository;
import com.studyolle.studyolle.modules.notification.Notification;
import com.studyolle.studyolle.modules.notification.NotificationRepository;
import com.studyolle.studyolle.modules.notification.NotificationType;
import com.studyolle.studyolle.modules.study.Study;
import com.studyolle.studyolle.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Optional<Study> study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        if(study.isPresent()) {
            Study newStudy = study.get();
            log.info(newStudy.getTitle() + "is created." );
            Iterable<Account> accounts = accountRepository.findAll( AccountPredicates.findByTagsAndZones( newStudy.getTags(), newStudy.getZones() ) );
            accounts.forEach( account-> {
                if(account.isStudyCreatedByEmail()) {
                    sendStudyCreatedEmail( newStudy, account, "새로운 스터디가 생겼습니다.", "스터디 올래, '" + newStudy.getTitle() + "' 스터디가 생겼습니다." );
                }
                if(account.isStudyCreatedByWeb()) {
                    createNotification( newStudy, account, newStudy.getShortDescription(), NotificationType.STUDY_CREATED );
                }
            } );
        }
    }

    @EventListener
    public void handleStudyUpdateEvent(StudyUpdateEvent studyUpdateEvent) {
        Optional<Study> study = studyRepository.findStudyWithManagersAndMembersById(studyUpdateEvent.getStudy().getId());
        if(study.isPresent()) {
            Study newStudy = study.get();
            Set<Account> accounts = new HashSet<>();
            accounts.addAll( newStudy.getManagers() );
            accounts.addAll( newStudy.getMembers() );

            accounts.forEach( account->{
                if (account.isStudyUpdatedByEmail()) {
                    sendStudyCreatedEmail( newStudy, account, studyUpdateEvent.getMessage(), "스터디 올래, '" + newStudy.getTitle() + "' 스터디에 새소식이 있습니다." );
                }
                if (account.isStudyUpdatedByWeb()) {
                    createNotification( newStudy, account, studyUpdateEvent.getMessage(), NotificationType.STUDY_UPDATED );
                }
            } );
        }

    }
    private void createNotification ( Study study, Account account, String message, NotificationType notificationType ) {
        Notification notification = Notification.builder()
                .title( study.getTitle() )
                .link( "/study/"+ study.getEncodedPath() )
                .checked( false )
                .createdLocalDateTime( LocalDateTime.now() )
                .message( message )
                .account( account )
                .notificationType( notificationType )
            .build();
        notificationRepository.save( notification );
    }

    private void sendStudyCreatedEmail ( Study study, Account account, String contextMessage, String emailSubject ) {
        Context context = new Context();
        context.setVariable( "nickname", account.getNickname() );
        context.setVariable( "link", "/study/" + study.getEncodedPath() );
        context.setVariable( "linkName", study.getTitle());
        context.setVariable( "message", contextMessage);
        context.setVariable( "host", appProperties.getHost() );
        String message = templateEngine.process( "mail/simple-link", context );
        EmailMessage emailMessage = EmailMessage.builder()
                .subject( emailSubject )
                .to( account.getEmail() )
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }
}
