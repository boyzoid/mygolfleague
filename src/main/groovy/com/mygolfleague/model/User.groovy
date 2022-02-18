package com.mygolfleague.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import groovy.transform.MapConstructor
import io.micronaut.data.annotation.AutoPopulated

import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@CompileStatic
@Table( name= "user" )
@MapConstructor( noArg = true )
class User {
    @Id
    @AutoPopulated
    String id
    @NotNull
    @Size(min=1, max=50)
    @Column( name = 'firstName')
    String firstName
    @NotNull
    @Size(min=1, max=50)
    @Column( name = 'lastName' )
    String lastName
    @NotNull
    @Size(min=1, max=50)
    @Column( name = 'emailAddress' )
    String emailAddress
    @NotNull
    @Size( min=1, max=255 )
    @Column( name='password' )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password

    @ManyToMany( fetch = FetchType.EAGER )
    @JoinTable(name = "userleague",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "leagueId"))
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    List<League> leagues = new ArrayList<>()
}
