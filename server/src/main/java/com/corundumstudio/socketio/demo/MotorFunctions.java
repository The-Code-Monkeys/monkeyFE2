package com.corundumstudio.socketio.demo;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.Gpio;

public class MotorFunctions {

    private static GpioPinDigitalOutput motor1A;
    private static GpioPinDigitalOutput motor1B;
    private static GpioPinPwmOutput motor1PWM;

    private static GpioPinDigitalOutput motor2A;
    private static GpioPinDigitalOutput motor2B;
    private static GpioPinPwmOutput motor2PWM;

    public static void init() {
        // Create a GPIO controller instance
        GpioController gpio = GpioFactory.getInstance();

        // Initialize the motor pins for motor 1
        motor1A = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW);
        motor1B = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
        motor1PWM = GpioFactory.getInstance().provisionPwmOutputPin(RaspiPin.GPIO_23);

        // Initialize the motor pins for motor 2
        motor2A = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);
        motor2B = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW);
        motor2PWM = GpioFactory.getInstance().provisionPwmOutputPin(RaspiPin.GPIO_26);

        // Initialize PWM settings
        Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
        Gpio.pwmSetRange(1000); // Set the range for your motor speed
        Gpio.pwmSetClock(500);

        // Set the motor speed to a slower value initially
        motor1PWM.setPwm(200); // Adjust the speed as needed
        motor2PWM.setPwm(200); // Adjust the speed as needed
    }

    // Move the robot forward at a slower speed
    public static void forward() {
        // Set the pins for forward movement
        motor1A.setState(PinState.HIGH); // Motor 1 forward
        motor1B.setState(PinState.LOW); // Motor 1 forward
        motor2A.setState(PinState.HIGH); // Motor 2 forward
        motor2B.setState(PinState.LOW); // Motor 2 forward
    }

    // Move the robot backward at a slower speed
    public static void backward() {
        // Set the pins for backward movement
        motor1A.setState(PinState.LOW); // Motor 1 backward
        motor1B.setState(PinState.HIGH); // Motor 1 backward
        motor2A.setState(PinState.LOW); // Motor 2 backward
        motor2B.setState(PinState.HIGH); // Motor 2 backward
    }

    // Turn the robot left at a slower speed
    public static void left() {
        // Set the pins for left turn
        motor1A.setState(PinState.LOW); // Motor 1 backward (to turn left)
        motor1B.setState(PinState.HIGH); // Motor 1 backward (to turn left)
        motor2A.setState(PinState.HIGH); // Motor 2 forward (to turn left)
        motor2B.setState(PinState.LOW); // Motor 2 forward (to turn left)
    }

    // Turn the robot right at a slower speed
    public static void right() {
        // Set the pins for right turn
        motor1A.setState(PinState.HIGH); // Motor 1 forward (to turn right)
        motor1B.setState(PinState.LOW); // Motor 1 forward (to turn right)
        motor2A.setState(PinState.LOW); // Motor 2 backward (to turn right)
        motor2B.setState(PinState.HIGH); // Motor 2 backward (to turn right)
    }

    // Stop the robot
    public static void stop() {
        // Set all pins to LOW to stop the motors
        motor1A.setState(PinState.LOW);
        motor1B.setState(PinState.LOW);
        motor2A.setState(PinState.LOW);
        motor2B.setState(PinState.LOW);
        motor1PWM.setPwm(0); // Set motor speed to 0 (stop)
        motor2PWM.setPwm(0); // Set motor speed to 0 (stop)
    }
}
