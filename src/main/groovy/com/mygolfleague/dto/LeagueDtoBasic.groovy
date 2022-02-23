package com.mygolfleague.dto

import io.micronaut.core.annotation.Introspected

@Introspected
class LeagueDtoBasic {
    String id
    String name
    SeasonDtoBasic currentSeason
}
