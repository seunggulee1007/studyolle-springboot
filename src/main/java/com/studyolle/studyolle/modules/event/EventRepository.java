package com.studyolle.studyolle.modules.event;

import com.studyolle.studyolle.modules.event.form.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

}
