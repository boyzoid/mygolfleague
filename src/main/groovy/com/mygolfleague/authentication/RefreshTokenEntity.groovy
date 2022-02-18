package com.mygolfleague.authentication

import groovy.transform.CompileStatic
import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import java.time.Instant

@CompileStatic
@MappedEntity
class RefreshTokenEntity {

    @Id
    @GeneratedValue
    @NonNull
    Long id

    @NonNull
    @NotBlank
    String username

    @NonNull
    @NotBlank
    String refreshToken

    @NonNull
    @NotNull
    Boolean revoked

    @DateCreated
    @NonNull
    @NotNull
    Instant dateCreated

}