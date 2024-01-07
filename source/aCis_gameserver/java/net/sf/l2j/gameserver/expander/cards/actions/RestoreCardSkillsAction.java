package net.sf.l2j.gameserver.expander.cards.actions;

import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.skills.L2Skill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RestoreCardSkillsAction extends Action {
    private static final String _query = "SELECT card_type,skill_id,skill_lvl FROM character_card_skills WHERE owner_id=?";

    public void execute(Player player) {
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement(_query)) {
            ps.setInt(1, player.getObjectId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    L2Skill skill = SkillTable.getInstance().getInfo(rs.getInt(2), rs.getInt(3));
                    player.getCards().get(rs.getString(1)).addSkill(skill);

                    if (player.hasSkill(rs.getInt(2))) {
                        continue;
                    }

                    player.addSkill(skill, true);
                }
            }

        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
