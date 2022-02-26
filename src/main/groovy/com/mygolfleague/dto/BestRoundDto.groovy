package com.mygolfleague.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected

@Introspected
class BestRoundDto {

    BestRoundDto( Object item ){
        this.score = item[ 0 ]
        this.datePlayed = item[ 1 ]
        this.courseName = item[ 2 ]
        this.courseAbbrev = item[ 3 ]
        this.holeGroup = item[ 4 ]
        this.golfer = item[ 5 ]
    }

    Integer score
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yyyy")
    Date datePlayed
    String courseName
    String courseAbbrev
    String holeGroup
    String golfer
}
