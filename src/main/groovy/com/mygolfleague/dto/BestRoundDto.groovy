package com.mygolfleague.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected

@Introspected
class BestRoundDto {

    def score
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yyyy")
    Date datePlayed
    String courseName
    String courseAbbrev
    String holeGroup
    String golfer
}
