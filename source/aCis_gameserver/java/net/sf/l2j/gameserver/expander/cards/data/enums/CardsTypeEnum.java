package net.sf.l2j.gameserver.expander.cards.data.enums;

import java.util.ArrayList;
import java.util.List;

public enum CardsTypeEnum {
    MONSTER("monster");
    private final String _name;

    CardsTypeEnum(String name) {
        this._name = name;
    }

    public String getName() {
        return _name;
    }

    public static List<String> getList() {
        return new ArrayList<>() {
            {
                add(MONSTER.getName());
            }
        };
    }
}