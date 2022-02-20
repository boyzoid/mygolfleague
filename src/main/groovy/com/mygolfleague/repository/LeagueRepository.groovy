package com.mygolfleague.repository

import com.mygolfleague.dto.BasicLeagueDto
import com.mygolfleague.model.League
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

import javax.persistence.EntityManager

@CompileStatic
@Repository
abstract class LeagueRepository implements CrudRepository<League, String>{
    private final EntityManager entityManager

    LeagueRepository(EntityManager entityManager ){
        this.entityManager = entityManager
    }
    @Query(value = "select l.id, l.name from league l join userleague ul on l.id=ul.leagueId join user u on ul.userId=u.id where u.id=:id and l.enabled=1 order by l.name",
            nativeQuery = true)
    abstract List<BasicLeagueDto> findAllByUserId(String id);
}
