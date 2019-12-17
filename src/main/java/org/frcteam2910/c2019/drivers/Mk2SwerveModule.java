package org.frcteam2910.c2019.drivers;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Spark;

import org.frcteam2910.common.Logger;
import org.frcteam2910.common.control.PidConstants;
import org.frcteam2910.common.control.PidController;
import org.frcteam2910.common.drivers.SwerveModule;
import org.frcteam2910.common.math.Vector2;

public class Mk2SwerveModule extends SwerveModule {
    private static Logger logger = new Logger(Mk2SwerveModule.class);
    //private static final PidConstants ANGLE_CONSTANTS = new PidConstants(.25, 0.0, 0.0001);
    private static final PidConstants ANGLE_CONSTANTS = new PidConstants(.25, 0.0, 0.000);
    private static final double DRIVE_TICKS_PER_INCH = 1.0 / (4.0 * Math.PI / 60.0 * 15.0 / 20.0 * 24.0 / 38.0 * 18.0); // 0.707947

    private static final double CAN_UPDATE_RATE = 50.0;

    private final double angleOffset;

    private CANSparkMax angleMotor;
    private AnalogInput angleEncoder;
    private CANSparkMax driveMotor;
    private CANEncoder driveEncoder;

    private final Object canLock = new Object();
    private double driveEncoderTicks = 0.0;
    private double drivePercentOutput = 0.0;
    private double driveVelocityRpm = 0.0;
    private double driveCurrent = 0.0;

    private Notifier canUpdateNotifier = new Notifier(() -> {
        double driveEncoderTicks = driveEncoder.getPosition();
        synchronized (canLock) {
            Mk2SwerveModule.this.driveEncoderTicks = driveEncoderTicks;
        }

        double driveVelocityRpm = driveEncoder.getVelocity();
        synchronized (canLock) {
            Mk2SwerveModule.this.driveVelocityRpm = driveVelocityRpm;
        }

        double localDriveCurrent = driveMotor.getOutputCurrent();
        synchronized (canLock) {
            driveCurrent = localDriveCurrent;
        }

        double drivePercentOutput;
        synchronized (canLock) {
            drivePercentOutput = Mk2SwerveModule.this.drivePercentOutput;
        }
        driveMotor.set(drivePercentOutput);
    });

    private PidController angleController = new PidController(ANGLE_CONSTANTS);

    public Mk2SwerveModule(Vector2 modulePosition, double angleOffset,
                           CANSparkMax angleMotor, CANSparkMax driveMotor, AnalogInput angleEncoder) {
        super(modulePosition);
        this.angleOffset = angleOffset;
        this.angleMotor = angleMotor;
        this.angleEncoder = angleEncoder;
        this.driveMotor = driveMotor;
        this.driveEncoder = new CANEncoder(driveMotor);

        driveMotor.setSmartCurrentLimit(60);
        driveMotor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 500);
        driveMotor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 3);

        angleController.setInputRange(0.0, 2.0 * Math.PI);
        angleController.setContinuous(true);
        angleController.setOutputRange(-0.5, 0.5);

        canUpdateNotifier.startPeriodic(1.0 / CAN_UPDATE_RATE);
    }

    @Override
    protected double readAngle() {
        double angle = (1.0 - angleEncoder.getVoltage() / RobotController.getVoltage5V()) * 2.0 * Math.PI + angleOffset;
        angle %= 2.0 * Math.PI;
        if (angle < 0.0) {
            angle += 2.0 * Math.PI;
        }

        return angle;
    }

    @Override
    protected double readDistance() {
        double driveEncoderTicks;
        synchronized (canLock) {
            driveEncoderTicks = this.driveEncoderTicks;
        }

        return driveEncoderTicks / DRIVE_TICKS_PER_INCH;
    }

    protected double readVelocity() {
        double driveVelocityRpm;
        synchronized (canLock) {
            driveVelocityRpm = this.driveVelocityRpm;
        }

        return driveVelocityRpm * (1.0 / 60.0) / DRIVE_TICKS_PER_INCH;
    }

    protected double readDriveCurrent() {
        double localDriveCurrent;
        synchronized (canLock) {
            localDriveCurrent = driveCurrent;
        }

        return localDriveCurrent;
    }

    @Override
    public double getCurrentVelocity() {
        return readVelocity();
    }

    @Override
    public double getDriveCurrent() {
        return readDriveCurrent();
    }

    @Override
    protected void setTargetAngle(double angle) {
        angleController.setSetpoint(angle);
    }

    @Override
    protected void setDriveOutput(double output) {
        synchronized (canLock) {
            this.drivePercentOutput = output;
        }
    }

    @Override
    public void updateState(double dt) {
        super.updateState(dt);

        double c = getCurrentAngle();
        double sp = angleController.getSetpoint();
        double r = angleController.calculate(c, dt);

        if (DriverStation.getInstance().isEnabled() && getName().equals("Front Left")) {
            logger.info("sp[%f] ta[%f] c[%f] e[%f] r[%f]", sp, this.targetAngle, c, (sp - c), r);
        }
        angleMotor.set(r);
    }
}
