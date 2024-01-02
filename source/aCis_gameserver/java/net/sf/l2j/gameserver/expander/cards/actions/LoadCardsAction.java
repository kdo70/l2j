package net.sf.l2j.gameserver.expander.cards.actions;

import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoadCardsAction extends Action {
    private static final String _query = "SELECT type,level,exp,points FROM character_cards WHERE owner_id=?";

    public void execute(Player player) {
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(_query)) {
            ps.setInt(1, player.getObjectId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CharacterCardHolder card = new CharacterCardHolder(
                            rs.getString(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getInt(4)
                    );

                    player.addCard(rs.getString(1), card);
                }
            }

        } catch (final Exception e) {
            LOGGER.error("Couldn't restore player cards.", e);
        }
    }
}
