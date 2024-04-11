package com.studyolle.studyolle.modules.study;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {

    List<Study> findByKeyword( String keyword);

    List<Study> findByKeywordByTransform( String keyword );

    Page<Study> findByKeywordAndPaging ( String keyword, Pageable pageable );

}
