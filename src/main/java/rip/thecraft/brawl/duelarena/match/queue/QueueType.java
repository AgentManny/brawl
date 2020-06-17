package rip.thecraft.brawl.duelarena.match.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QueueType {

    DUEL("Duel"),
    QUICKMATCH("Quickmatch"),
    UNRANKED("Unranked"),
    RANKED("Ranked");

    private final String name;



}
