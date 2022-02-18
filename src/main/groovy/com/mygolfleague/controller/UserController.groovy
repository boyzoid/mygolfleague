package com.mygolfleague.controller

import com.mygolfleague.repository.LeagueRepository
import com.mygolfleague.repository.UserRepository
import groovy.transform.CompileStatic
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

import java.security.Principal

@CompileStatic
@Secured( SecurityRule.IS_AUTHENTICATED )
@Controller( '/user' )
class UserController {
    private final UserRepository userRepository
    private final LeagueRepository leagueRepository
    UserController( UserRepository userRepository, LeagueRepository leagueRepository ){
        this.userRepository = userRepository
        this.leagueRepository = leagueRepository
    }
    @Get( uri = '/list', produces = MediaType.APPLICATION_JSON )
    HttpResponse list( Principal principal ){
        var result = [ id: principal.name, users: userRepository.findByLastName( 'Smith')]
        return HttpResponse.ok(
                result
        )
    }
    @Get( uri = '/', produces = MediaType.APPLICATION_JSON )
    HttpResponse user( Principal principal ){
        var result = [ user: userRepository.getById( principal.name ), leagues : leagueRepository.findAllByUserId( principal.name )]
        return HttpResponse.ok(
                result
        )
    }
}
