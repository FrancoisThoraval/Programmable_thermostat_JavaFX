package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Switchs;

import com.pauware.pauware_engine._Exception.Statechart_exception;

public interface Fan_switch_input {

    void auto() throws Statechart_exception;

    void on() throws Statechart_exception;
}