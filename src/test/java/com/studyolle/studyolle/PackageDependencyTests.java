package com.studyolle.studyolle;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = StudyolleApplication.class)
public class PackageDependencyTests {

    private static final String STUDY = "..modules.study..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";
    private static final String MAIN = "..modules.main..";

    @ArchTest
    ArchRule studyPackageRule = classes().that().resideInAPackage( STUDY )
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage( STUDY, EVENT, MAIN );

    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage( EVENT )
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage( STUDY, ACCOUNT, EVENT );

    @ArchTest
    ArchRule cycleCheck = slices().matching("com.studyolle.studyolle.modules.(*)..")
            .should().beFreeOfCycles();

}
