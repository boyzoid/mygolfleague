package com.mygolfleague.repository

import groovy.transform.CompileStatic
import io.micronaut.core.annotation.Introspected

import javax.inject.Inject
import javax.persistence.EntityManager

@Singleton
public class StatsService  {

    @Inject
    private final EntityManager entityManager

    List getUserStats( String userid, String seasonId ){
        return entityManager.createNativeQuery( "select * from league" ) as List
    }
}
