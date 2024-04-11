package com.studyolle.studyolle.commons;

import com.studyolle.studyolle.modules.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ErrorsResource extends EntityModel<Errors> {

    public EntityModel<Errors> createErrorResource ( Errors content, Link... links ) {
        EntityModel<Errors> errorsEntityModel = of( content, links );
        errorsEntityModel.add( linkTo( methodOn( IndexController.class ).index() ).withRel( "index" ) );
        return errorsEntityModel;
    }

}
