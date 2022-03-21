
   
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;


import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

/** An example command that uses an example subsystem. */
public class TestDriveMotors extends CommandBase {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private CANSparkMax leftMotor, rightMotor;
  int leftID, rightID;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public TestDriveMotors(CANSparkMax leftMotor, int leftMotorCanID, CANSparkMax rightMotor, int rightMotorCanID) {
    this.leftMotor = leftMotor;
    // Use addRequirements() here to declare subsystem dependencies.
    this.rightMotor = rightMotor;
    this.leftID = leftMotorCanID;
    this.rightID = rightMotorCanID;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    leftMotor.set(0.1);
    rightMotor.set(0.1);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    SmartDashboard.putNumber("Left Motor Position", leftMotor.getEncoder().getPosition());
    SmartDashboard.putNumber("Right Motor Position", rightMotor.getEncoder().getPosition());
    SmartDashboard.putNumber("Left Motor Voltage", leftMotor.getAppliedOutput());
    SmartDashboard.putNumber("Right Motor Voltage", rightMotor.getAppliedOutput());
    SmartDashboard.putNumber("Left Motor Velocity", leftMotor.getEncoder().getVelocity());
    SmartDashboard.putNumber("Right Motor Velocity", rightMotor.getEncoder().getVelocity());

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    StringBuilder motor1Obs = new StringBuilder("CAN ID ");
    motor1Obs.append(leftID);
    motor1Obs.append(" Left Motor Encoder Position: ");
    motor1Obs.append(leftMotor.getEncoder().getPosition());
    motor1Obs.append(" Motor 1 Encoder Velocity");
    motor1Obs.append(leftMotor.getEncoder().getVelocity());
    StringBuilder motor2Obs = new StringBuilder("CAN ID ");
    motor2Obs.append(rightID);
    motor2Obs.append(" Right Motor Encoder Position: ");
    motor2Obs.append(rightMotor.getEncoder().getPosition());
    motor2Obs.append(" Motor 2 Encoder Velocity: ");
    motor2Obs.append(rightMotor.getEncoder().getVelocity());
    
    System.out.println(motor1Obs.toString());
    System.out.println(motor2Obs.toString());

    leftMotor.setVoltage(0);
    rightMotor.setVoltage(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}