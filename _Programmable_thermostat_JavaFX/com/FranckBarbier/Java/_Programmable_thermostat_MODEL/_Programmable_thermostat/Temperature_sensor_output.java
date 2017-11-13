package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat;

import com.pauware.pauware_engine._Exception.Statechart_exception;

public interface Temperature_sensor_output {

    void ambient_temperature_changed(com.FranckBarbier.Java._Temperature.Temperature temperature) throws Statechart_exception;
}
