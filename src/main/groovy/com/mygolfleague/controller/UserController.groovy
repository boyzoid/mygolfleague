package com.mygolfleague.controller

import com.mygolfleague.repository.LeagueRepository
import com.mygolfleague.repository.UserRepository
import groovy.transform.CompileStatic
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.session.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.security.Principal

@CompileStatic
@Secured( SecurityRule.IS_AUTHENTICATED )
@Controller( '/user' )
class UserController {
    private final UserRepository userRepository
    private final LeagueRepository leagueRepository
    private static final String ATTR_LEAGUE= "league"
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
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
    HttpResponse user(Session session, Principal principal ){
        def ret = userRepository.getUserInit( principal.name )
        return HttpResponse.ok(
                [ user: ret ]
        )
    }
}
