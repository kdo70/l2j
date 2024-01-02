package net.sf.l2j.gameserver.expander.statistic.tasks;

import net.sf.l2j.gameserver.expander.statistic.CharacterStatistic;

import java.time.LocalTime;

public class ResetDayStatisticTask implements Runnable {
    @Override
    public void run() {
        if (LocalTime.now().getHour() == 0 && LocalTime.now().getMinute() == 1) {
            CharacterStatistic.resetStatistic();
        }
    }
}