package net.sf.l2j.gameserver.expander.buffer.actions;

import net.sf.l2j.gameserver.expander.buffer.data.xml.BuffsByClassData;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Player;

public class GetErrorMessageAction extends Action {
    protected final int _playerMinLvl = BuffsByClassData.getInstance().getMinLvl();
    protected final int _playerMaxLvl = BuffsByClassData.getInstance().getMaxLvl();

    public String execute(Player player) {
        final int playerLvl = player.getStatus().getLevel();

        String message = null;
        if (playerLvl < _playerMinLvl) {
            message = "Ваш уровень должен быть больше или равен " + _playerMinLvl;
        } else if (playerLvl > _playerMaxLvl) {
            message = "Ваш уровень должен быть меньше или равен " + _playerMaxLvl;
        } else if (player.isSubClassActive()) {
            message = "Поддержка доступна только для основного класса персонажа";
        } else if (player.getKarma() > 0) {
            message = "Поддержка недоступна для персонажа с кармой";
        }

        return message;
    }
}
