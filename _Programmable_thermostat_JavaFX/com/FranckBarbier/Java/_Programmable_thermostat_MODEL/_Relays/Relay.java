package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Relays;

import com.pauware.pauware_engine._Core.*;
import com.pauware.pauware_engine._Exception.Statechart_exception;

/**
 * Java ME only
 */
import com.pauware.pauware_engine._Java_ME.*;

abstract public class Relay implements Java_MEExecutor {

    /**
     * UML statechart
     */
    protected AbstractStatechart _Is_off;
    protected AbstractStatechart _Is_on;
    protected AbstractStatechart_monitor _Relay_state_machine;

    /**
     * UML creation
     */
    protected void init_behavior() throws Statechart_exception {
        _Is_off = new Java_MEStatechart("Is off");
        _Is_off.inputState();
        _Is_on = new Java_MEStatechart("Is on");
        _Relay_state_machine = new Java_MEStatechart_monitor(_Is_off.xor(_Is_on), "Relay", AbstractStatechart_monitor.Show_on_system_out);
    }

    protected void start() throws Statechart_exception {
        _Relay_state_machine.fires("run", _Is_off, _Is_on);
        _Relay_state_machine.fires("stop", _Is_on, _Is_off);
        _Relay_state_machine.start();
    }

    /**
     * UML events
     */
    public void run() throws Statechart_exception {
        _Relay_state_machine.run_to_completion("run");
    }

    public void stop() throws Statechart_exception {
        _Relay_state_machine.run_to_completion("stop");
    }

    /**
     * UML actions
     */
    abstract public void activate();

    abstract public void disactivate();

    /**
     * Java ME only
     */
    public Object execute(String action, Object[] args) throws Throwable {
        /**
         * UML actions
         */
        Object result = null;
        if (action != null && action.equals("activate")) {
            activate();
        }
        if (action != null && action.equals("disactivate")) {
            disactivate();
        }
        if (action != null && action.equals("run")) {
            run();
        }
        if (action != null && action.equals("stop")) {
            stop();
        }
        return result;
    }
}
