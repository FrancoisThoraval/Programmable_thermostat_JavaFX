package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat;

import com.pauware.pauware_engine._Core.*;
import com.pauware.pauware_engine._Exception.Statechart_exception;

import com.FranckBarbier.Java._Programmable_thermostat_MODEL._Simulation.*;
import com.FranckBarbier.Java._Temperature.*;

public class Temperature_sensor extends AbstractTimer_monitor implements Runnable {

    private Temperature_sensor_output _programmable_thermostat;
    private Temperature _ambient_temperature;

    public static final int Interval = 4;
    private java.util.Random _random = new java.util.Random();
    private Thread _simulation;
    volatile private boolean _stop = false;

    public Temperature_sensor(Temperature_sensor_output programmable_thermostat, Temperature ambient_temperature) {
        _programmable_thermostat = programmable_thermostat;
        _ambient_temperature = ambient_temperature;
        _simulation = new Thread(this, "Temperature sensor");
        _simulation.setPriority(Thread.MIN_PRIORITY);
    }

    public void start() throws Statechart_exception {
        _simulation.start();
        // The ambient temperature is sent to the thermostat each sec.:
        to_be_set(1000);
    }

    public void stop() {
        to_be_killed();
        _stop = true;
    }

    public void run() {
        while (!_stop) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            // For Northern hemisphere only!
            int probability = _random.nextInt(Interval);
            if (probability < Interval - 1) {
                if (Season.Winter()) {
                    _ambient_temperature.decrement();
                }
                if (Season.Summer()) {
                    _ambient_temperature.increment();
                }
            } else { // 'probability' is 1/Interval
                if (Season.Winter()) {
                    _ambient_temperature.increment();
                }
                if (Season.Summer()) {
                    _ambient_temperature.decrement();
                }
            }
            probability = _random.nextInt(2);
            if (probability < 1) {
                if (Season.Autumn()) {
                    _ambient_temperature.decrement();
                }
                if (Season.Spring()) {
                    _ambient_temperature.increment();
                }
            } else { // 'probability' is 1/2
                if (Season.Autumn()) {
                    _ambient_temperature.increment();
                }
                if (Season.Spring()) {
                    _ambient_temperature.decrement();
                }
            }
        }
    }

    public void time_out(long delay, AbstractStatechart context) throws Statechart_exception {
        _programmable_thermostat.ambient_temperature_changed(_ambient_temperature);
    }

    public void time_out_error(Statechart_exception se) throws Statechart_exception {
        se.printStackTrace();
//        possible fault recovery here...
    }
}
