package com.studyolle.studyolle.modules.study;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.study.event.StudyCreatedEvent;
import com.studyolle.studyolle.modules.study.event.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {

    private final StudyRepository studyRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Study createNewStudy ( StudyForm studyForm, Account account ) {
        Study study = Study.builder().build();
        copyProperties( studyForm, study );
        study = studyRepository.save( study );
        study.addManager(account);
        eventPublisher.publishEvent( new StudyCreatedEvent(study) );
        return study;
    }

    public Study getStudyToUpdate ( Account account, String path ) {
        Study study = this.getStudy(path);
        checkIfManager(account, study);
        return study;
    }

    public Study getStudyToUpdateTag(Account account, String path) {
        Study study = studyRepository.findStudyWithTagsByPath(path).orElseThrow( ()-> new IllegalArgumentException( setIllegalArgumentExceptionMessage(path) ) );
        checkIfExistingStudy(path, study);
                
        return study;
    }

    private void checkIfExistingStudy ( String path, Study study ) {
        if(study == null) {
            throw new IllegalArgumentException( setIllegalArgumentExceptionMessage(path) );
        }
    }

    private void checkIfManager(Account account, Study study) {
        if(!study.isManagedBy( account )) {
            throw new AccessDeniedException( "해당 기능을 사용할 수 없습니다." );
        }
    }

    public void publish(Study study) {
        study.publish();
    }

    public Study getStudy ( String path ) {
        return studyRepository.findByPath( path ).orElseThrow(()-> new IllegalArgumentException( setIllegalArgumentExceptionMessage(path) ));
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        copyProperties(studyDescriptionForm, study);
        eventPublisher.publishEvent( new StudyUpdateEvent(study, "스터디 소개를 수정했습니다.") );
    }

    public void close(Study study) {
        study.close();
        eventPublisher.publishEvent( new StudyUpdateEvent(study, "스터디를 종료했습니다.") );
    }

    public void startRecruit(Study study) {
        study.startRecruit();
        eventPublisher.publishEvent( new StudyUpdateEvent(study, "스터디 모집을 시작 했습니다.") );
    }

    public void stopRecruit(Study study) {
        study.stopRecruit();
        eventPublisher.publishEvent( new StudyUpdateEvent(study, "스터디 모집을 종료했습니다.") );
    }

    public void updateStudyPath(Study study, String newPath) { study.setPath(newPath); }

    public boolean isValidTitle(String newTitle) {return newTitle.length() <= 50;}

    public void updateStudyTitle(Study study, String newTitle) { study.setTitle( newTitle );}


    private String setIllegalArgumentExceptionMessage(String path) {
        return path + "에 해당하는 스터디가 없습니다.";
    }

    public Study getStudyToUpdateStatus ( Account account, String path ) {
        Study study = studyRepository.findStudyWithManagersByPath(path);
        checkIfExistingStudy( path, study );
        checkIfManager( account, study );
        return  study;
    }

}
