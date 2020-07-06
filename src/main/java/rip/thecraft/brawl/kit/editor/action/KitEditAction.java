package rip.thecraft.brawl.kit.editor.action;

import lombok.Getter;

@Getter
public enum  KitEditAction {

    WEIGHT("Edit the weight"),
    PRICE("Edit the price"),
    DESCRIPTION("Edit the description"),
    ABILITY("Select the abilities"),
    ICON("Set the icon"),
    UPDATE("Update kit");

    private String display;

    KitEditAction(String display) {
        this.display = display;
    }
}
