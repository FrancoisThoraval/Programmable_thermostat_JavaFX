package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Switchs;

import com.pauware.pauware_engine._Core.*;
import com.pauware.pauware_engine._Exception.Statechart_exception;

/**
 * Java ME only
 */
import com.pauware.pauware_engine._Java_ME.*;

public class Season_switch implements Season_switch_input, Java_MEExecutor {

    /**
     * UML control flow
     */
    protected Season_switch_output _programmable_thermostat;
    /**
     * UML statechart
     */
    protected AbstractStatechart _Is_cool;
    protected AbstractStatechart _Is_heat;
    protected AbstractStatechart _Is_off;
    protected AbstractStatechart _Is_on;
    protected AbstractStatechart_monitor _Season_switch_state_machine;

    /**
     * UML creation
     */
    private void init_structure(Season_switch_output programmable_thermostat) throws Statechart_exception {
        _programmable_thermostat = programmable_thermostat;
    }

    private void init_behavior() throws Statechart_exception {
        _Is_cool = new Java_MEStatechart("Is cool");
        _Is_heat = new Java_MEStatechart("Is heat");
        _Is_off = new Java_MEStatechart("Is off");
        _Is_off.inputState();
        _Is_on = (_Is_cool.xor(_Is_heat)).name("Is on");
        _Season_switch_state_machine = new Java_MEStatechart_monitor(_Is_on.xor(_Is_off), "Season switch", AbstractStatechart_monitor.Show_on_system_out);
    }

    public void start() throws Statechart_exception {
        _Season_switch_state_machine.fires("cool", _Is_heat, _Is_cool);
        _Season_switch_state_machine.fires("cool", _Is_off, _Is_cool);
        _Season_switch_state_machine.fires("heat", _Is_cool, _Is_heat);
        _Season_switch_state_machine.fires("heat", _Is_off, _Is_heat);
        /**
         * **
         * _Season_switch_state_machine.fires("off",_Is_on,_Is_off,true,_programmable_thermostat,"season_switch_turned_off");
         * ***
         */
        /* Java ME only */ _Season_switch_state_machine.fires("off", _Is_on, _Is_off, true, this, "programmable_thermostat_season_switch_turned_off");
        _Season_switch_state_machine.start();
    }

    public Season_switch(Season_switch_output programmable_thermostat) throws Statechart_exception {
        init_structure(programmable_thermostat);
        init_behavior();
    }

    /**
     * UML events
     */
    public void cool() throws Statechart_exception {
        _Season_switch_state_machine.run_to_completion("cool");
    }

    public void heat() throws Statechart_exception {
        _Season_switch_state_machine.run_to_completion("heat");
    }

    public void off() throws Statechart_exception {
        _Season_switch_state_machine.run_to_completion("off");
    }

    /**
     * UML <in>
     */
    public boolean in(String name) {
        return _Season_switch_state_machine.in_state(name);
    }

    /**
     * Java ME only
     */
    public Object execute(String action, Object[] args) throws Throwable {
        /**
         * UML actions
         */
        Object result = null;
        if (action != null && action.equals("programmable_thermostat_season_switch_turned_off")) {
            _programmable_thermostat.season_switch_turned_off();
        }
        return result;
    }
}
