package com.mygolfleague.repository

import com.mygolfleague.dto.BasicNewsDto
import com.mygolfleague.model.Season
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

import javax.persistence.EntityManager

@CompileStatic
@Repository
abstract class NewsRepository implements CrudRepository<Season, String>{
    private final EntityManager entityManager

    NewsRepository(EntityManager entityManager ){
        this.entityManager = entityManager
    }
    @Query( value = "select n.id, n.title, n.summary, n.publishedDate, n.lastUpdatedDate, concat( u.firstName, ' ', u.lastName ) author, concat( u2.firstName, ' ', u2.lastName ) updatedBy from newsitem n join user u on n.authorId = u.id join user u2 on n.updatedById = u2.id where n.leagueId = :id order by publishedDate desc limit :count",
        nativeQuery = true )
    abstract List<BasicNewsDto> getLatestNews(String id, int count=3)
}
