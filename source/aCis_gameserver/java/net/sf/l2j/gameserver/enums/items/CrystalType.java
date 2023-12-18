package net.sf.l2j.gameserver.enums.items;

public enum CrystalType
{
	NONE(0, 0, 0, 0, "NG"),
	D(1, 1458, 11, 90, "D"),
	C(2, 1459, 6, 45, "C"),
	B(3, 1460, 11, 67, "B"),
	A(4, 1461, 19, 144, "A"),
	S(5, 1462, 25, 250, "S");
	
	private final int _id;
	private final int _crystalId;
	private final int _crystalEnchantBonusArmor;
	private final int _crystalEnchantBonusWeapon;
	private String _name;

	private CrystalType(int id, int crystalId, int crystalEnchantBonusArmor, int crystalEnchantBonusWeapon, String name)
	{
		_id = id;
		_crystalId = crystalId;
		_crystalEnchantBonusArmor = crystalEnchantBonusArmor;
		_crystalEnchantBonusWeapon = crystalEnchantBonusWeapon;
		_name = name;
	}
	
	/**
	 * Gets the crystal type ID.
	 * @return the crystal type ID
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * Gets the item ID of the crystal.
	 * @return the item ID of the crystal
	 */
	public int getCrystalId()
	{
		return _crystalId;
	}

	public String getName() {
		return _name;
	}

	public int getCrystalEnchantBonusArmor()
	{
		return _crystalEnchantBonusArmor;
	}
	
	public int getCrystalEnchantBonusWeapon()
	{
		return _crystalEnchantBonusWeapon;
	}
	
	public boolean isGreater(CrystalType crystalType)
	{
		return getId() > crystalType.getId();
	}
	
	public boolean isLesser(CrystalType crystalType)
	{
		return getId() < crystalType.getId();
	}
}