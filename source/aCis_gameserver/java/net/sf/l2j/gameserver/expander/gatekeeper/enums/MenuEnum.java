package net.sf.l2j.gameserver.expander.gatekeeper.enums;

public enum MenuEnum {

    TOWNS(1),
    VILLAGES(2),
    POPULAR(20),
    RECOMMENDED(21);

    private final int id;

    MenuEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}