package com.studyolle.studyolle.modules.account;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

@Component
public class CustomEntityModel<T> extends EntityModel<T> {

    public EntityModel<T> createEntityModel(T t, Link ...links) {
        return of( t, links );
    }

}
