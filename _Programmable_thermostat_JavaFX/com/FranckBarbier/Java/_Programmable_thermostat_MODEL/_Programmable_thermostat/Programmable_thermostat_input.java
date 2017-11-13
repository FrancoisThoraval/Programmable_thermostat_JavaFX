package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat;

import com.pauware.pauware_engine._Exception.Statechart_exception;

public interface Programmable_thermostat_input {

    void fan_switch_auto() throws Statechart_exception;

    void fan_switch_on() throws Statechart_exception;

    void f_c() throws Statechart_exception;

    void hold_temp() throws Statechart_exception;

    void run_program() throws Statechart_exception;

    void season_switch_cool() throws Statechart_exception;

    void season_switch_heat() throws Statechart_exception;

    void season_switch_off() throws Statechart_exception;

    void set_clock() throws Statechart_exception;

    void set_day() throws Statechart_exception;

    void temp_down() throws Statechart_exception;

    void temp_up() throws Statechart_exception;

    void time_backward() throws Statechart_exception;

    void time_forward() throws Statechart_exception;

    void view_program() throws Statechart_exception;
}
