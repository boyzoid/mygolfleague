package com.mygolfleague.dto

import io.micronaut.core.annotation.Introspected

import javax.persistence.Lob

@Introspected
class RulesDto {
    String id
    String name
    Boolean publishRules
    @Lob
    String rules
}
