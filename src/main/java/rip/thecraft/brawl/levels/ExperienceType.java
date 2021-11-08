package rip.thecraft.brawl.levels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExperienceType {

    PLAYTIME("Playtime", 15), // TODO Make sure you aren't AFK AND NOT IN SPAWN and have killed a player and voted for NameMC
    KILL_ASSISTS("Assist on %s", 5),
    KILL("Killed %s", 10),
    DUEL_WIN("Duel Win",10), // TODO Add boost check and only for Unrated

    EVENT("Playing %s", 10), // TODO As long as there is 5 or more players
    EVENT_WIN("Winning %s", 5); // TODO 5 * Players on there

    private String name;
    private int experience;


}
