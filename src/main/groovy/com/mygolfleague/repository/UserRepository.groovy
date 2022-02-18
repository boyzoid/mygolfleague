package com.mygolfleague.repository

import com.mygolfleague.dto.LoginDto
import com.mygolfleague.dto.UserDto
import com.mygolfleague.model.User
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

import javax.persistence.EntityManager


@CompileStatic
@Repository
abstract class UserRepository implements CrudRepository< User, String> {
    private final EntityManager entityManager

    UserRepository(EntityManager entityManager ){
        this.entityManager = entityManager
    }
    abstract LoginDto getByEmailAddressAndPassword(String emailAddress, String password )
    abstract UserDto getById(String id)
    abstract List<User> findByLastName(String lastName)

}
