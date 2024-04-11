package com.studyolle.studyolle.modules.event.form;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.event.EventType;
import com.studyolle.studyolle.modules.study.Study;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Event {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    private LocalDateTime createdDateTime;

    private LocalDateTime endEnrollmentDateTime;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private Integer limitOfEnrollments;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event")
    private List<Enrollment> enrollmentList;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public Event ( EventForm eventForm ) {
        copyProperties(eventForm, this);
    }

    public boolean canAccept(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollmentList.contains( enrollment )
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    public boolean canReject(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollmentList.contains( enrollment )
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }

    public long getNumberOfAcceptedEnrollments() {
        return this.enrollmentList.stream().filter( Enrollment::isAccepted ).count();
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollmentList.add( enrollment );
        enrollment.setEvent(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollmentList.remove( enrollment );
        enrollment.setEvent( null );
    }

    public boolean isAbleToAcceptWatingEnrollment() {
        return this.eventType == EventType.FCFS && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
    }

    public Enrollment getTheFirstWatingEnrollment() {
        return this.enrollmentList.stream().filter( enrollment -> !enrollment.isAccepted() ).findFirst().orElseGet( Enrollment::new );
    }

    private List<Enrollment> getWaitingList() {
        return this.enrollmentList.stream().filter( enrollment -> !enrollment.isAccepted() ).collect( Collectors.toList());
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( o == null || Hibernate.getClass( this ) != Hibernate.getClass( o ) ) return false;
        Event event = ( Event ) o;
        return Objects.equals( id, event.id );
    }

    @Override
    public int hashCode () {
        return 0;
    }

    public void acceptWaitingList () {
        if(this.isAbleToAcceptWatingEnrollment()) {
            var waitingList = getWaitingList();
            int numberToAccept = (int) Math.min( this.limitOfEnrollments - this.getNumberOfAcceptedEnrollments(), waitingList.size() );
            waitingList.subList( 0, numberToAccept ).forEach( e -> e.setAccepted( true ) );
        }
    }

    public void acceptNextWaitingEnrollment () {
        if(this.isAbleToAcceptWatingEnrollment()) {
            Enrollment enrollmentToAccept = this.getTheFirstWatingEnrollment();
            if(enrollmentToAccept.getId() != null) {
                enrollmentToAccept.setAccepted( true );
            }
        }
    }
}
