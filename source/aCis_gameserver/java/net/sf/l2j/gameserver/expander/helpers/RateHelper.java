package net.sf.l2j.gameserver.expander.helpers;

import net.sf.l2j.Config;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.enums.DropType;
import net.sf.l2j.gameserver.enums.items.ArmorType;
import net.sf.l2j.gameserver.enums.items.EtcItemType;
import net.sf.l2j.gameserver.enums.items.WeaponType;
import net.sf.l2j.gameserver.enums.skills.Stats;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.DropData;
import net.sf.l2j.gameserver.model.item.kind.Item;

public class RateHelper {
    private static final CLogger LOGGER = new CLogger(RateHelper.class.getName());

    public static double getDropCategoryRate(DropType dropType, boolean isRaid, Player player) {
        switch (dropType) {
            case SPOIL -> {
                return Config.RATE_DROP_SPOIL;
            }
            case CURRENCY -> {
                double chance = player.getStatus().calcStat(Stats.MONSTER_MUL_ADENA_CHANCE, 0, null, null);
                if (chance > 1) {
                    chance -= 1;
                }
                return isRaid ? 0 : chance;
            }
            case GOLD -> {
                double chance = player.getStatus().calcStat(Stats.MONSTER_MUL_GOLD_CHANCE, 0, null, null);
                if (chance > 1) {
                    chance -= 1;
                }
                return isRaid ? 0 : chance;
            }
            case DROP -> {
                return isRaid ? 0 : 1;
            }
            case HERB -> {
                return Config.RATE_DROP_HERBS;
            }
            case ITEM -> {
                double chance = player.getStatus().calcStat(Stats.MONSTER_MUL_ITEMS_CHANCE, 0, null, null);
                if (chance > 1) {
                    chance -= 1;
                }
                return isRaid ? 0 : chance;
            }
            case EQUIP -> {
                double chance = player.getStatus().calcStat(Stats.MONSTER_MUL_EQUIP_CHANCE, 0, null, null);
                if (chance > 1) {
                    chance -= 1;
                }
                return isRaid ? 0 : chance;
            }
            default -> {
                return 0;
            }
        }
    }

    public static double getDropRate(DropData dropData, boolean isRaid, Player player) {
        Item item = ItemData.getInstance().getTemplate(dropData.getItemId());

        if (item.getItemId() == 57) {
            double chance = player.getStatus().calcStat(Stats.MONSTER_MUL_ADENA_CHANCE, 0, null, null);
            if (chance >= 1) {
                chance -= 1;
            }
            LOGGER.error("adena chance: " + chance);
            return isRaid ? 1 : chance;
        }

        if (item.getItemId() == 4356) {
            double chance = player.getStatus().calcStat(Stats.MONSTER_MUL_GOLD_CHANCE, 0, null, null);
            if (chance >= 1) {
                chance -= 1;
            }
            LOGGER.error("gold chance: " + chance);
            return isRaid ? 1 : chance;
        }

        if (item.getItemType() instanceof EtcItemType) {
            double chance = player.getStatus().calcStat(Stats.MONSTER_MUL_ITEMS_CHANCE, 0, null, null);
            if (chance >= 1) {
                chance -= 1;
            }
            LOGGER.error("item chance: " + chance);
            return isRaid ? 1 : chance;
        }

        if (item.getItemType() instanceof ArmorType || item.getItemType() instanceof WeaponType) {
            double chance = player.getStatus().calcStat(Stats.MONSTER_MUL_EQUIP_CHANCE, 0, null, null);
            if (chance >= 1) {
                chance -= 1;
            }
            LOGGER.error("item equip: " + chance);
            return isRaid ? 1 : chance;
        }

        return 0;
    }

    public static double getDropCountRate(DropData dropData, boolean isRaid, Player player) {
        Item item = ItemData.getInstance().getTemplate(dropData.getItemId());

        if (item.getItemId() == 57) {
            double rate = player.getStatus().calcStat(Stats.MONSTER_MUL_ADENA_COUNT, 0, null, null);
            LOGGER.error("adena count: " + rate);
            return isRaid ? 1 : rate;
        }

        if (item.getItemId() == 4356) {
            double rate = player.getStatus().calcStat(Stats.MONSTER_MUL_GOLD_COUNT, 0, null, null);
            LOGGER.error("gold count: " + rate);
            return isRaid ? 1 : rate;
        }

        if (item.getItemType() instanceof EtcItemType) {
            double rate = player.getStatus().calcStat(Stats.MONSTER_MUL_ITEMS_COUNT, 0, null, null);
            LOGGER.error("item count: " + rate);
            return isRaid ? 1 : rate;
        }

        if (item.getItemType() instanceof ArmorType || item.getItemType() instanceof WeaponType) {
            double rate = player.getStatus().calcStat(Stats.MONSTER_MUL_EQUIP_COUNT, 0, null, null);
            LOGGER.error("equip count: " + rate);
            return isRaid ? 1 : rate;
        }
        return 1;
    }

    public static long calculateExp(long exp, Player player, Creature creature) {
        double rate = Config.RATE_XP;
        double personalRateExp = player.getStatus().calcStat(Stats.MONSTER_MUL_EXP, 0, null, null);
        rate += personalRateExp >= 1 ? personalRateExp - 1 : personalRateExp;

        LOGGER.error("rateXp: " + rate);
        return (long) (creature.isRaidBoss() ? exp : exp * rate);
    }

    public static int calculateSp(int sp, Player player, Creature creature) {
        double rate = Config.RATE_SP;
        double personalRateSp = player.getStatus().calcStat(Stats.MONSTER_MUL_SP, 0, null, null);
        rate += personalRateSp >= 1 ? personalRateSp - 1 : personalRateSp;

        LOGGER.error("rateSp: " + rate);
        return (int) (creature.isRaidBoss() ? sp : sp * rate);
    }
}