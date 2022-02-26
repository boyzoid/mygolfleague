package com.mygolfleague.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected

@Introspected
class AcesDto {
    AcesDto( Integer holeNumber, Date datePlayed, String golfer, String course ){
        this.holeNumber = holeNumber
        this.datePlayed = datePlayed
        this.golfer = golfer
        this.course = course
    }
    AcesDto( Object item ){
        this.holeNumber = item[ 0 ]
        this.datePlayed = item[ 1 ]
        this.golfer = item[ 2 ]
        this.course = item[ 3 ]
    }
    Integer holeNumber
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yyyy")
    Date datePlayed
    String golfer
    String course
}
