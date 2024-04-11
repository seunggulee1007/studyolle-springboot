package com.studyolle.studyolle.modules.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {

    boolean existsByEmail ( String email );

    boolean existsByNickname ( String nickname );

    Optional<Account> findByEmail ( String email );

    Optional<Account> findByNickname ( String nicknameOrEmail );
}
