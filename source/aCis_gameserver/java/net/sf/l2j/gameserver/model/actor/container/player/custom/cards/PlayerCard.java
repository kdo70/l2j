package net.sf.l2j.gameserver.model.actor.container.player.custom.cards;

import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.model.actor.instance.Monster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PlayerCard {
    private static final CLogger LOGGER = new CLogger(PlayerCard.class.getName());
    private String _type;
    private int _level;
    private int _exp;
    private int _points;
    private static final List<String> _cardTypes = new ArrayList<>() {
        {
            add("monster");
        }
    };

    private static final String INSERT = "INSERT INTO character_cards (owner_id,type) VALUES (?,?)";
    private static final String RESTORE = "SELECT type,level,exp,points FROM character_cards WHERE owner_id=?";
    private static final String UPDATE = "UPDATE character_cards SET level=?,exp=?,points=? WHERE owner_id=? AND type=?";

    public PlayerCard(String type, int level, int exp, int points) {
        _type = type;
        _level = level;
        _exp = exp;
        _points = points;
    }

    public String getType() {
        return _type;
    }

    public void setType(String _type) {
        this._type = _type;
    }

    public int getLevel() {
        return _level;
    }

    public String getLevelStr() {
        return String.valueOf(_level);
    }

    public void setLevel(int _level) {
        this._level = _level;
    }

    public int getExp() {
        return _exp;
    }

    public void setExp(int _exp) {
        this._exp = _exp;
    }

    public void setPoints(int _points) {
        this._points = _points;
    }

    public int getPoints() {
        return _points;
    }

    public String getPointsStr() {
        return Str.numFormat(_points);
    }

    public void addExp(Player player, Monster monster) {
        HuntingCard.addExp(player, this, monster);
    }

    public String getRewardList() {
        return HuntingCard.getRewardList(this);
    }

    public String getRewardSkillInfo(Player player) {
        return HuntingCard.getRewardSkillInfo(this, player);
    }

    public static void createCards(Player player) {
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(INSERT)) {
            for (final String type : _cardTypes) {
                ps.setInt(1, player.getObjectId());
                ps.setString(2, type);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (final Exception e) {
            LOGGER.error("Couldn't create player cards.", e);
        }
    }

    public static void storeCards(Player player) {
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE)) {
            for (final PlayerCard card : player.getCards().values()) {
                ps.setLong(1, card.getLevel());
                ps.setInt(2, card.getExp());
                ps.setInt(3, card.getPoints());
                ps.setInt(4, player.getObjectId());
                ps.setString(5, card.getType());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (final Exception e) {
            LOGGER.error("Couldn't store player cards.", e);
        }
    }

    public static void restoreCards(Player player) {
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(RESTORE)) {
            ps.setInt(1, player.getObjectId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PlayerCard card = new PlayerCard(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4));
                    player.addCard(rs.getString(1), card);
                }
            }

            if (player.getCards().isEmpty()) {
                createAndRestoreCards(player);
            }

        } catch (final Exception e) {
            LOGGER.error("Couldn't restore player cards.", e);
        }
    }

    public static void createAndRestoreCards(Player player) {
        createCards(player);
        restoreCards(player);
    }

    public String getPercent() {
        return HuntingCard.getPercent(this);
    }

    public String getProgress() {
        return HuntingCard.getProgress(this);
    }

    public String getPageIndex(String content, Player player) {
        return HuntingCard.getPageIndex(content, this, player);
    }

    public String getPageShop(String content, int page) {
        return HuntingCard.getPageShop(content, this, page);
    }
}