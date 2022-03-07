package com.mygolfleague.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected

@Introspected
class AcesDto {

    def holeNumber
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yyyy")
    Date datePlayed
    String golfer
    String courseName
}
