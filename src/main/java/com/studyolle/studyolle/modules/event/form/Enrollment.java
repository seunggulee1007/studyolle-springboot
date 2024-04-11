package com.studyolle.studyolle.modules.event.form;

import com.studyolle.studyolle.modules.account.Account;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( o == null || Hibernate.getClass( this ) != Hibernate.getClass( o ) ) return false;
        Enrollment that = ( Enrollment ) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode () {
        return 0;
    }
}