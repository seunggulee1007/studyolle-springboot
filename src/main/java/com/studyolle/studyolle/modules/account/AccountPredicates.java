package com.studyolle.studyolle.modules.account;

import com.querydsl.core.types.Predicate;
import com.studyolle.studyolle.modules.zone.Zone;

import java.util.Set;

public class AccountPredicates {

    public static Predicate findByTagsAndZones( Set<Tag> tags, Set<Zone> zones) {

        QAccount account = QAccount.account;
        return account.zones.any().in( zones ).and( account.tags.any().in( tags ) );
    }
}
