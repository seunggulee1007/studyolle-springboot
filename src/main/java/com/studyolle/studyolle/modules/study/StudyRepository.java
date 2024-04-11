package com.studyolle.studyolle.modules.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryExtension {

    boolean existsByPath(String path);

    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Study> findByPath ( String path );

    @EntityGraph(value = "Study.withTagsAndManagers", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Study> findStudyWithTagsByPath ( String path );

    @EntityGraph(value = "Study.withZonesAndManagers", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Study> findStudyWithZonesByPath(String path);

    @EntityGraph(value = "Study.withManagers", type = EntityGraph.EntityGraphType.LOAD)
    Study findStudyWithManagersByPath ( String path );

    Study findStudyOnlyByPath(String path);

    @EntityGraph(value = "Study.withTagsAndZones", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Study> findStudyWithTagsAndZonesById ( Long id );

    @EntityGraph(attributePaths = {"members", "managers"})
    Optional<Study> findStudyWithManagersAndMembersById ( Long id );

}
