package com.studyolle.studyolle.modules.event;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.AccountService;
import com.studyolle.studyolle.modules.account.CustomEntityModel;
import com.studyolle.studyolle.modules.event.form.Event;
import com.studyolle.studyolle.modules.event.form.EventForm;
import com.studyolle.studyolle.modules.event.validator.EventValidator;
import com.studyolle.studyolle.security.JwtAuthentication;
import com.studyolle.studyolle.modules.study.Study;
import com.studyolle.studyolle.modules.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;
    private final AccountService accountService;

    private final EventService eventService;

    private final EventValidator eventValidator;

    private final EventRepository eventRepository;

    @InitBinder("eventForm")
    public void initBinder( WebDataBinder webDataBinder ) {
        webDataBinder.addValidators( eventValidator );
    }

    private final CustomEntityModel<Event> customEntityModel;

    public ResponseEntity<EventForm> newEventForm( @AuthenticationPrincipal JwtAuthentication authentication, @PathVariable String path ) {
        Account account = accountService.getAccountByAuthentication( authentication );
        Study study = studyService.getStudyToUpdateStatus(account, path);

        return ResponseEntity.ok(EventForm.builder()
                .account( account )
                .study( study )
                .build());
    }

    public ResponseEntity newEventSubmit( @AuthenticationPrincipal JwtAuthentication authentication, @PathVariable String path, @Valid EventForm eventForm, Errors errors ) {
        Account account = accountService.getAccountByAuthentication( authentication );
        Study study = studyService.getStudyToUpdateStatus( account, path );
        if(errors.hasErrors()) {
            eventForm.setAccount( account );
            eventForm.setStudy( study );
            return ResponseEntity.badRequest().body( eventForm );
        }
        Event event = eventService.createEvent(new Event(eventForm), study, account );
        WebMvcLinkBuilder selfLinkBuilder = linkTo( EventController.class ).slash(event.getId());
        URI createdUri = linkTo( EventController.class ).slash( event.getId() ).toUri();
        EntityModel<Event> entityModel = customEntityModel.createEntityModel( event,
                linkTo(methodOn(EventController.class).newEventSubmit( authentication, path, eventForm, errors )).withSelfRel()
                , selfLinkBuilder.withRel("update-event")
        );
        return ResponseEntity.created( createdUri ).body( entityModel );
    }

    @PostMapping("/events/{id}/edit")
    public void updateEventSubmit(@AuthenticationPrincipal JwtAuthentication authentication, @PathVariable String path, @PathVariable("id") Event event, @Valid @RequestBody EventForm eventForm, Errors errors) {
        Account account = accountService.getAccountByAuthentication( authentication );
        Study study = studyService.getStudyToUpdate( account, path );
        eventForm.setEventType( event.getEventType() );

    }

}
