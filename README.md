This is meant to simulate a REST API in the backed using NanoHttpd as the simulation.

to run the simluation run the following commands for windows:
javac -cp ".;libs/nanohttpd-2.3.1.jar" MockDevices.java
java -cp ".;libs/nanohttpd-2.3.1.jar" MockDevices


Since the main code is simlulated I needed to simulate the flips and the switches and the thermostat so I used standard input to fill that in.
