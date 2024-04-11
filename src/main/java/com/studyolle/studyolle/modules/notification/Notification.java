package com.studyolle.studyolle.modules.notification;

import com.studyolle.studyolle.modules.account.Account;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String link;

    private String message;

    private boolean checked;

    @ManyToOne
    private Account account;

    private LocalDateTime createdLocalDateTime;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( o == null || Hibernate.getClass( this ) != Hibernate.getClass( o ) ) return false;
        Notification that = ( Notification ) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode () {
        return 0;
    }
}
