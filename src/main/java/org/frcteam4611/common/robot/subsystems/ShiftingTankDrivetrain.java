package org.frcteam4611.common.robot.subsystems;

public abstract class ShiftingTankDrivetrain extends TankDrivetrain {

	public ShiftingTankDrivetrain(double trackWidth) {
		super(trackWidth);
	}

	public abstract boolean inHighGear();

	public abstract void setHighGear(boolean highGear);
}
