package com.studyolle.studyolle.modules.tag;

import com.studyolle.studyolle.modules.account.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByTitle ( String title );
}
