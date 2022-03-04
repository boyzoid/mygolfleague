package com.mygolfleague.services

import com.mygolfleague.dto.AcesDto
import com.mygolfleague.dto.BestRoundDto
import com.mygolfleague.dto.HoleAverageDto
import com.mygolfleague.dto.RoundHistoryDto
import org.hibernate.Session
import org.hibernate.query.Query
import org.hibernate.transform.AliasToBeanResultTransformer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
            bestRoundsGross: getGolferBestRoundsGross( userId, leagueId, seasonId),
            bestRoundsNet: getGolferBestRoundsNet( userId, leagueId, seasonId ),
            aggregate: getGolferAggregate( userId, leagueId, seasonId ),
            roundHistory: getGolferRoundHistory( userId, leagueId, seasonId ),
            holeAverage: [
                    golfer: getGolferHoleAverage( userId, leagueId, seasonId ),
                    league: getLeagueHoleAverage( leagueId, seasonId )
            ]
        ]
        return ret
    }
    @Transactional
    Map getLeagueStats( String leagueId, String seasonId ){
        def ret = [
            aces: getLeagueAces( leagueId),
            bestRoundsGross: getLeagueBestRoundsGross( leagueId, seasonId ),
            bestRoundsNet: getLeagueBestRoundsNet( leagueId, seasonId ),
            aggregate: getLeagueAggregate( leagueId, seasonId )
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
                "WHERE lc.leagueId = :leagueId " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                "ORDER BY mr.score, m.datePlayed desc " +
                "LIMIT 10 " )
        query.setParameter( "leagueId", leagueId)
        if( seasonId != null ){
            query.setParameter( "seasonId", seasonId)
        }
        query.setResultTransformer(new AliasToBeanResultTransformer(BestRoundDto.class))

        return query.list();
    }
    private List<BestRoundDto> getGolferBestRoundsGross( String userId, String leagueId, String seasonId ){
        Session session = entityManager.unwrap(Session.class)
        Query query =  session.createNativeQuery( "SELECT mr.score, m.datePlayed datePlayed, c.name courseName, c.abbrev courseAbbrev, hg.name holeGroup" +
                "     FROM matchresult mr " +
                "     JOIN `match` m on mr.matchId = m.id " +
                "     JOIN `week` w on m.weekId = w.id  " +
                "     JOIN division d on w.divisionId = d.id " +
                "     JOIN season s on d.seasonId = s.id " +
                "     JOIN league l on s.leagueId = l.id " +
                "     JOIN holegroup hg on m.holeGroupId = hg.id " +
                "     JOIN course c on hg.courseId = c.id " +
                "     WHERE " +
                "     ( " +
                "     ( mr.golferid = :id and mr.subId is NULL) " +
                "     OR mr.subId = :id ) " +
                "     AND l.id = :leagueId " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                "ORDER BY mr.score, m.datePlayed desc " +
                "LIMIT 10 " )
        query.setParameter( "leagueId", leagueId)
        query.setParameter( "id", userId )
        if( seasonId != null ){
            query.setParameter( "seasonId", seasonId)
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

    private List<BestRoundDto> getGolferBestRoundsNet( String userId, String leagueId, String seasonId ){
        Session session = entityManager.unwrap(Session.class)
        Query query =  session.createNativeQuery( "SELECT mr.score - mr.handicap score, m.datePlayed datePlayed, c.name courseName, c.abbrev courseAbbrev, hg.name holeGroup " +
                "FROM matchresult mr  " +
                "JOIN `user` u on mr.golferId = u.id  " +
                "JOIN `match` m on mr.matchId = m.id  " +
                "JOIN `week` w on m.weekId = w.id   " +
                "JOIN division d on w.divisionId = d.id  " +
                "JOIN season s on d.seasonId = s.id  " +
                "JOIN league l on s.leagueId = l.id  " +
                "JOIN holegroup hg on m.holeGroupId = hg.id  " +
                "JOIN course c on hg.courseId = c.id  " +
                "where ( ( mr.golferid = :id and mr.subId is NULL) OR mr.subId = :id ) " +
                "AND l.id = :leagueId " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                "ORDER BY mr.score  - mr.handicap, m.datePlayed desc " +
                "LIMIT 10 " )
        query.setParameter( "leagueId", leagueId)
        query.setParameter( "id", userId )
        if( seasonId != null ){
            query.setParameter( "seasonId", seasonId)
        }
        query.setResultTransformer(new AliasToBeanResultTransformer(BestRoundDto.class))

        return query.list();
    }
    
    private Map<String, Integer> getGolferAggregate( String userId,  String leagueId, String seasonId){
        Query query =  entityManager.createNativeQuery( "select (select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holeGroupId = hg.id " +
                "join course c on hg.courseId = c.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where ( ( mr.golferid = :id and mr.subId is NULL) OR mr.subId = :id ) " +
                "and lc.leagueId = :leagueId " +
                "and mrh.score = h.par - 2 " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                ") as eagle, " +
                "(select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holeGroupId = hg.id " +
                "join course c on hg.courseId = c.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where ( ( mr.golferid = :id and mr.subId is NULL) OR mr.subId = :id ) " +
                "and lc.leagueId = :leagueId " +
                "and mrh.score = h.par - 1 " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                ") as birdie, " +
                "(select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holeGroupId = hg.id " +
                "join course c on hg.courseId = c.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where ( ( mr.golferid = :id and mr.subId is NULL) OR mr.subId = :id ) " +
                "and lc.leagueId = :leagueId " +
                "and mrh.score = h.par " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                ") as par, " +
                "(select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holeGroupId = hg.id " +
                "join course c on hg.courseId = c.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where ( ( mr.golferid = :id and mr.subId is NULL) OR mr.subId = :id ) " +
                "and lc.leagueId = :leagueId " +
                "and mrh.score = h.par + 1 " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                ") as bogey, " +
                "(select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holeGroupId = hg.id " +
                "join course c on hg.courseId = c.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where ( ( mr.golferid = :id and mr.subId is NULL) OR mr.subId = :id ) " +
                "and lc.leagueId = :leagueId " +
                "and mrh.score = h.par + 2 " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                ") as 'double', " +
                "(select count( mrh.score) " +
                "from matchresulthole mrh " +
                "join matchresult mr on mrh.matchResultId = mr.id " +
                "join hole h on mrh.holeId = h.id " +
                "join holegroup hg on h.holeGroupId = hg.id " +
                "join course c on hg.courseId = c.id " +
                "join leaguecourse lc on hg.courseId = lc.courseId " +
                "join `match` m ON mr.matchId = m.id " +
                "join `week` w ON m.weekId = w.id " +
                "join `division` d ON w.divisionId = d.id " +
                "where ( ( mr.golferid = :id and mr.subId is NULL) OR mr.subId = :id ) " +
                "and lc.leagueId = :leagueId " +
                "and mrh.score > h.par + 2 " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                ") as other" )
        query.setParameter( "leagueId", leagueId)
        query.setParameter( "id", userId )
        if( seasonId != null ){
            query.setParameter( "seasonId", seasonId)
        }
        List result = query.getSingleResult()
        Map<String, Integer> ret = [ eagle: result[ 0 ], birdie: result[ 1 ], par: result[ 2 ], bogey: result[ 3 ], doubleBogey: result[ 4 ], other: result[ 5 ] ]

        return ret
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

    private List getGolferRoundHistory( String userId, String leagueId, String seasonId ){
        Session session = entityManager.unwrap(Session.class)
        Query query =  session.createNativeQuery( "SELECT data.* " +
                "FROM  " +
                "( " +
                "SELECT m.datePlayed datePlayed, " +
                "mr.score,  " +
                "mr.score-mr.handicap as netScore,  " +
                "mr.handicap,  " +
                "CONCAT(c.name, ' - ', hg.name) course, " +
                "s.initialHandicapsCalculated handicapCalculated " +
                "FROM matchresult mr " +
                "JOIN `match` m ON mr.matchid = m.id " +
                "JOIN week w ON m.weekId = w.id " +
                "JOIN holegroup hg ON m.holeGroupId = hg.id " +
                "JOIN course c on hg.courseId = c.id " +
                "JOIN division d ON w.divisionId = d.id " +
                "JOIN season s ON d.seasonId = s.id " +
                "JOIN league l on s.leagueId = l.id " +
                "WHERE ( ( mr.golferid = :id and mr.subId is NULL) OR mr.subId = :id ) " +
                "AND l.id = :leagueId " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                "ORDER BY m.datePlayed DESC " +
                (seasonId != null ? "LIMIT 50 " : "") +
                ") data " +
                "ORDER BY data.datePlayed" )
        query.setParameter( "leagueId", leagueId)
        query.setParameter( "id", userId )
        if( seasonId != null ){
            query.setParameter( "seasonId", seasonId)
        }
        query.setResultTransformer(new AliasToBeanResultTransformer(RoundHistoryDto.class))
        return query.list();
    }

    private List getGolferHoleAverage( String userId, String leagueId, String seasonId ){
        Session session = entityManager.unwrap(Session.class)
        Query query =  session.createNativeQuery( "select h.number,  " +
                "h.par,  " +
                "avg(  " +
                "CASE  " +
                "WHEN mrh.score > 0 THEN mrh.score  " +
                "WHEN mrh.score = -1 THEN h.par + 3  " +
                " END  " +
                "   ) averageScore,  " +
                "avg(CASE  " +
                "WHEN mrh.score > 0 THEN mrh.score  " +
                "WHEN mrh.score = -1 THEN h.par + 3  " +
                " END) - h.par diff  " +
                "from matchresulthole mrh  " +
                "join matchresult mr on mrh.matchResultId = mr.id  " +
                "join hole h on mrh.holeId = h.id  " +
                "join holegroup hg on h.holegroupid = hg.id  " +
                "join leaguecourse lc on hg.courseId = lc.courseId  " +
                "join `match` m ON mr.matchId = m.id  " +
                "join `week` w ON m.weekId = w.id  " +
                "join `division` d ON w.divisionId = d.id  " +
                "where ( ( mr.golferid = :id and mr.subId is NULL) OR mr.subId = :id )  " +
                "and lc.leagueId = :leagueId  " +
                "and mrh.score >= -1  " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                "Group by h.number  " +
                "order by h.number" )
        query.setParameter( "leagueId", leagueId)
        query.setParameter( "id", userId )
        if( seasonId != null ){
            query.setParameter( "seasonId", seasonId)
        }
        query.setResultTransformer(new AliasToBeanResultTransformer(HoleAverageDto.class))
        return query.list();
    }

    private List getLeagueHoleAverage( String leagueId, String seasonId ){
        Session session = entityManager.unwrap(Session.class)
        Query query =  session.createNativeQuery( "select h.number,  " +
                "h.par,  " +
                "avg(  " +
                "CASE  " +
                "WHEN mrh.score > 0 THEN mrh.score  " +
                "WHEN mrh.score = -1 THEN h.par + 3  " +
                " END  " +
                "   ) averageScore,  " +
                "avg(CASE  " +
                "WHEN mrh.score > 0 THEN mrh.score  " +
                "WHEN mrh.score = -1 THEN h.par + 3  " +
                " END) - h.par diff  " +
                "from matchresulthole mrh  " +
                "join matchresult mr on mrh.matchResultId = mr.id  " +
                "join hole h on mrh.holeId = h.id  " +
                "join holegroup hg on h.holegroupid = hg.id  " +
                "join leaguecourse lc on hg.courseId = lc.courseId  " +
                "join `match` m ON mr.matchId = m.id  " +
                "join `week` w ON m.weekId = w.id  " +
                "join `division` d ON w.divisionId = d.id  " +
                "where lc.leagueId = :leagueId  " +
                "and mrh.score >= -1  " +
                (seasonId != null ? "and d.seasonId = :seasonId " : "") +
                "Group by h.number  " +
                "order by h.number" )
        query.setParameter( "leagueId", leagueId)
        if( seasonId != null ){
            query.setParameter( "seasonId", seasonId)
        }
        query.setResultTransformer(new AliasToBeanResultTransformer(HoleAverageDto.class))
        return query.list();
    }
}


