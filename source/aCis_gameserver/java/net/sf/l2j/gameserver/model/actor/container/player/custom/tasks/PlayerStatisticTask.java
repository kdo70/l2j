package net.sf.l2j.gameserver.model.actor.container.player.custom.tasks;

import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.gameserver.model.actor.container.player.custom.cards.PlayerCard;
import net.sf.l2j.gameserver.model.actor.container.player.custom.statistics.PlayerStatistic;

import java.time.LocalTime;

public class PlayerStatisticTask implements Runnable {
    private static final CLogger LOGGER = new CLogger(PlayerCard.class.getName());

    @Override
    public void run() {
        if (LocalTime.now().getHour() == 0 && LocalTime.now().getMinute() == 1) {
            PlayerStatistic.resetStatistic();

            LOGGER.info("The zeroing of player statistics was started.");
        }
    }
}