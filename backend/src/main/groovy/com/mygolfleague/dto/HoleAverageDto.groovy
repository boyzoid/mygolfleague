package com.mygolfleague.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected

@Introspected
class HoleAverageDto {

    def number
    def par
    def averageScore
    def diff
}
