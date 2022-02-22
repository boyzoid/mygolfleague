package com.mygolfleague.model


import groovy.transform.CompileStatic
import groovy.transform.MapConstructor
import io.micronaut.data.annotation.AutoPopulated
import javax.persistence.Column
import javax.persistence.FetchType
import javax.persistence.Id

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@CompileStatic
@Table( name= "season" )
@MapConstructor( noArg = true )
class Season {
    @Id
    @Column(name = "id")
    @AutoPopulated
    String id

    @Column(name = "name")
    String name
    @Column(name = "fee")
    Double fee

    @Column(name = "greensFees")
    Double greensFees

    @Column(name = "golfersPerTeam")
    Integer golfersPerTeam

    @Column(name = "maxScorePerHole")
    Integer maxScorePerHole

    @Column(name = "maxHandicap")
    Integer maxHandicap

    @Column(name = "pointsPerHole")
    Integer pointsPerHole

    @Column(name = "pointsPerMatch")
    Integer pointsPerMatch

    @Column(name = "maxScoresToUseForHandicap")
    Integer maxScoresToUseForHandicap

    @Column(name = "nummatches")
    Integer nummatches

    @Column(name = "matchesToDelayHandicap")
    Integer matchesToDelayHandicap

    @Column(name = "pointsForForfeit")
    Integer pointsForForfeit

    @Column(name = "blindMatchForForfeit")
    Boolean blindMatchForForfeit = false

    @Column(name = "currentSeason")
    Boolean currentSeason = false

    @Lob
    @Column(name = "rules")
    String rules

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leagueId")
    League league

    @Column(name = "publishRules")
    Boolean publishRules

    @Column(name = "startDate")
    Date startDate

    @Column(name = "initialHandicapsCalculated")
    Boolean initialHandicapsCalculated

    @Column(name = "scoringFile")
    String scoringFile

    @Column(name = "registrationOpen")
    Boolean registrationOpen

    @Column(name = "useSubs")
    Boolean useSubs

    @Column(name = "useContests")
    Boolean useContests

    @Column(name = "maxHandicapHistory")
    Integer maxHandicapHistory

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoringId")
    Scoring scoring*/

    @Column(name = "sub_pool")
    String subPool

    @Column(name = "sub_team_max")
    Integer subTeamMax

    @Column(name = "sub_golfer_max")
    Integer subGolferMax

    @Column(name = "max_forfeit_points")
    Integer maxForfeitPoints

    @Column(name = "min_handicap")
    Integer minHandicap

    /*@OneToMany(mappedBy = "seasonId")
    Set<Week> weeks = new LinkedHashSet<>()*/
}
