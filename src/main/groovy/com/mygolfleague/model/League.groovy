package com.mygolfleague.model

import com.mygolfleague.dto.SimpleSeasonDto;

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

    @ManyToMany( fetch = FetchType.EAGER )
    @JoinTable(name = "userleague")
    List<User> users = new ArrayList<>()
}