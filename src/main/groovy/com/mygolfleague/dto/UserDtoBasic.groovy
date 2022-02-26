package com.mygolfleague.dto

import com.mygolfleague.model.User


class UserDtoBasic {

    UserDtoBasic(User user ){
        id = user.id
        firstName = user.firstName
        lastName = user.lastName
        emailAddress = user.emailAddress
    }

    String id
    String firstName
    String lastName
    String emailAddress
}
