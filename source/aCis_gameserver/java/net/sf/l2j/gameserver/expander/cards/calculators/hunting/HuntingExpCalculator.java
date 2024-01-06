package net.sf.l2j.gameserver.expander.cards.calculators.hunting;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.expander.common.calculators.Calculator;
import net.sf.l2j.gameserver.model.actor.instance.Monster;

public class HuntingExpCalculator extends Calculator {
    private static final int MUL_CHANCE = 1;

    public int execute(Monster monster, int lvlDiff) {
        int points = monster.getStatus().getLevel();
        points = Rnd.get(points, points * 2);
        points *= Rnd.chance(MUL_CHANCE) ? 2 : 1;

        switch (lvlDiff) {
            case (5) -> points /= 5;
            case (4) -> points /= 4;
            case (3) -> points /= 3;
            case (2) -> points /= 2;
            default -> {
            }
        }

        return points;
    }
}
