package com.mygolfleague.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import groovy.transform.MapConstructor
import io.micronaut.data.annotation.AutoPopulated
import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated
import org.jsoup.Jsoup

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.persistence.Table
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Entity
@CompileStatic
@Table( name= "newsitem" )
@MapConstructor( noArg = true )
class News {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    String id

    @Column(name = "title", nullable = false)
    String title

    @Lob
    @Column(name = "body", nullable = false)
    String body

    @Column(name = "publishedDate")
    Date publishedDate

    @DateCreated
    @Column(name = "createdDate")
    Date createdDate

    @Column(name = "released", nullable = false)
    Boolean released = false;

    @DateUpdated
    @Column(name = "lastUpdatedDate")
    Date lastUpdatedDate

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "authorId")
    User author

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "updatedById")
    User editor

    @Column(name = "emailSent", nullable = false)
    Boolean emailSent = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leagueId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    League league

    @Lob
    @Column(name = "summary")
    String summary

    @PrePersist
    void prePersist(){
        if( this.id == null ) this.id = UUID.randomUUID().toString()
        this.summary = stripHtml( this.body )
        if( this.released && this.publishedDate == null ){
            this.publishedDate = new Date()
        }
    }
    @PreUpdate
    void preUpdate(){
        this.summary = stripHtml( this.body )
        if( this.released && this.publishedDate == null ){
            this.publishedDate = new Date()
        }
    }

    String stripHtml( String body ){
        String summary
        String rawText = Jsoup.parse( body ).text()
        List<String> splitText = rawText.tokenize(" " )
        if( splitText.size() < 30 ){
            summary = splitText.join(" ")
        }
        else{
            summary = splitText.subList( 0, 29 ).join(" ") + '...'
        }
        return summary
    }
}
