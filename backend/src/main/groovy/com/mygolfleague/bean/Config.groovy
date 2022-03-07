package com.mygolfleague.bean

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("secrets")
class Config {
    String hashSalt;
}
