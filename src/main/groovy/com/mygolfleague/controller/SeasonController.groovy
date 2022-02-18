package com.mygolfleague.controller

import com.mygolfleague.repository.LeagueRepository
import com.mygolfleague.repository.SeasonRepository
import com.mygolfleague.repository.UserRepository
import groovy.transform.CompileStatic
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

import java.security.Principal

@CompileStatic
@Secured( SecurityRule.IS_AUTHENTICATED )
@Controller( '/season' )
class SeasonController {
    private final SeasonRepository seasonRepository

    SeasonController( SeasonRepository seasonRepository ){
        this.seasonRepository = seasonRepository
    }
    @Get( uri = '/list/{id}', produces = MediaType.APPLICATION_JSON )
    HttpResponse list( String id ){
        var result = [ seasons: seasonRepository.findAllLeagueId( id ) ]
        return HttpResponse.ok(
                result
        )
    }
}
