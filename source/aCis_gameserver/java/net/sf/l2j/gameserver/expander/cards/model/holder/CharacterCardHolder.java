package net.sf.l2j.gameserver.expander.cards.model.holder;

public class CharacterCardHolder {
    protected final String _type;
    protected int _level;
    protected int _exp;
    protected int _points;

    public CharacterCardHolder(String type, int level, int exp, int points) {
        _type = type;
        _level = level;
        _exp = exp;
        _points = points;
    }

    public String getType() {
        return _type;
    }

    public int getLevel() {
        return _level;
    }

    public void setLevel(int level) {
        _level = level;
    }

    public int getExp() {
        return _exp;
    }

    public void setExp(int exp) {
        _exp = exp;
    }

    public void setPoints(int points) {
        _points = points;
    }

    public int getPoints() {
        return _points;
    }
}