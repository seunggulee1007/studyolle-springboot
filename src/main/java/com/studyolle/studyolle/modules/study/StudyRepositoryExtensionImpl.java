package com.studyolle.studyolle.modules.study;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.studyolle.studyolle.modules.account.QTag;
import com.studyolle.studyolle.modules.account.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.querydsl.core.types.Projections.list;

public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {

    public StudyRepositoryExtensionImpl (  ) {
        super( Study.class );
    }

    @Override
    public List<Study> findByKeyword ( String keyword ) {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where( study.published.isTrue()
                .and(study.title.containsIgnoreCase( keyword ))
                .or(study.tags.any().title.containsIgnoreCase( keyword ))
                .or(study.zones.any().localNameOfCity.containsIgnoreCase( keyword ))
        ).leftJoin( study.tags, QTag.tag ).fetchJoin()
        .distinct();
        return query.fetch();
    }

    @Override
    public List<Study> findByKeywordByTransform( String keyword ) {
        QStudy study = QStudy.study;
        Map<Study, Set<Tag>> transform = from( study ).where( study.published.isTrue()
                    .and(study.title.containsIgnoreCase( keyword ))
                    .or(study.tags.any().title.containsIgnoreCase( keyword ))
                    .or(study.zones.any().localNameOfCity.containsIgnoreCase( keyword ))
            ).leftJoin( study.tags, QTag.tag )
            .transform( groupBy(study).as(set( QTag.tag)) );
        return transform.entrySet().stream().map(
                entry-> new Study(entry.getKey().getId(), entry.getValue())
        ).collect( Collectors.toList());
    }

    @Override
    public Page<Study> findByKeywordAndPaging ( String keyword, Pageable pageable ) {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where( study.published.isTrue()
                .and(study.title.containsIgnoreCase( keyword ))
                .or(study.tags.any().title.containsIgnoreCase( keyword ))
                .or(study.zones.any().localNameOfCity.containsIgnoreCase( keyword ))
        ).leftJoin( study.tags, QTag.tag ).fetchJoin()
        .distinct();
        JPQLQuery<Study> pageableQuery = Objects.requireNonNull( getQuerydsl() ).applyPagination( pageable, query );
        QueryResults<Study> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>( fetchResults.getResults(), pageable, fetchResults.getTotal() );
    }
}
