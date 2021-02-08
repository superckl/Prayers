package me.superckl.prayers.capability;

import me.superckl.prayers.Prayer;

public class AltarPrayerUser extends DefaultPrayerUser{

	@Override
	public boolean canActivatePrayer(final Prayer prayer) {
		return false;
	}

	@Override
	public void activatePrayer(final Prayer prayer) {
		throw new UnsupportedOperationException("Altars cannot use prayers!");
	}

	@Override
	public int getPrayerLevel() {
		throw new UnsupportedOperationException("Altars do not have a prayer level!");
	}

	@Override
	protected float computeMaxPoints() {
		// TODO Auto-generated method stub
		return super.computeMaxPoints();
	}

}
