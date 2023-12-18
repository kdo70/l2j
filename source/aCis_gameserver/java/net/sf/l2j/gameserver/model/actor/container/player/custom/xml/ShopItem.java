package net.sf.l2j.gameserver.model.actor.container.player.custom.xml;

import net.sf.l2j.commons.data.StatSet;

public class ShopItem {
    private final int _itemId;
    private final int _count;
    private final int _price;
    private int _limit;
    private final int _level;
    private int _soldCount = 0;

    public ShopItem(StatSet set) {
        _itemId = set.getInteger("itemId");
        _count = set.getInteger("count");
        _price = set.getInteger("price");
        _limit = set.getInteger("limit");
        _level = set.getInteger("level");
    }

    public int getItemId() {
        return _itemId;
    }

    public int getCount() {
        return _count;
    }

    public int getPrice() {
        return _price;
    }

    public int getLimit() {
        return _limit;
    }

    public void setLimit(int _limit) {
        this._limit = _limit;
    }

    public int getLevel() {
        return _level;
    }

    public int getSoldCount() {
        return _soldCount;
    }

    public void setSoldCount(int _soldCount) {
        this._soldCount = _soldCount;
    }
}