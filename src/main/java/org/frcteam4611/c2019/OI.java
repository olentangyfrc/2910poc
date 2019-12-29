package org.frcteam4611.c2019;

import org.frcteam4611.common.robot.input.XboxController;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class OI {
    private final XboxController primaryController = new XboxController(0);

    private final Joystick leftJoy = new Joystick(1);
    private final Joystick rightJoy = new Joystick(2);
    private ShuffleboardTab tab ;

    private NetworkTableEntry useXBox = null;
    private boolean usexb = true;

    public OI() {
        primaryController.getLeftXAxis().setInverted(true);
        primaryController.getRightXAxis().setInverted(true);

        primaryController.getRightXAxis().setScale(0.45);


        tab = Shuffleboard.getTab("OI");

        useXBox = tab.add("Use XBox", usexb).withSize(1, 1).withPosition(0, 0).getEntry();
    }


    public double getForward() {
        usexb    = useXBox.getBoolean(usexb);
        if (usexb)
            return primaryController.getLeftYAxis().get(true);
        else 
            return getFiltered(-leftJoy.getY());
    }

    public double getStrafe() {
        usexb    = useXBox.getBoolean(usexb);
        if (usexb)
            return primaryController.getLeftXAxis().get(true);
        else
            return getFiltered(-leftJoy.getX());

    }

    public double getRotation() {
        usexb    = useXBox.getBoolean(usexb);
        boolean ignoreScalars = primaryController.getLeftBumperButton().get();

        if (usexb)
            return primaryController.getRightXAxis().get(true, ignoreScalars);
        else
            return getFiltered(-rightJoy.getX());
    }

    public boolean getRobotOriented() {
        return (primaryController.getXButton().get());
    }

    public boolean getReverseRobotOriented() {
        return primaryController.getYButton().get();
    }


    private double getFiltered(double v) {
        if (Math.abs(v) < 0.15) {
            return 0.0;
        }

        return v;
    }
}

