package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Switchs;

import com.pauware.pauware_engine._Core.*;
import com.pauware.pauware_engine._Exception.Statechart_exception;

/**
 * Java ME only
 */
import com.pauware.pauware_engine._Java_ME.*;

public class Fan_switch implements Fan_switch_input, Java_MEExecutor {

    /**
     * UML control flow
     */
    protected Fan_switch_output _programmable_thermostat;
    /**
     * UML statechart
     */
    protected AbstractStatechart _Is_auto;
    protected AbstractStatechart _Is_on;
    protected AbstractStatechart_monitor _Fan_switch_state_machine;

    /**
     * UML creation
     */
    private void init_structure(Fan_switch_output programmable_thermostat) throws Statechart_exception {
        _programmable_thermostat = programmable_thermostat;
    }

    private void init_behavior() throws Statechart_exception {
        _Is_auto = new Java_MEStatechart("Is auto");
        _Is_auto.inputState();
        _Is_on = new Java_MEStatechart("Is on");
        _Fan_switch_state_machine = new Java_MEStatechart_monitor(_Is_auto.xor(_Is_on), "Fan switch", AbstractStatechart_monitor.Show_on_system_out);
    }

    public void start() throws Statechart_exception {
        _Fan_switch_state_machine.fires("auto", _Is_on, _Is_auto);
        /**
         * **
         * _Fan_switch_state_machine.fires("on",_Is_auto,_Is_on,true,_programmable_thermostat,"fan_switch_turned_on");
         * ***
         */
        /* Java ME only */ _Fan_switch_state_machine.fires("on", _Is_auto, _Is_on, true, this, "programmable_thermostat_fan_switch_turned_on");
        _Fan_switch_state_machine.start();
    }

    public Fan_switch(Fan_switch_output programmable_thermostat) throws Statechart_exception {
        init_structure(programmable_thermostat);
        init_behavior();
    }

    /**
     * UML events
     */
    public void auto() throws Statechart_exception {
        _Fan_switch_state_machine.run_to_completion("auto");
    }

    public void on() throws Statechart_exception {
        _Fan_switch_state_machine.run_to_completion("on");
    }

    /**
     * UML <in>
     */
    public boolean in(String name) {
        return _Fan_switch_state_machine.in_state(name);
    }

    /**
     * Java ME only
     */
    public Object execute(String action, Object[] args) throws Throwable {
        /**
         * UML actions
         */
        Object result = null;
        if (action != null && action.equals("programmable_thermostat_fan_switch_turned_on")) {
            _programmable_thermostat.fan_switch_turned_on();
        }
        return result;
    }
}
