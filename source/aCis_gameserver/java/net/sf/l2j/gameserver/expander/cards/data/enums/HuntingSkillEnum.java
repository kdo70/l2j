package net.sf.l2j.gameserver.expander.cards.data.enums;

import java.util.ArrayList;
import java.util.List;

public enum HuntingSkillEnum {
    AUTOLOOT_CURRENCY(8000),
    AUTOLOOT_ITEMS(8001),
    AUTOLOOT_EQUIP(8002),
    PVE_DAMAGE(8003),
    PVE_DEFENCE(8004),
    ADENA_CHANCE(8005),
    GOLD_CHANCE(8006),
    ITEMS_CHANCE(8007),
    EQUIP_CHANCE(8008),
    ADENA_COUNT(8009),
    GOLD_COUNT(8010),
    ITEMS_COUNT(8011),
    EQUIP_COUNT(8012),
    EXP_COUNT(8013),
    SP_COUNT(8014);
    private final int _id;

    HuntingSkillEnum(int id) {
        this._id = id;
    }

    public int getId() {
        return _id;
    }

    public static List<Integer> getList() {
        return new ArrayList<>() {
            {
                add(AUTOLOOT_CURRENCY.getId());
                add(AUTOLOOT_ITEMS.getId());
                add(AUTOLOOT_EQUIP.getId());
                add(PVE_DAMAGE.getId());
                add(PVE_DEFENCE.getId());
                add(ADENA_CHANCE.getId());
                add(GOLD_CHANCE.getId());
                add(ITEMS_CHANCE.getId());
                add(EQUIP_CHANCE.getId());
                add(ADENA_COUNT.getId());
                add(GOLD_COUNT.getId());
                add(ITEMS_COUNT.getId());
                add(EQUIP_COUNT.getId());
                add(EXP_COUNT.getId());
                add(SP_COUNT.getId());
            }
        };
    }
}