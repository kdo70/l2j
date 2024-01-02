package net.sf.l2j.gameserver.expander.statistic.data.enums;

import java.util.ArrayList;
import java.util.List;

public enum StatisticTypeEnum {
    MONSTER_KILLS("monster_kills"),
    MONSTER_KILLS_TODAY("monster_kills_today"),
    MONSTER_DEATHS("monster_deaths"),
    MONSTER_DEATHS_TODAY("monster_deaths_today"),
    EXP("exp"),
    EXP_TODAY("exp_today"),
    SP("sp"),
    SP_TODAY("sp_today"),
    ADENA("adena"),
    ADENA_TODAY("adena_today"),
    GOLD("gold"),
    GOLD_TODAY("gold_today"),
    ITEMS("items"),
    ITEMS_TODAY("items_today"),
    EQUIP("equip"),
    EQUIP_TODAY("equip_today");
    private final String _name;

    StatisticTypeEnum(String name) {
        this._name = name;
    }

    public String getName() {
        return _name;
    }

    public static List<String> getList() {
        return new ArrayList<>() {
            {
                add(MONSTER_KILLS.getName());
                add(MONSTER_KILLS_TODAY.getName());
                add(MONSTER_DEATHS.getName());
                add(MONSTER_DEATHS_TODAY.getName());
                add(EXP.getName());
                add(EXP_TODAY.getName());
                add(SP.getName());
                add(SP_TODAY.getName());
                add(ADENA.getName());
                add(ADENA_TODAY.getName());
                add(GOLD.getName());
                add(GOLD_TODAY.getName());
                add(ITEMS.getName());
                add(ITEMS_TODAY.getName());
                add(EQUIP.getName());
                add(EQUIP_TODAY.getName());
            }
        };
    }

    public static List<String> getListToday() {
        return new ArrayList<>() {
            {
                add(MONSTER_KILLS_TODAY.getName());
                add(MONSTER_DEATHS_TODAY.getName());
                add(EXP_TODAY.getName());
                add(SP_TODAY.getName());
                add(ADENA_TODAY.getName());
                add(GOLD_TODAY.getName());
                add(ITEMS_TODAY.getName());
                add(EQUIP_TODAY.getName());
            }
        };
    }
}