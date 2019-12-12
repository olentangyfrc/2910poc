package org.frcteam2910.c2019.subsystems;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.frcteam2910.common.Logger;
import org.frcteam2910.common.robot.drivers.Pigeon;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class Superstructure {
    private static final Logger LOGGER = new Logger(Superstructure.class);

    private static final Superstructure instance = new Superstructure();

    private Pigeon pigeon = new Pigeon(21);

    private Superstructure() {
        pigeon.calibrate();
        pigeon.setInverted(true);
    }


    public static Superstructure getInstance() {
        return instance;
    }

    public Pigeon getGyroscope() {
        return pigeon;
    }
}
