package net.sf.l2j.gameserver.expander.buffer.model.holder;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;

public class BuffHolder extends IntIntHolder {
    private final int _minLvl;
    private final int _maxLvl;
    private final boolean _isMagic;
    private final int _priceId;
    private final int _priceCount;
    private final boolean _isOnlyNight;

    public BuffHolder(StatSet set) {
        super(set.getInteger("skillId"), set.getInteger("skillLevel"));

        _minLvl = set.getInteger("minLvl");
        _maxLvl = set.getInteger("maxLvl");
        _isMagic = set.getBool("isMagic", false);
        _priceId = set.getInteger("priceId", 57);
        _priceCount = set.getInteger("priceCount", 1);
        _isOnlyNight = set.getBool("isOnlyNight", false);
    }

    public int getMinLvl() {
        return _minLvl;
    }

    public int getMaxLvl() {
        return _maxLvl;
    }

    public boolean isMagic() {
        return _isMagic;
    }

    public Integer getPriceId() {
        return _priceId;
    }

    public int getPriceCount() {
        return _priceCount;
    }

    public boolean isOnlyNight() {
        return _isOnlyNight;
    }
}