package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Simulation;

public final class Furnace extends Thread {

    final private com.FranckBarbier.Java._Temperature.Temperature _ambient_temperature;
    final private java.util.Random _random = new java.util.Random();
    volatile private boolean _active = false;

    public Furnace(com.FranckBarbier.Java._Temperature.Temperature ambient_temperature) {
        super("Furnace");
        setPriority(Thread.MIN_PRIORITY);
        _ambient_temperature = ambient_temperature;
    }

    public void activate() {
        _active = true;
    }

    public void disactivate() {
        _active = false;
    }

    public void run() {
        while (true) {
            if (_active) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                int probability = _random.nextInt(com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Temperature_sensor.Interval + 1);
                if (probability < com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Temperature_sensor.Interval) {
                    _ambient_temperature.increment();
                } else {
                    _ambient_temperature.decrement();
                }
            }
        }
    }
}
