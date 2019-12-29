package org.frcteam4611.common.math.spline;

import org.frcteam4611.common.math.Rotation2;
import org.frcteam4611.common.math.Vector2;

public abstract class Spline {

    public abstract Vector2 getPoint(double t);

    public abstract Rotation2 getHeading(double t);
}
