package net.sf.l2j.gameserver.model.actor.container.player.custom.helpers;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

import java.text.DecimalFormat;
import java.util.Locale;

public class Str {
    public static String number(long value) {
        String prefix = "";

        if (value > 1000000 & value < 1000000000) {
            value /= 1000000;
            prefix = "M";
        }

        if (value > 1000000000) {
            value /= 1000000000;
            prefix = "B";
        }

        return String.format(Locale.US, "%,d", value) + prefix;
    }

    public static String morph(int num, String... arr) {
        String result = "";
        int num100 = num % 100;
        if (num100 > 4 && num100 < 21) result = arr[2];
        else {
            int num10 = num100 % 10;
            if (num10 == 1) result = arr[0];
            else if (num10 > 1 && num10 < 5) result = arr[1];
            else result = arr[2];
        }
        return result;
    }

    public static String mul(double value) {
        return "x" + value;
    }

    public static String percent(double chance) {
        String percent;
        if (chance <= 0.001) {
            DecimalFormat string = new DecimalFormat("#.####");
            percent = string.format(chance);
        } else if (chance <= 0.01) {
            DecimalFormat string = new DecimalFormat("#.###");
            percent = string.format(chance);
        } else {
            DecimalFormat string = new DecimalFormat("##.##");
            percent = string.format(chance);
        }
        return percent;
    }

    public static String numFormat(int value) {
        return String.format(Locale.US, "%,d", value);
    }

    public static void sendMsg(Player player, String text) {
        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1).addString(text));
    }

    public static void sendMsg(Player player, SystemMessageId messageId) {
        player.sendPacket(SystemMessage.getSystemMessage(messageId));
    }
}