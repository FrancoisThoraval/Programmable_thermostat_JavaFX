package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Relays;

import com.pauware.pauware_engine._Core.*;
import com.pauware.pauware_engine._Exception.Statechart_exception;

import com.FranckBarbier.Java._Programmable_thermostat_MODEL._Simulation.Furnace;
import com.FranckBarbier.Java._Temperature.*;

public class Furnace_relay extends Relay {

    /**
     * UML statechart
     */
    protected AbstractStatechart _Furnace_off;
    protected AbstractStatechart _Furnace_on;
    protected AbstractStatechart_monitor _Furnace_relay_state_machine;

    /**
     * UML creation
     */
    private void init_structure(Temperature ambient_temperature) throws Statechart_exception {
        _furnace = new Furnace(ambient_temperature);
    }

    protected void init_behavior() throws Statechart_exception {
        super.init_behavior();
        _Furnace_off = (_Is_off.name("Furnace off")).set_entryAction(this, "disactivate");
        _Furnace_off.inputState();
        _Furnace_on = (_Is_on.name("Furnace on")).set_entryAction(this, "activate");
        _Furnace_relay_state_machine = (AbstractStatechart_monitor) _Relay_state_machine.name("Furnace relay");
    }

    public void start() throws Statechart_exception {
        super.start();
        _furnace.start();
    }

    public Furnace_relay(Temperature ambient_temperature) throws Statechart_exception {
        init_structure(ambient_temperature);
        init_behavior();
    }

    /**
     * UML events
     */
    /**
     * UML actions
     */
    public void activate() {
        _furnace.activate();
    }

    public void disactivate() {
        _furnace.disactivate();
    }
    /**
     * Implementation
     */
    protected Furnace _furnace;

    public boolean is_on() {
        return _Furnace_on.active();
    }
}
