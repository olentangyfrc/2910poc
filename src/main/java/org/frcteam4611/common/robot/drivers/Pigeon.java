package org.frcteam4611.common.robot.drivers;

import com.ctre.phoenix.sensors.PigeonIMU;
import org.frcteam4611.common.drivers.Gyroscope;
import org.frcteam4611.common.math.Rotation2;

public final class Pigeon extends Gyroscope {
    private PigeonIMU pigeon;

    public Pigeon(int canPort) {
        pigeon = new PigeonIMU(canPort);
    }

    @Override
    public void calibrate() {
        pigeon.setFusedHeading(0, 20);
    }

    @Override
    public Rotation2 getUnadjustedAngle() {
        return new Rotation2(0.0, 0.0, false);
        // return Rotation2.fromRadians(getAxis(Axis.YAW));
    }

    @Override
    public double getUnadjustedRate() {
        double[] xyz = new double[3];
        pigeon.getRawGyro(xyz);

        // return Math.toRadians(xyz[2]);
        return 0.0;
    }

    public double getAxis(Axis axis) {
      	double[] ypr = new double[3];
        pigeon.getYawPitchRoll(ypr);

        switch (axis) {
            case PITCH:
                return Math.toRadians(ypr[1]);
            case ROLL:
                return Math.toRadians(ypr[2]);
            case YAW:
                return Math.toRadians(ypr[0]);
            default:
                return 0.0;
        }
    }
}
