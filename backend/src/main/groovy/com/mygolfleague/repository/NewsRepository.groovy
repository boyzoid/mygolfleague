package com.mygolfleague.repository


import com.mygolfleague.dto.NewsDtoBasic
import com.mygolfleague.model.News
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.repository.CrudRepository

import javax.persistence.EntityManager

@CompileStatic
@Repository
abstract class NewsRepository implements CrudRepository<News, String>{
    private final EntityManager entityManager

    NewsRepository(EntityManager entityManager ){
        this.entityManager = entityManager
    }
    @Query( value = "select n.id, n.title, n.summary, n.publishedDate, n.lastUpdatedDate, concat( u.firstName, ' ', u.lastName ) author, concat( u2.firstName, ' ', u2.lastName ) updatedBy from newsitem n join user u on n.authorId = u.id join user u2 on n.updatedById = u2.id where n.leagueId = :id and n.released = 1 order by publishedDate desc",
        nativeQuery = true,
        countQuery = "select count( id ) from newsitem where leagueId = :id" )
    abstract Page<NewsDtoBasic> getNews(String id, Pageable pageable )

    @Query( value = "select n.id, n.title, n.summary, n.publishedDate, n.lastUpdatedDate, concat( u.firstName, ' ', u.lastName ) author, concat( u2.firstName, ' ', u2.lastName ) updatedBy from newsitem n join user u on n.authorId = u.id join user u2 on n.updatedById = u2.id where n.leagueId = :id and n.released = 1 and n.body like :search order by publishedDate desc",
            nativeQuery = true,
            countQuery = "select count( id ) from newsitem where leagueId = :id and body like :search " )
    abstract Page<NewsDtoBasic> searchNews(String id, String search, Pageable pageable )
    abstract News getById( String id )
}
