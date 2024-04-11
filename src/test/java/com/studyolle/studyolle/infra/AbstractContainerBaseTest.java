package com.studyolle.studyolle.infra;

import org.testcontainers.containers.PostgreSQLContainer;

public class AbstractContainerBaseTest {

    static final PostgreSQLContainer POSTGRE_SQL_CONTAINER;

    static  {
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer("postgres");
        POSTGRE_SQL_CONTAINER.start();
    }
}
