package com.studyolle.studyolle.commons;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;


public class BaseController {
    protected ResponseEntity<?> badRequest ( EntityModel<Errors> errorResource ) {
        return ResponseEntity.badRequest().body( errorResource );
    }

}
