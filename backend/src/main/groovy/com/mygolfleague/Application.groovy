package com.mygolfleague

import io.micronaut.core.annotation.Introspected
import io.micronaut.runtime.Micronaut
import groovy.transform.CompileStatic

import javax.persistence.Entity

@CompileStatic
@Introspected(packages="com.mygoflleague.temp", includedAnnotations= Entity.class)
class Application {
    static void main(String[] args) {
        Micronaut.run(Application, args)
    }
}
