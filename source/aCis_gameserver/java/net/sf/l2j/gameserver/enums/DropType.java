package net.sf.l2j.gameserver.enums;

import net.sf.l2j.Config;

public enum DropType {
    SPOIL,
    GOLD,
    CURRENCY,
    ITEM,
    EQUIP,
    DROP,
    HERB;

    public double getDropRate(boolean isRaid) {
        switch (this) {
            case SPOIL:
                return Config.RATE_DROP_SPOIL;

            case CURRENCY, GOLD:
                return Config.RATE_DROP_CURRENCY;

            case DROP:
                return isRaid ? Config.RATE_DROP_ITEMS_BY_RAID : Config.RATE_DROP_ITEMS;

            case HERB:
                return Config.RATE_DROP_HERBS;

            case ITEM:
                return 1.;

            case EQUIP:
                return 1.;

            default:
                return 0;
        }
    }
}
