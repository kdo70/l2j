package net.sf.l2j.gameserver.expander.cards.actions;

import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.gameserver.expander.cards.data.enums.CardsTypeEnum;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public class CreateCardsAction extends Action {
    private static final String _query = "INSERT INTO character_cards (owner_id,type) VALUES (?,?)";
    private static final List<String> _cardTypes = CardsTypeEnum.getList();

    public void execute(Player player) {
        final Map<String, CharacterCardHolder> characterCards = player.getCards();

        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(_query)) {

            for (final String type : _cardTypes) {
                if (characterCards.get(type) != null) {
                    continue;
                }

                ps.setInt(1, player.getObjectId());
                ps.setString(2, type);

                ps.addBatch();
            }

            ps.executeBatch();
        } catch (final Exception e) {
            LOGGER.error("Couldn't create player cards.", e);
        }
    }
}
