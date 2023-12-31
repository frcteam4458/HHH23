// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.TeleopCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ExtensionArmSubsystem;
import frc.robot.subsystems.GripperSubsystem;
import frc.robot.subsystems.PivotSubsystem;
import frc.robot.subsystems.GripperSubsystem.GripperState;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.RepeatCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PrintCommand;
public class RobotContainer
{
  private final CommandJoystick driverController = new CommandJoystick(OperatorConstants.kDriverControllerPort);
  private GripperSubsystem gripperSubsystem = new GripperSubsystem();
  private PivotSubsystem pivotSubsystem = new PivotSubsystem();
  private ExtensionArmSubsystem extensionArmSubsystem = new ExtensionArmSubsystem();
  private DriveSubsystem driveSubsystem = new DriveSubsystem();
  private TeleopCommand teleopCommand = new TeleopCommand(driveSubsystem, this);

  private SendableChooser driveChooser = new SendableChooser<Integer>();

  private RepeatCommand extensionCommand;


  public RobotContainer()
  {
    configureBindings();

    driveChooser.addOption("Tank", 1);
    driveChooser.setDefaultOption("Arcade", 0);
  }

  private void configureBindings() 
  {

    SmartDashboard.putData("Chooser", driveChooser);
    // Controller 0 Button 4 opens gripper
    driverController.button(4).onTrue(Commands.runOnce(
    () -> { gripperSubsystem.setState(GripperState.OPEN); } ));
    
    // Controller 0 Button 5 closes gripper
    driverController.button(5).onTrue(Commands.runOnce(
    () -> { gripperSubsystem.setState(GripperState.CLOSE); } ));

    // Temp extension arm
    // driverController.button(1).onTrue(Commands.runOnce(
    //   () -> { extensionArmSubsystem.setMotor(0);}
    // ));
    
    // // Negative
    // driverController.button(2).onTrue(Commands.runOnce(
    //   () -> { extensionArmSubsystem.setMotor(-.25);}
    // ));

    // // Positive
    // driverController.button(3).onTrue(Commands.runOnce(
    //   () -> { extensionArmSubsystem.setMotor(.25);}
    // ));


    // Manual control of pivot only when Controller 0 Axis 1 exceeds deadzone in positive or negative direction 
    driverController.axisGreaterThan(1, OperatorConstants.kDeadzone).whileTrue(
      Commands.run(() -> { pivotSubsystem.adjustSetpoint(-driverController.getRawAxis(1) * 2); },
      pivotSubsystem));

    driverController.axisLessThan(1, -OperatorConstants.kDeadzone).whileTrue(
      Commands.run(() -> { pivotSubsystem.adjustSetpoint(-driverController.getRawAxis(1) * 2); },
      pivotSubsystem));



    // Manual control of extension only when Controller 0 Axis 0 exceeds deadzone in positive or negative direction (Non-PID loop)
    driverController.axisGreaterThan(0, OperatorConstants.kExtensionDeadzone).whileTrue(
      Commands.run(() -> { extensionArmSubsystem.adjustSetpoint(driverController.getRawAxis(0) * 3); },
      extensionArmSubsystem));

    driverController.axisLessThan(0, -OperatorConstants.kExtensionDeadzone).whileTrue(
      Commands.run(() -> { extensionArmSubsystem.adjustSetpoint(driverController.getRawAxis(0) * 1); },
      extensionArmSubsystem));
    
    driveSubsystem.setDefaultCommand(teleopCommand);
    // extensionArmSubsystem.setDefaultCommand(Commands.run(() -> {
    //   extensionArmSubsystem.setMotor(0);
    // }, extensionArmSubsystem));
  }

  public Command getAutonomousCommand() 
  {
    return Commands.print("No autonomous command configured");
  }

  public int getDriveConfig() {
    return (int) driveChooser.getSelected();
  }
}
