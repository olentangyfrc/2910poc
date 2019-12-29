package org.frcteam4611.c2019.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.frcteam4611.c2019.Robot;
import org.frcteam4611.c2019.subsystems.DrivetrainSubsystem;
import org.frcteam4611.common.math.Rotation2;
import org.frcteam4611.common.math.Vector2;

public class HolonomicDriveCommand extends Command {
    public HolonomicDriveCommand() {
        requires(DrivetrainSubsystem.getInstance());
    }

    @Override
    protected void execute() {

        double forward = Robot.getOi().getForward();
        double strafe = Robot.getOi().getStrafe();
        double rotation = Robot.getOi().getRotation();

        boolean robotOriented = Robot.getOi().getRobotOriented();
        boolean reverseRobotOriented = Robot.getOi().getReverseRobotOriented();

        Vector2 translation = new Vector2(forward, strafe);

        if (reverseRobotOriented) {
            robotOriented = true;
            translation = translation.rotateBy(Rotation2.fromDegrees(180.0));
        }

        DrivetrainSubsystem.getInstance().holonomicDrive(translation, rotation, !robotOriented);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}