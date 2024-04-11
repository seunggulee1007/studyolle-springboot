package com.studyolle.studyolle.modules.event;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.event.form.Enrollment;
import com.studyolle.studyolle.modules.event.form.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByEventAndAccount ( Event event, Account account );

    Enrollment findByEventAndAccount ( Event event, Account account );

}
