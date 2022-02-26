package com.mygolfleague.repository

import com.mygolfleague.dto.AcesDto
import com.mygolfleague.dto.LeagueDtoBasic

import javax.inject.Inject
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Singleton
public class StatsService  {

    private final EntityManager entityManager

    StatsService( EntityManager entityManager ){
        this.entityManager = entityManager
    }
    //def list = entityManager.createNativeQuery( "select * from league where id = :id" ).setParameter( 'id', 'sed').getResultList()
    @Transactional
    Map getUserStats( String userid, String leagueId, String seasonId ){
        def ret = [
                league: [
                    aces: getLeagueAces( leagueId)
                ]
        ]
        return ret
    }

    private List getLeagueAces( String id ){
        List result =  entityManager.createNativeQuery( "SELECT h.number, m.datePlayed, concat( u.firstname, ' ', u.lastName)  as golfer, c.name coursename " +
                "FROM matchresulthole mrh " +
                "JOIN hole h on mrh.holeId = h.id " +
                "JOIN holegroup hg on h.holeGroupId = hg.id " +
                "JOIN course c on hg.courseId = c.id " +
                "JOIN leaguecourse lc on c.id = lc.courseId  " +
                "JOIN matchresult mr on mrh.matchResultId = mr.id " +
                "JOIN `match` m on mr.matchId = m.id " +
                "JOIN `user` u on mr.golferId = u.id " +
                "JOIN `week` w on m.weekId = w.id " +
                "JOIN `division` d on w.divisionId = d.id " +
                "WHERE mrh.score = 1 " +
                "and lc.leagueId = :id " +
                "ORDER BY m.datePlayed desc" ).setParameter( 'id', id).getResultList()
        List ret = []
        for( def item in result ){
            ret.add( new AcesDto( item[ 0 ], item[ 1 ], item[ 2 ], item[ 3 ] ) )
        }
        return ret
    }
}


