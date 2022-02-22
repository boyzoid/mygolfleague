package com.mygolfleague.repository

import com.mygolfleague.dto.RulesDto
import com.mygolfleague.dto.SeasonDtoBasic
import com.mygolfleague.model.Season
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

import javax.persistence.EntityManager

@CompileStatic
@Repository
abstract class SeasonRepository implements CrudRepository<Season, String>{
    private final EntityManager entityManager

    SeasonRepository(EntityManager entityManager ){
        this.entityManager = entityManager
    }
    @Query(value = "select s.id, s.name from season s join league l on l.id=s.leagueId where l.id = :id order by s.startDate desc",
            nativeQuery = true)
    abstract List<SeasonDtoBasic> findAllLeagueId(String id);

    @Query( value = "select id, name, rules, publishRules from season where season.id = :id", nativeQuery = true)
    abstract RulesDto getRules( String id )
}
