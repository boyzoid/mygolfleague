package com.mygolfleague.dto

import com.mygolfleague.model.User


class UserDtoBasic {

    UserDtoBasic(User user, List lgs ){
        id = user.id
        firstName = user.firstName
        lastName = user.lastName
        emailAddress = user.emailAddress
        leagues = lgs
    }

    String id
    String firstName
    String lastName
    String emailAddress
    List<Map> leagues
}
