package com.mygolfleague.controller

import com.mygolfleague.repository.StatsService
import groovy.transform.CompileStatic
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule


@CompileStatic
@Secured( SecurityRule.IS_AUTHENTICATED )
@Controller( '/stats' )
class StatsController {
    private final StatsService statsService
    StatsController(  StatsService statsService ){
        this.statsService = statsService
    }

    @Get( uri = '/{userId}/{leagueId}{?seasonId}', produces = MediaType.APPLICATION_JSON )
    HttpResponse list(String userId, String leagueId,  @Nullable String seasonId ){
        return HttpResponse.ok(
                statsService.getUserStats( userId, leagueId, seasonId)
        )
    }
}
