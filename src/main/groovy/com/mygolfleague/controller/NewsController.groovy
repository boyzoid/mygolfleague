package com.mygolfleague.controller

import com.mygolfleague.model.News
import com.mygolfleague.model.User
import com.mygolfleague.repository.NewsRepository
import com.mygolfleague.repository.UserRepository
import groovy.transform.CompileStatic
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

import javax.validation.constraints.Null
import java.security.Principal

@CompileStatic
@Secured( SecurityRule.IS_AUTHENTICATED )
@Controller( '/news' )
class NewsController {
    private final NewsRepository newsRepository
    private final UserRepository userRepository

    NewsController(NewsRepository newsRepository, UserRepository userRepository ){
        this.newsRepository = newsRepository
        this.userRepository = userRepository
    }

    @Get( uri = '/list/{id}/{page}/{size}{?search}', produces = MediaType.APPLICATION_JSON )

    HttpResponse search( String id, int size, int page, @Nullable String search ){
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
    @Post( uri = '/', produces = MediaType.APPLICATION_JSON )
    HttpResponse put(Principal principal, @Body News news ){
        User user = userRepository.getById( principal.name )
        if( news.id == null ){
            news.author = user
            newsRepository.save( news )
        }
        else{
            news.editor = user
            newsRepository.update( news )
        }
        try{
            def result = [ success : true, news: newsRepository.getById( news.id )]
            return HttpResponse.ok{
                result
            }
        }
        catch( e ){
            def result = [ success : false, error: e ]
            return HttpResponse.ok{
                    result
            }
        }
    }
}
