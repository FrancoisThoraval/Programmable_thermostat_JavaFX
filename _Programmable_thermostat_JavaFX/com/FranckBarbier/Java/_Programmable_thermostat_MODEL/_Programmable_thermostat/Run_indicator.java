package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat;

import com.pauware.pauware_engine._Core.*;
import com.pauware.pauware_engine._Exception.Statechart_exception;

/**
 * Java ME only
 */
import com.pauware.pauware_engine._Java_ME.*;

public class Run_indicator implements Java_MEExecutor /**
 * **java.io.Serializable***
 */
{
//    public static final String Status = "Run indicator status";
//    private String status;
//    private java.beans.PropertyChangeSupport propertySupport;

    /**
     * UML control flow
     */
    protected Run_indicator_output _programmable_thermostat;
    /**
     * UML statechart
     */
    public static String Everything_off = "Everything off";

    protected AbstractStatechart _Everything_off;
    protected AbstractStatechart _Something_on;
    protected AbstractStatechart_monitor _Run_indicator_state_machine;

    /**
     * UML creation
     */
    private void init_structure(Run_indicator_output programmable_thermostat) throws Statechart_exception {
        _programmable_thermostat = programmable_thermostat;
    }

    private void init_behavior() throws Statechart_exception {
        _Everything_off = new Java_MEStatechart(Everything_off);
        _Everything_off.inputState();
        _Something_on = new Java_MEStatechart("Something on");
        _Run_indicator_state_machine = new Java_MEStatechart_monitor(_Everything_off.xor(_Something_on), "Run indicator", AbstractStatechart_monitor.Show_on_system_out);
    }

    public void start() throws Statechart_exception {
        _Run_indicator_state_machine.fires("off", _Something_on, _Everything_off);
        _Run_indicator_state_machine.fires("on", _Everything_off, _Something_on);
        _Run_indicator_state_machine.start();
    }

    public Run_indicator(Run_indicator_output programmable_thermostat) throws Statechart_exception {
        init_structure(programmable_thermostat);
        init_behavior();
//        status = getStatus();
//        propertySupport = new java.beans.PropertyChangeSupport(this);
//        propertySupport.addPropertyChangeListener(programmable_thermostat);
    }

    /**
     * JavaBeans component model not supported by Java ME
     */
    /* private String getStatus() {
     return _Run_indicator_state_machine.current_state();
     }
     private void setStatus(String newValue) {
     String oldValue = status;
     status = newValue;
     propertySupport.firePropertyChange(Status,oldValue,newValue);
     }
     private void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
     propertySupport.addPropertyChangeListener(listener);
     }
     private void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
     propertySupport.removePropertyChangeListener(listener);
     } */
    /**
     * UML events
     */
    public void off() throws Statechart_exception {
        _Run_indicator_state_machine.run_to_completion("off");
        // setStatus(_Run_indicator_state_machine.current_state());
        _programmable_thermostat.propertyChange(_Run_indicator_state_machine.current_state());
    }

    public void on() throws Statechart_exception {
        _Run_indicator_state_machine.run_to_completion("on");
        // setStatus(_Run_indicator_state_machine.current_state());
        _programmable_thermostat.propertyChange(_Run_indicator_state_machine.current_state());
    }

    /**
     * Java ME only
     */
    public Object execute(String action, Object[] args) throws Throwable {
        Object result = null;
        if (action != null && action.equals("off")) {
            off();
        }
        if (action != null && action.equals("on")) {
            on();
        }
        return result;
    }
}
