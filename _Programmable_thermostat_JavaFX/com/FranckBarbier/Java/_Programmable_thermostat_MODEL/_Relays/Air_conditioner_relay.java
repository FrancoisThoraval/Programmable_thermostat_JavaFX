package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Relays;

import com.pauware.pauware_engine._Core.*;
import com.pauware.pauware_engine._Exception.Statechart_exception;

import com.FranckBarbier.Java._Programmable_thermostat_MODEL._Simulation.Air_conditioner;
import com.FranckBarbier.Java._Temperature.*;

public class Air_conditioner_relay extends Relay {

    /**
     * UML statechart
     */
    protected AbstractStatechart _Air_conditioner_off;
    protected AbstractStatechart _Air_conditioner_on;
    protected AbstractStatechart_monitor _Air_conditioner_relay_state_machine;

    /**
     * UML creation
     */
    private void init_structure(Temperature ambient_temperature) throws Statechart_exception {
        _air_conditioner = new Air_conditioner(ambient_temperature);
    }

    protected void init_behavior() throws Statechart_exception {
        super.init_behavior();
        _Air_conditioner_off = (_Is_off.name("Air conditioner off")).set_entryAction(this, "disactivate");
        _Air_conditioner_off.inputState();
        _Air_conditioner_on = (_Is_on.name("Air conditioner on")).set_entryAction(this, "activate");
        _Air_conditioner_relay_state_machine = (AbstractStatechart_monitor) _Relay_state_machine.name("Air conditioner relay");
    }

    public void start() throws Statechart_exception {
        super.start();
        _air_conditioner.start();
    }

    public Air_conditioner_relay(Temperature ambient_temperature) throws Statechart_exception {
        init_structure(ambient_temperature);
        init_behavior();
    }

    /**
     * UML actions
     */
    public void activate() {
        _air_conditioner.activate();
    }

    public void disactivate() {
        _air_conditioner.disactivate();
    }
    /**
     * Implementation
     */
    protected Air_conditioner _air_conditioner;

    public boolean is_on() {
        return _Air_conditioner_on.active();
    }
}
