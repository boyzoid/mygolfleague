package com.mygolfleague.repository

import com.mygolfleague.dto.AcesDto
import com.mygolfleague.dto.BestRoundDto
import com.mygolfleague.dto.LeagueDtoBasic
import org.hibernate.Session
import org.hibernate.query.Query
import org.hibernate.transform.AliasToBeanResultTransformer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.inject.Singleton
import javax.persistence.EntityManager
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
        def ret = [
                league: [
                    aces: getLeagueAces( leagueId),
                    bestRoundsGross: getLeagueBestRoundsGross( leagueId, seasonId ),
                    bestRoundsNet: getLeagueBestRoundsNet( leagueId, seasonId ),
                    aggragate: getLeagueAggregate( leagueId, seasonId )
                ]
        ]
        return ret
    }

    private List<AcesDto> getLeagueAces( String id ){
        Session session = entityManager.unwrap(Session.class)
        return  session.createNativeQuery( "SELECT h.number holeNumber, m.datePlayed, concat( u.firstname, ' ', u.lastName)  as golfer, c.name courseName " +
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
                "ORDER BY m.datePlayed desc" ).setParameter( "id", id).setResultTransformer(new AliasToBeanResultTransformer(AcesDto.class)).list()

    }
    private List<BestRoundDto> getLeagueBestRoundsGross( String leagueId, String seasonId ){
        Session session = entityManager.unwrap(Session.class)
        Query query =  session.createNativeQuery( "SELECT distinct mr.score score, m.datePlayed, c.name courseName, c.abbrev courseAbbrev, hg.name holeGroup, " +
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
        query.setResultTransformer(new AliasToBeanResultTransformer(BestRoundDto.class))

        return query.list();
    }
    private List<BestRoundDto> getLeagueBestRoundsNet( String leagueId, String seasonId ){
        Session session = entityManager.unwrap(Session.class)
        Query query =  session.createNativeQuery( "SELECT distinct mr.score - mr.handicap score, m.datePlayed, c.name courseName, c.abbrev courseAbbrev, hg.name holeGroup, " +
                "concat( u.firstName, ' ', u.lastName)  as golfer " +
                "FROM matchresult mr " +
                "    JOIN `user` u on u.id = ( " +
                "   case when mr.subId is null then mr.golferId " +
                "   else mr.subid " +
                "   end " +
                " ) " +
                "JOIN `match` m on mr.matchId = m.id " +
                "JOIN holegroup hg on m.holeGroupId = hg.id " +
                "JOIN course c on hg.courseId = c.id " +
                "JOIN leaguecourse lc on c.id = lc.courseId " +
                "JOIN `week` w on m.weekId = w.id " +
                "JOIN `division` d on w.divisionId = d.id " +
                "WHERE lc.leagueId = :leagueId " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                "ORDER BY mr.score - mr.handicap, m.datePlayed desc " +
                "LIMIT 10 " )
        query.setParameter( "leagueId", leagueId)
        if( seasonId != null ){
            query.setParameter( "seasonId", seasonId)
        }
        query.setResultTransformer(new AliasToBeanResultTransformer(BestRoundDto.class))

        return query.list();
    }
    
    private Map<String, Integer> getLeagueAggregate( String leagueId, String seasonId){
        Query query =  entityManager.createNativeQuery( "select (select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holegroupid = hg.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where mrh.score = h.par - 2 " +
                " and lc.leagueId = :leagueId " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                " ) as eagle, " +
                "(select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holegroupid = hg.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where mrh.score = h.par - 1 " +
                "and lc.leagueId = :leagueId " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                ") as birdie, " +
                "(select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holegroupid = hg.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where mrh.score = h.par " +
                "and lc.leagueId = :leagueId " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                ") as par, " +
                "(select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holegroupid = hg.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where mrh.score = h.par + 1 " +
                "and lc.leagueId = :leagueId " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                ") as bogey, " +
                "(select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holegroupid = hg.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where mrh.score = h.par + 2 " +
                "and lc.leagueId = :leagueId " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                ") as 'double', " +
                "(select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holegroupid = hg.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where mrh.score > h.par + 2 " +
                "and lc.leagueId = :leagueId " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                ") as other" )
        query.setParameter( "leagueId", leagueId)
        if( seasonId != null ){
            query.setParameter( "seasonId", seasonId)
        }
        List result = query.getSingleResult()
        Map<String, Integer> ret = [ eagle: result[ 0 ], birdie: result[ 1 ], par: result[ 2 ], bogey: result[ 3 ], doubleBogey: result[ 4 ], other: result[ 5 ] ]

        return ret
    }


}


