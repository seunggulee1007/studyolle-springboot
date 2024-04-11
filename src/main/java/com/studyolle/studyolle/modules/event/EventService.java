package com.studyolle.studyolle.modules.event;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.event.form.Enrollment;
import com.studyolle.studyolle.modules.event.form.Event;
import com.studyolle.studyolle.modules.event.form.EventForm;
import com.studyolle.studyolle.modules.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;

    public Event createEvent ( Event event, Study study, Account account ) {
        event.setCreateBy( account );
        event.setCreatedDateTime( LocalDateTime.now());
        event.setStudy( study );
        return eventRepository.save( event );
    }

    public void updateEvent(Event event, EventForm eventForm) {
        copyProperties(eventForm, event);
        event.acceptWaitingList();
    }

    public void deleteEvent(Event event) {
        eventRepository.delete( event );
    }

    public void newEnrollment(Event event, Account account) {
        if(!enrollmentRepository.existsByEventAndAccount(event, account)) {
            Enrollment enrollment = Enrollment.builder()
                    .enrolledAt( LocalDateTime.now() )
                    .accepted( event.isAbleToAcceptWatingEnrollment() )
                    .account( account )
                    .build();
            event.addEnrollment( enrollment );
            enrollmentRepository.save( enrollment );
        }
    }

    public void cancelEnrollment(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        event.removeEnrollment( enrollment );
        enrollmentRepository.delete( enrollment );
        event.acceptNextWaitingEnrollment();

    }

}
