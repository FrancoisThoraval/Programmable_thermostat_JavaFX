package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat;

/**
 * Java ME only
 */
import com.pauware.pauware_engine._Java_ME.*;

public class Programmable_thermostatActionExecutor implements Java_MEActionExecutor {

    /**
     * constructor without arguments is mandatory
     */
    public Object execute(Object object, String action, Object[] args) throws Throwable {
        /**
         * UML actions
         */
        Object result = null;
        if (action != null && action.equals("decrement")) {
            ((com.FranckBarbier.Java._Temperature.Temperature) object).decrement();
        }
        if (action != null && action.equals("increment")) {
            ((com.FranckBarbier.Java._Temperature.Temperature) object).increment();
        }
        return result;
    }
}
