package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Relays;

import com.pauware.pauware_engine._Core.*;
import com.pauware.pauware_engine._Exception.Statechart_exception;

public class Fan_relay extends Relay {

    /**
     * UML statechart
     */
    protected AbstractStatechart _Fan_off;
    protected AbstractStatechart _Fan_on;
    protected AbstractStatechart_monitor _Fan_relay_state_machine;

    /**
     * UML creation
     */
    protected void init_behavior() throws Statechart_exception {
        super.init_behavior();
        _Fan_off = (_Is_off.name("Fan off")).set_entryAction(this, "disactivate");
        _Fan_off.inputState();
        _Fan_on = (_Is_on.name("Fan on")).set_entryAction(this, "activate");
        _Fan_relay_state_machine = (AbstractStatechart_monitor) _Relay_state_machine.name("Fan relay");
    }

    public void start() throws Statechart_exception {
        super.start();
    }

    public Fan_relay() throws Statechart_exception {
        init_behavior();
    }

    /**
     * UML events
     */
    public void stop() throws Statechart_exception {
        // Inherited from 'Relay' without added value here...
    }

    public void stop(Boolean guard) throws Statechart_exception {
        _Fan_relay_state_machine.fires("stop", _Fan_on, _Fan_off, guard.booleanValue());
        _Fan_relay_state_machine.run_to_completion("stop");
    }

    /**
     * UML actions
     */
    public void activate() {
    }

    public void disactivate() {
    }

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
            stop((Boolean) args[0]);
        }
        return result;
    }
}
