package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.XboxController;
import frc.robot.util.XboxController.Axis;

public class Drivetrain extends SubsystemBase {
    public enum DriveMode {
        ARCADE,
        TANK,
        CURVATURE
    }
    public CANSparkMax rightMotor1, rightMotor2, leftMotor1, leftMotor2;
    private MotorControllerGroup left, right;
    private DifferentialDrive drive;
    private RelativeEncoder leftEncoder, rightEncoder;
    private SlewRateLimiter throttleFilter, turnFilter;
    private double throttleLimit = 1, turnLimit = 1;
    private double turnConstant = 1;
    private double throttleConstant = 1;
    private DriveMode driveMode;
    private SendableChooser<DriveMode> driveModeChooser;

    public Drivetrain() {
        rightMotor1 = new CANSparkMax(Constants.rightMotor1, MotorType.kBrushless);
        rightMotor2 = new CANSparkMax(Constants.rightMotor2, MotorType.kBrushless);
        leftMotor1 = new CANSparkMax(Constants.leftMotor1, MotorType.kBrushless);
        leftMotor2 = new CANSparkMax(Constants.leftMotor2, MotorType.kBrushless);

        rightMotor1.setInverted(true);
        rightMotor2.setInverted(true);
        leftMotor1.setInverted(false);
        leftMotor2.setInverted(false);

        right = new MotorControllerGroup(rightMotor1, rightMotor2);
        left = new MotorControllerGroup(leftMotor1, leftMotor2);

        leftEncoder = leftMotor1.getEncoder();
        rightEncoder = rightMotor1.getEncoder();

        drive = new DifferentialDrive(left, right);

        throttleFilter = new SlewRateLimiter(throttleLimit);
        turnFilter = new SlewRateLimiter(turnLimit);

        driveModeChooser = new SendableChooser<DriveMode>();
        driveModeChooser.addOption("Arcade", DriveMode.ARCADE);
        driveModeChooser.addOption("Tank", DriveMode.TANK);
        driveModeChooser.setDefaultOption("Curvature", DriveMode.CURVATURE);
        SmartDashboard.putData(driveModeChooser);
        SmartDashboard.putNumber("Throttle Constant", throttleConstant);
        SmartDashboard.putNumber("Turn Constant", turnConstant);
    }

    public void drive(XboxController driver) {
        switch (driveMode) {
            case ARCADE:
                arcadeDrive(driver.getAxisValue(Axis.LEFT_Y), driver.getAxisValue(Axis.RIGHT_X));
            case TANK:
                tankDrive(driver.getAxisValue(Axis.LEFT_Y), driver.getAxisValue(Axis.RIGHT_Y));
            case CURVATURE:
                curvatureDrive(driver.getAxisValue(Axis.LEFT_Y), driver.getAxisValue(Axis.RIGHT_X), !isMoving());
        }
    }

    @Override
    public void periodic() {
        driveMode = driveModeChooser.getSelected();
        throttleConstant = SmartDashboard.getNumber("Throttle Constant", throttleConstant);
        turnConstant = SmartDashboard.getNumber("Turn Constant", turnConstant);
        double throttleChange = SmartDashboard.getNumber("Throttle Filter", throttleLimit);
        if (throttleChange != throttleLimit && !isMoving()) {
            throttleFilter = new SlewRateLimiter(throttleChange);
        }
        double turnChange = SmartDashboard.getNumber("Turn Filter", turnLimit);
        if (turnChange != turnLimit && !isMoving()) {
            turnFilter = new SlewRateLimiter(turnChange);
        }

    }

    public void curvatureDrive(double throttle, double turn, boolean inPlace) {
        drive.curvatureDrive(throttleFilter.calculate(throttle*throttleConstant), turnFilter.calculate(turn*turnConstant), inPlace);
    }

    public void arcadeDrive(double throttle, double turn) {
        drive.arcadeDrive(throttleFilter.calculate(throttle*throttleConstant), turnFilter.calculate(turn*turnConstant));
    }

    public void tankDrive(double left, double right) {
        drive.tankDrive(left*throttleConstant, right*throttleConstant);
    }

    public boolean isMoving() {
        return Math.abs(leftEncoder.getVelocity()) > 0.05 || Math.abs(rightEncoder.getVelocity()) > 0.05;
    }
    

    
}
