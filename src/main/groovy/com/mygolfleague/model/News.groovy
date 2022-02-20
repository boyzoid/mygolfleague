package com.mygolfleague.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import groovy.transform.MapConstructor
import io.micronaut.data.annotation.AutoPopulated
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.PrePersist
import javax.persistence.Table
import java.time.Instant

@Entity
@CompileStatic
@Table( name= "newsitem" )
@MapConstructor( noArg = true )
class News {
    @Id
    @AutoPopulated
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @Column(name = "title", nullable = false)
    String title;

    @Lob
    @Column(name = "body", nullable = false)
    String body;

    @Column(name = "publishedDate")
    Date publishedDate;

    @Column(name = "createdDate")
    Date createdDate;

    @Column(name = "released", nullable = false)
    Boolean released = false;

    @Column(name = "lastUpdatedDate")
    Date lastUpdatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorId")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updatedById")
    User user1;

    @Column(name = "emailSent", nullable = false)
    Boolean emailSent = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leagueId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    League league;

    @Lob
    @Column(name = "summary")
    String summary;




}
