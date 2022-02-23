package com.mygolfleague.repository

import com.mygolfleague.dto.LeagueDtoBasic
import com.mygolfleague.dto.LoginDto
import com.mygolfleague.dto.UserDtoBasic
import com.mygolfleague.model.User
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

import javax.persistence.EntityManager


@CompileStatic
@Repository
abstract class UserRepository implements CrudRepository< User, String> {
    private final EntityManager entityManager
    private final LeagueRepository leagueRepository
    private final SeasonRepository seasonRepository

    UserRepository(EntityManager entityManager, LeagueRepository leagueRepository, SeasonRepository seasonRepository  ){
        this.entityManager = entityManager
        this.leagueRepository = leagueRepository
        this.seasonRepository = seasonRepository
    }
    abstract LoginDto getByEmailAddressAndPassword(String emailAddress, String password )
    abstract User getById(String id)
    abstract List<User> findByLastName(String lastName)
    UserDtoBasic getUserInit( String id ){
        User user = getById( id )
        List<LeagueDtoBasic> leagues = leagueRepository.findAllByUserId( id )
        def myLeagues = []
        for( def league in leagues ){
            Map thisLeague = [ id: league.id, name: league.name, seasons: seasonRepository.findAllLeagueId( league.id ) ]
            myLeagues.add( thisLeague )
        }
        UserDtoBasic ret =new UserDtoBasic( user, myLeagues )
        return ret
    }
}
