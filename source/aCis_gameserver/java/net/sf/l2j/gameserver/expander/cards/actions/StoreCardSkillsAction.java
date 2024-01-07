package net.sf.l2j.gameserver.expander.cards.actions;

import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.skills.L2Skill;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class StoreCardSkillsAction extends Action {
    private static final String _query = "INSERT INTO character_card_skills (owner_id, card_type, skill_id, skill_lvl) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE skill_lvl=VALUES(skill_lvl)";

    public void execute(Player player) {
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(_query)) {

            for (final CharacterCardHolder card : player.getCards().values()) {
                for (final L2Skill skill : card.getSkills()) {
                    ps.setInt(1, player.getObjectId());
                    ps.setString(2, card.getType());
                    ps.setInt(3, skill.getId());
                    ps.setInt(4, skill.getLevel());

                    ps.addBatch();
                }
            }

            ps.executeBatch();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
