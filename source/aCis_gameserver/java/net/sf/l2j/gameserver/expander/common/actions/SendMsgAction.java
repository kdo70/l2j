package net.sf.l2j.gameserver.expander.common.actions;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class SendMsgAction extends Action {
    public static void execute(Player player, String message) {
        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1).addString(message));
    }

    public static void execute(Player player, SystemMessageId messageId) {
        player.sendPacket(SystemMessage.getSystemMessage(messageId));
    }
}
