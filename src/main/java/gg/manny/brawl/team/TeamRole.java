package gg.manny.brawl.team;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TeamRole {

    LEADER("**"), COLEADER("**"), CAPTAIN("*"), MEMBER("");

    private String astrix;

    public boolean hasRole(TeamRole requiredRole) {
        return this.ordinal() <= requiredRole.ordinal();
    }

}