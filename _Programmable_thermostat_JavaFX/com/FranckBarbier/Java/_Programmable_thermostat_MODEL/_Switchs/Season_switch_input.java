package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Switchs;

import com.pauware.pauware_engine._Exception.Statechart_exception;

public interface Season_switch_input {

    void cool() throws Statechart_exception;

    void heat() throws Statechart_exception;

    void off() throws Statechart_exception;
}