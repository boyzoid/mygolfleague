package com.mygolfleague.controller


import com.mygolfleague.repository.NewsRepository
import groovy.transform.CompileStatic
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@CompileStatic
@Secured( SecurityRule.IS_AUTHENTICATED )
@Controller( '/news' )
class NewsController {
    private final NewsRepository newsRepository

    NewsController(NewsRepository newsRepository ){
        this.newsRepository = newsRepository
    }
    @Get( uri = '/latest/{id}', produces = MediaType.APPLICATION_JSON )
    HttpResponse list( String id ){
        var result = [ seasons: newsRepository.getLatestNews( id ) ]
        return HttpResponse.ok(
                result
        )
    }
}
