package org.frcteam2910.c2019;

import org.frcteam2910.common.robot.input.XboxController;

public class OI {
    public final XboxController primaryController = new XboxController(0);


    public OI() {
        primaryController.getLeftXAxis().setInverted(true);
        primaryController.getRightXAxis().setInverted(true);

        primaryController.getRightXAxis().setScale(0.45);
    }

}

