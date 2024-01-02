package net.sf.l2j.gameserver.expander.cards.actions;

import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class StoreCardsAction extends Action {
    private static final String _query = "UPDATE character_cards SET level=?,exp=?,points=? WHERE owner_id=? AND type=?";

    public void execute(Player player) {
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(_query)) {

            for (final CharacterCardHolder card : player.getCards().values()) {
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
}
