package com.mygolfleague.repository

import com.mygolfleague.dto.AcesDto
import com.mygolfleague.dto.BestRoundDto
import com.mygolfleague.dto.LeagueDtoBasic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.persistence.Query
import javax.transaction.Transactional

@Singleton
public class StatsService  {
    private static final Logger LOG = LoggerFactory.getLogger(StatsService.class);
    private final EntityManager entityManager

    StatsService( EntityManager entityManager ){
        this.entityManager = entityManager
    }
    //def list = entityManager.createNativeQuery( "select * from league where id = :id" ).setParameter( 'id', 'sed').getResultList()
    @Transactional
    Map getUserStats( String userId, String leagueId, String seasonId ){
        LOG.info( "UserId: " + userId )
        LOG.info( "LeagueID: " + leagueId )
        LOG.info( "SeasonId: " + seasonId )
        def ret = [
                league: [
                    aces: getLeagueAces( leagueId),
                    bestRounds: getLeagueBestRoundsGross( leagueId, seasonId )
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
                "ORDER BY m.datePlayed desc" ).setParameter( "id", id).getResultList()
        List ret = []
        for( def item in result ){
            ret.add( new AcesDto( item ) )
        }
        return ret
    }
    private List getLeagueBestRoundsGross( String leagueId, String seasonId ){
        LOG.info( "LeagueID: " + leagueId )
        LOG.info( "SeasonId: " + seasonId )
        Query query =  entityManager.createNativeQuery( "SELECT distinct mr.score score, m.datePlayed, c.name courseName, c.abbrev courseAbbrev, hg.name, " +
                "concat( u.firstName, ' ', u.lastName)  as golfer " +
                "FROM matchresult mr " +
                "JOIN `user` u on u.id = ( " +
                "case when mr.subId is null then mr.golferId " +
                "else mr.subid " +
                "end ) " +
                "JOIN `match` m on mr.matchId = m.id " +
                "JOIN holegroup hg on m.holeGroupId = hg.id " +
                "JOIN course c on hg.courseId = c.id " +
                "JOIN leaguecourse lc on c.id = lc.courseId " +
                "JOIN `week` w on m.weekId = w.id " +
                "JOIN `division` d on w.divisionId = d.id " +
                "WHERE lc.leagueId = ? " +
                (seasonId != null ? "and d.seasonId = ? " : "") +
                "ORDER BY mr.score, m.datePlayed desc " +
                "LIMIT 10 " )
        query.setParameter( 1, leagueId)
        if( seasonId != null ){
            query.setParameter( 2, seasonId)
        }
        LOG.info( query.getParameters().toString() )
        List result = query.getResultList()
        List ret = []
        for( def item in result ){
            ret.add( new BestRoundDto( item ) )
        }
        return ret
    }
}


