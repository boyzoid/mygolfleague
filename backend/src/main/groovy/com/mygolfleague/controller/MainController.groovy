package com.mygolfleague.controller

import groovy.transform.CompileStatic
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

import java.security.Principal

@CompileStatic
@Secured( SecurityRule.IS_AUTHENTICATED)
@Controller
class MainController {

    @Get( uri = '/', produces = MediaType.APPLICATION_JSON)
    HttpResponse main( @Nullable Principal principal ){
        var result = [ loggedIn : principal != null]
        return HttpResponse.ok(
                result
        )
    }

}
