package net.sf.l2j.gameserver.model.actor.container.player.custom.cards;

import net.sf.l2j.commons.data.StatSet;

public class HuntingCardLevel {
    private final int _level;
    private final int _requiredExpToLevelUp;
    private final int _skillId;
    private final int _skillLevel;
    private final int _itemId;
    private final int _itemCount;

    public HuntingCardLevel(StatSet set) {
        _level = set.getInteger("level");
        _requiredExpToLevelUp = set.getInteger("requiredExpToLevelUp");
        _skillId = set.getInteger("skillId");
        _skillLevel = set.getInteger("skillLevel");
        _itemId = set.getInteger("itemId");
        _itemCount = set.getInteger("itemCount");
    }

    public int getLevel() {
        return _level;
    }

    public int getRequiredExpToLevelUp() {
        return _requiredExpToLevelUp;
    }

    public int getSkillId() {
        return _skillId;
    }

    public int getSkillLevel() {
        return _skillLevel;
    }

    public int getItemId() {
        return _itemId;
    }

    public int getItemCount() {
        return _itemCount;
    }
}