package net.sf.l2j.gameserver.model.actor.container.player.custom.statistics;

import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.enums.items.ArmorType;
import net.sf.l2j.gameserver.enums.items.EtcItemType;
import net.sf.l2j.gameserver.enums.items.ItemType;
import net.sf.l2j.gameserver.enums.items.WeaponType;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.cards.PlayerCard;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayerStatistic {
    private static final CLogger LOGGER = new CLogger(PlayerCard.class.getName());
    private static final String INSERT = "INSERT INTO character_statistic (owner_id,type) VALUES (?,?)";
    private static final String RESTORE = "SELECT type,value FROM character_statistic WHERE owner_id=?";
    private static final String UPDATE = "UPDATE character_statistic SET value=? WHERE owner_id=? AND type=?";
    private static final String RESET = "UPDATE character_statistic SET value=? WHERE type=?";
    private static final List<String> _statisticTypes = new ArrayList<>() {
        {
            add("monster_kills");
            add("monster_kills_today");
            add("monster_deaths");
            add("monster_deaths_today");
            add("exp");
            add("exp_today");
            add("sp");
            add("sp_today");
            add("adena");
            add("adena_today");
            add("gold");
            add("gold_today");
            add("items");
            add("items_today");
            add("equip");
            add("equip_today");
        }
    };
    private static final List<String> _statisticTodayTypes = new ArrayList<>() {
        {
            add("monster_kills_today");
            add("monster_deaths_today");
            add("exp_today");
            add("sp_today");
            add("adena_today");
            add("gold_today");
            add("items_today");
            add("equip_today");
        }
    };
    private String _type;
    private long _value;

    public PlayerStatistic(String type, long value) {
        _type = type;
        _value = value;
    }

    public String getType() {
        return _type;
    }

    public void setType(String _type) {
        this._type = _type;
    }

    public long getValue() {
        return _value;
    }

    public void setValue(long _value) {
        this._value = _value;
    }

    public static void createStatistic(Player player) {
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(INSERT)) {
            for (final String type : _statisticTypes) {
                ps.setLong(1, player.getObjectId());
                ps.setString(2, type);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (final Exception e) {
            LOGGER.error("Couldn't create player statistic.", e);
        }
    }

    public static void storeStatistic(Player player) {
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE)) {
            for (final PlayerStatistic statistic : player.getStatistics().values()) {
                ps.setLong(1, statistic.getValue());
                ps.setInt(2, player.getObjectId());
                ps.setString(3, statistic.getType());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (final Exception e) {
            LOGGER.error("Couldn't store player statistic.", e);
        }
    }

    public static void restoreStatistic(Player player) {
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(RESTORE)) {
            ps.setInt(1, player.getObjectId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PlayerStatistic statistic = new PlayerStatistic(rs.getString(1), rs.getLong(2));
                    player.addStatistic(rs.getString(1), statistic);
                }
            }

            if (player.getStatistics().isEmpty()) {
                createAndRestoreStatistic(player);
            }

        } catch (final Exception e) {
            LOGGER.error("Couldn't restore player statistic.", e);
        }
    }

    public static void resetStatistic() {

        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(RESET)) {
            for (final String type : _statisticTodayTypes) {
                ps.setLong(1, 0);
                ps.setString(2, type);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (final Exception e) {
            LOGGER.error("Couldn't reset player statistic.", e);
        }

        for (Player player : World.getInstance().getPlayers()) {

            player.sendMessage("Дневная статистика персонажа была обнулена");

            for (final String type : _statisticTodayTypes) {
                player.getStatistics().get(type).setValue(0);
            }
        }
    }

    public static void createAndRestoreStatistic(Player player) {
        createStatistic(player);
        restoreStatistic(player);
    }

    public static void addMonsterKills(Player player, Monster monster) {
        if (monster.isRaidBoss()) {
            return;
        }

        final int levelDiff = Math.abs(player.getStatus().getLevel() - monster.getStatus().getLevel());
        if (levelDiff >= 10) {
            return;
        }

        PlayerStatistic monsterDeaths = player.getStatistics().get("monster_kills");
        PlayerStatistic monsterDeathsToday = player.getStatistics().get("monster_kills_today");

        monsterDeaths.setValue(monsterDeaths.getValue() + 1);
        monsterDeathsToday.setValue(monsterDeathsToday.getValue() + 1);
    }

    public static String getMonsterKills(Player player) {
        return String.format(Locale.US, "%,d", player.getStatistics().get("monster_kills").getValue());
    }

    public static String getMonsterKillsToday(Player player) {
        return String.format(Locale.US, "%,d", player.getStatistics().get("monster_kills_today").getValue());
    }

    public static void addMonsterDeaths(Player player, Creature killer) {
        if (!(killer instanceof Monster) || killer.isRaidBoss()) {
            return;
        }

        PlayerStatistic monsterKills = player.getStatistics().get("monster_deaths");
        PlayerStatistic monsterKillsToday = player.getStatistics().get("monster_deaths_today");

        monsterKills.setValue(monsterKills.getValue() + 1);
        monsterKillsToday.setValue(monsterKillsToday.getValue() + 1);
    }

    public static String getMonsterDeaths(Player player) {
        return String.format(Locale.US, "%,d", player.getStatistics().get("monster_deaths").getValue());
    }

    public static String getMonsterDeathsToday(Player player) {
        return String.format(Locale.US, "%,d", player.getStatistics().get("monster_deaths_today").getValue());
    }

    public static void addExpAndSp(Player player, long addToExp, int addToSp, Creature creature) {
        if (!(creature instanceof Monster) || creature.isRaidBoss()) {
            return;
        }

        PlayerStatistic exp = player.getStatistics().get("exp");
        PlayerStatistic expToday = player.getStatistics().get("exp_today");

        exp.setValue(exp.getValue() + addToExp);
        expToday.setValue(expToday.getValue() + addToExp);

        PlayerStatistic sp = player.getStatistics().get("sp");
        PlayerStatistic spToday = player.getStatistics().get("sp_today");

        sp.setValue(sp.getValue() + addToSp);
        spToday.setValue(spToday.getValue() + addToSp);
    }

    public static String getExp(Player player) {
        return Str.number(player.getStatistics().get("exp").getValue());
    }

    public static String getExpToday(Player player) {
        return Str.number(player.getStatistics().get("exp_today").getValue());
    }

    public static String getSp(Player player) {
        return Str.number(player.getStatistics().get("sp").getValue());
    }

    public static String getSpToday(Player player) {
        return Str.number(player.getStatistics().get("sp_today").getValue());
    }

    public static void calculateStatisticDrop(Player player, IntIntHolder intIntHolder, Attackable attackable) {
        if (!(attackable instanceof Monster) || attackable.isRaidBoss()) {
            return;
        }

        if (intIntHolder.getId() == 57) {
            addAdena(player, intIntHolder.getValue(), attackable);
            return;
        }

        if (intIntHolder.getId() == 4356) {
            addGold(player, intIntHolder.getValue());
            return;
        }

        ItemType type = ItemData.getInstance().getTemplate(intIntHolder.getId()).getItemType();

        if (intIntHolder.getId() != 57 & type instanceof EtcItemType) {
            addItems(player, intIntHolder.getValue());
            return;
        }

        if (type instanceof ArmorType || type instanceof WeaponType) {
            addEquip(player, intIntHolder.getValue());
        }
    }

    public static void addAdena(Player player, long count, Creature attachable) {
        if (!(attachable instanceof Monster) || attachable.isRaidBoss()) {
            return;
        }

        PlayerStatistic adena = player.getStatistics().get("adena");
        PlayerStatistic adenaToday = player.getStatistics().get("adena_today");

        adena.setValue(adena.getValue() + count);
        adenaToday.setValue(adenaToday.getValue() + count);
    }

    public static String getAdena(Player player) {
        return Str.number(player.getStatistics().get("adena").getValue());
    }

    public static String getAdenaToday(Player player) {
        return Str.number(player.getStatistics().get("adena_today").getValue());
    }

    public static void addGold(Player player, long count) {
        PlayerStatistic gold = player.getStatistics().get("gold");
        PlayerStatistic goldToday = player.getStatistics().get("gold_today");

        gold.setValue(gold.getValue() + count);
        goldToday.setValue(goldToday.getValue() + count);
    }

    public static String getGold(Player player) {
        return Str.number(player.getStatistics().get("gold").getValue());
    }

    public static String getGoldToday(Player player) {
        return Str.number(player.getStatistics().get("gold_today").getValue());
    }

    public static void addItems(Player player, long count) {
        PlayerStatistic items = player.getStatistics().get("items");
        PlayerStatistic itemsToday = player.getStatistics().get("items_today");

        items.setValue(items.getValue() + count);
        itemsToday.setValue(itemsToday.getValue() + count);
    }

    public static String getItems(Player player) {
        return Str.number(player.getStatistics().get("items").getValue());
    }

    public static String getItemsToday(Player player) {
        return Str.number(player.getStatistics().get("items_today").getValue());
    }

    public static void addEquip(Player player, long count) {
        PlayerStatistic equip = player.getStatistics().get("equip");
        PlayerStatistic equipToday = player.getStatistics().get("equip_today");

        equip.setValue(equip.getValue() + count);
        equipToday.setValue(equipToday.getValue() + count);
    }

    public static String getEquip(Player player) {
        return Str.number(player.getStatistics().get("equip").getValue());
    }

    public static String getEquipToday(Player player) {
        return Str.number(player.getStatistics().get("equip_today").getValue());
    }
}