package com.mygolfleague.controller


import com.mygolfleague.repository.NewsRepository
import groovy.transform.CompileStatic
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

import javax.validation.constraints.Null

@CompileStatic
@Secured( SecurityRule.IS_AUTHENTICATED )
@Controller( '/news' )
class NewsController {
    private final NewsRepository newsRepository

    NewsController(NewsRepository newsRepository ){
        this.newsRepository = newsRepository
    }

    @Get( uri = '/list/{id}/{page}/{size}', produces = MediaType.APPLICATION_JSON )
    HttpResponse search( String id, int size, int page, @Nullable @QueryValue String search ){
        def result
        Pageable pageable = Pageable.from(page, size )
        if( search == null ){
            result = newsRepository.getNews( id, pageable )
        }
        else{
            String searchString = '%' + search + '%'
            result = newsRepository.searchNews( id, searchString, pageable )
        }
        return HttpResponse.ok(
                result
        )
    }
}
