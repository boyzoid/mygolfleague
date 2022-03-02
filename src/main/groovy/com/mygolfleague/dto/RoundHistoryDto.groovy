package com.mygolfleague.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected

@Introspected
class RoundHistoryDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yy")
    Date datePlayed
    def score
    def netScore
    def handicap
    String course
    Boolean handicapCalculated
}
