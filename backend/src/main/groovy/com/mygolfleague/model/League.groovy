package com.mygolfleague.model


import javax.persistence.*;


@Entity
@Table(name = "league")
public class League {
    @Id
    @Column(name = "id", nullable = false, length = 36)
     String id;

    @Column(name = "name", nullable = false, length = 50)
     String name;

    @Column(name = "enabled")
     Boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_season_id")
    Season currentSeason

    @ManyToMany( fetch = FetchType.EAGER )
    @JoinTable(name = "userleague")
    List<User> users = new ArrayList<>()
}