package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat;

import com.pauware.pauware_engine._Core.*;
import com.pauware.pauware_engine._Exception.Statechart_exception;

/**
 * Java ME only
 */
import com.pauware.pauware_engine._Java_ME.*;

import com.FranckBarbier.Java._Programmable_thermostat_MODEL._Relays.*;
import com.FranckBarbier.Java._Programmable_thermostat_MODEL._Switchs.*;
import com.FranckBarbier.Java._Temperature.*;
import com.seekdasky.websocket.Client;
import com.seekdasky.websocket.Event;
import com.seekdasky.websocket.Token;
import com.seekdasky.websocket.WebSocketServer;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.json.Json.createObjectBuilder;
import javax.json.JsonObjectBuilder;

final class Program {

    Temperature target_temperature;
    /**
     * ** GregorianCalendar time; // -> not supported by Java ME ***
     */
    java.util.Calendar time;
    long difference;
}

public class Programmable_thermostat extends AbstractTimer_monitor implements Programmable_thermostat_input, Fan_switch_output, Run_indicator_output, Season_switch_output, Temperature_sensor_output, Manageable_base, Java_MEExecutor, com.pauware.pauware_engine._Core.AbstractStatechart_monitor_listener, com.seekdasky.websocket.WebSocketListener {

    /**
     * Websocket
     */
    private static WebSocketServer _WebSocketServer = null;
    private static Client[] _client = new Client[10];
    private int _NumberOfClients = 0;
    private int currentID = 0;

    static {
        _WebSocketServer.printCopyrightToConsole();
        _WebSocketServer = com.seekdasky.websocket.WebSocketServer.getInstance();

        _WebSocketServer.start();
    }
    /**
     * UML control flow
     */
    protected Programmable_thermostat_output _user_interface;

    public void add_user_interface(Programmable_thermostat_output user_interface) {
        _user_interface = user_interface;
    }
    /**
     * UML attributes
     */
    public static final int From_Setup_to_Operate_delay = 90; // 90 sec.

    protected Temperature _ambient_temperature;
    protected final float _delta = .1F;
    protected static Temperature _Max;

    public static float Max(byte temperature_unit) {
        switch (temperature_unit) {
            case Temperature.Celsius:
                return _Max.asCelsius();
            case Temperature.Fahrenheit:
                return _Max.asFahrenheit();
            case Temperature.Kelvin:
                return _Max.asKelvin();
            default:
                return _Max.asFahrenheit();
        }
    }
    protected static Temperature _Min;

    public static float Min(byte temperature_unit) {
        switch (temperature_unit) {
            case Temperature.Celsius:
                return _Min.asCelsius();
            case Temperature.Fahrenheit:
                return _Min.asFahrenheit();
            case Temperature.Kelvin:
                return _Min.asKelvin();
            default:
                return _Min.asFahrenheit();
        }
    }
    public static final float Target_temperature_precision = (float) 5E-1;
    protected Temperature _target_temperature;
    public static final byte Default_temperature_mode = Temperature.Celsius;
    protected byte _temperature_mode = Default_temperature_mode;
    /**
     * ** GregorianCalendar _time; // -> not supported by Java ME ***
     */
    protected java.util.Calendar _time = java.util.Calendar.getInstance();
    /**
     * UML associations
     */
    protected Program[] _program = new Program[8];

    protected Air_conditioner_relay _air_conditioner_relay;
    protected Fan_relay _fan_relay;
    protected Furnace_relay _furnace_relay;
    protected Fan_switch _fan_switch;
    protected Season_switch _season_switch;
    protected Run_indicator _run_indicator;
    /**
     * UML statechart
     */
    protected AbstractStatechart _Run;
    protected AbstractStatechart _Hold;
    protected AbstractStatechart _Current_date_and_time_displaying;
    protected AbstractStatechart _Ambient_temperature_displaying;
    protected AbstractStatechart _Target_temperature_displaying;
    protected AbstractStatechart _Operate;
    protected AbstractStatechart _Set_current_minute;
    protected AbstractStatechart _Set_current_hour;
    protected AbstractStatechart _Set_time;
    protected AbstractStatechart _Set_current_day;
    protected AbstractStatechart _Set_period;
    protected AbstractStatechart _Set_program_time;
    protected AbstractStatechart _Set_program_target_temperature;
    protected AbstractStatechart _Set_program;
    protected AbstractStatechart _Current_date_and_time_refreshing;
    protected AbstractStatechart _Program_target_temperature_refreshing;
    protected AbstractStatechart _Period_and_program_time_refreshing;
    protected AbstractStatechart _Program_refreshing;
    protected AbstractStatechart _Setup;
    protected AbstractStatechart _Control;
    protected AbstractStatechart_monitor _Programmable_thermostat_state_machine;
    /**
     * UML statechart variables
     */
    protected int _alternately = 0;
    protected int _no_input = 0;
    protected int _period = 1;

    /**
     * UML creation
     */
    static {
        try {
            _Max = new Temperature(28.F, Temperature.Celsius);
            _Min = new Temperature(14.F, Temperature.Celsius);
        } catch (Exception e) {
            System.exit(1);
        }
    }

    //François Thoraval  - Websocket
    public JsonObjectBuilder createMessage(String action) {
        JsonObjectBuilder json = createObjectBuilder();
        json.add("action", action);
        return json;
    }

    public JsonObjectBuilder createMessage(String action, String[] message) {
        int i = 0;
        JsonObjectBuilder json = createObjectBuilder();
        json.add("action", action);
        while (i < message.length && message[i] != null) {
            String field = "value" + i;
            json.add(field, message[i]);
            i++;
        }
        return json;

    }
    
    private Client[] sortClients(Client[] client) throws IOException {
        Client[] sort = new Client[10];
        int j = 0;
        int newNumberOfClients = 0;
        boolean isEmpty = true;
        //Removing all null clients
        for (Client c : client) {
            if (c != null) {
                isEmpty = false;
                if (!isEmpty) {
                    sort[j] = c;
                    j++;
                    newNumberOfClients++;
                }
            }

        }
        printClients(client);
        printClients(sort);
        if (!isEmpty) {
            _NumberOfClients = newNumberOfClients;
            j = 0;
            //sending new ids to all clients still connected
            for (Client c : sort) {
                String[] message = {String.valueOf(j)};
                if (c != null) {
                    _WebSocketServer.sendToken(c, Token.buildFromJSON(createMessage("set_id", message).build()));
                }
                j++;
            }

        }
        return sort;
    }
    
    //François Thoraval  - Websocket /end
    
    private void init_structure(Temperature temperature) throws Statechart_exception {
        try {
            _ambient_temperature = (Temperature) temperature.clone();
            _target_temperature = new Temperature((Max(Temperature.Celsius) + Min(Temperature.Celsius)) / 2.F, Temperature.Celsius, Target_temperature_precision);
        } catch (Exception e) {
            System.exit(1);
        }
        _air_conditioner_relay = new Air_conditioner_relay(temperature);
        _fan_relay = new Fan_relay();
        _fan_switch = new Fan_switch(this);
        _furnace_relay = new Furnace_relay(temperature);
        _run_indicator = new Run_indicator(this);
        _season_switch = new Season_switch(this);
        for (int i = 0; i < _program.length; i++) {
            _program[i] = new Program();
            _program[i].time = java.util.Calendar.getInstance();
            try {
                _program[i].target_temperature = new Temperature((Max(Temperature.Celsius) + Min(Temperature.Celsius)) / 2.F, Temperature.Celsius, Target_temperature_precision);
            } catch (Exception e) {
                System.exit(1);
            }
        }
    }

    private void init_behavior() throws Statechart_exception {
        _Run = new Java_MEStatechart("Run");
        _Hold = new Java_MEStatechart("Hold");
        _Hold.inputState();
        _Current_date_and_time_displaying = (new Java_MEStatechart("Current date and time displaying")).set_entryAction(this, "display_current_date_and_time");
        _Current_date_and_time_displaying.inputState();
        _Ambient_temperature_displaying = (new Java_MEStatechart("Ambient temperature displaying")).set_entryAction(this, "display_ambient_temperature");
        _Target_temperature_displaying = (new Java_MEStatechart("Target temperature displaying")).set_entryAction(this, "display_target_temperature");
        _Operate = (((_Run.xor(_Hold)).and(_Current_date_and_time_displaying.xor(_Ambient_temperature_displaying)).and(_Target_temperature_displaying)).name("Operate"));
        _Operate.inputState();
        _Set_current_minute = new Java_MEStatechart("Set current minute");
        _Set_current_hour = new Java_MEStatechart("Set current hour");
        _Set_time = (_Set_current_minute.xor(_Set_current_hour)).name("Set time");
        _Set_current_day = new Java_MEStatechart("Set current day");
        _Set_period = new Java_MEStatechart("Set period");
        _Set_program_time = new Java_MEStatechart("Set program time");
        _Set_program_target_temperature = new Java_MEStatechart("Set program target temperature");
        _Set_program = (_Set_period.and(_Set_program_time).and(_Set_program_target_temperature)).name("Set program");
        _Current_date_and_time_refreshing = (new Java_MEStatechart("Current date and time refreshing")).set_entryAction(this, "display_current_date_and_time");
        _Program_target_temperature_refreshing = (new Java_MEStatechart("Program target temperature refreshing")).set_entryAction(this, "display_program_target_temperature");
        _Period_and_program_time_refreshing = (new Java_MEStatechart("Period and program time refreshing")).set_entryAction(this, "display_period_and_program_time");
        _Program_refreshing = (_Program_target_temperature_refreshing.and(_Period_and_program_time_refreshing)).name("Program refreshing");
        _Setup = ((_Set_time.xor(_Set_current_day).xor(_Set_program).and(_Current_date_and_time_refreshing.xor(_Program_refreshing))).name("Setup"));
        _Control = new Java_MEStatechart("Control");
        _Programmable_thermostat_state_machine = new Java_MEStatechart_monitor((_Operate.xor(_Setup)).and(_Control), "Programmable thermostat", AbstractStatechart_monitor.Show_on_system_out, this);
        _Programmable_thermostat_state_machine.set_entryAction(this, "to_be_set", new Object[]{Integer.valueOf(1000)});
        _Programmable_thermostat_state_machine.set_exitAction(this, "to_be_killed");
    }

    public void start() throws Statechart_exception {
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_greaterThan_target_temperature", _air_conditioner_relay, "run");
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_greaterThan_target_temperature", _fan_relay, "run");
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_greaterThan_target_temperature", _run_indicator, "on");
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_lessThan_target_temperature_minus_delta", _air_conditioner_relay, "stop");
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_lessThan_target_temperature_minus_delta", _run_indicator, "off");
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_lessThan_target_temperature", _furnace_relay, "run");
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_lessThan_target_temperature", _fan_relay, "run");
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_lessThan_target_temperature", _run_indicator, "on");
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_greaterThan_target_temperature_plus_delta", _furnace_relay, "stop");
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_greaterThan_target_temperature_plus_delta", _run_indicator, "off");
        _Programmable_thermostat_state_machine.fires("fan_switch_turned_on", _Control, _Control, _fan_relay, "run");
        _Programmable_thermostat_state_machine.fires("f_c", _Ambient_temperature_displaying, _Ambient_temperature_displaying);
        _Programmable_thermostat_state_machine.fires("f_c", _Target_temperature_displaying, _Target_temperature_displaying, true, this, "switch_mode");
        _Programmable_thermostat_state_machine.fires("f_c", _Program_target_temperature_refreshing, _Program_target_temperature_refreshing, true, this, "switch_mode");
        _Programmable_thermostat_state_machine.fires("hold_temp", _Run, _Hold);
        _Programmable_thermostat_state_machine.fires("run_program", _Programmable_thermostat_state_machine, _Current_date_and_time_displaying);
        _Programmable_thermostat_state_machine.fires("run_program", _Programmable_thermostat_state_machine, _Run);
        _Programmable_thermostat_state_machine.fires("season_switch_turned_off", _Control, _Control, true, _air_conditioner_relay, "stop");
        _Programmable_thermostat_state_machine.fires("season_switch_turned_off", _Control, _Control, true, _furnace_relay, "stop");
        _Programmable_thermostat_state_machine.fires("season_switch_turned_off", _Control, _Control, true, _run_indicator, "off");
        _Programmable_thermostat_state_machine.fires("set_clock", _Programmable_thermostat_state_machine, _Current_date_and_time_refreshing);
        // Each time a setup-based key is pressed, one resets the '_no_input' variable to zero so that, after 90 sec., one goes back to 'Operate':
        _Programmable_thermostat_state_machine.fires("set_clock", _Programmable_thermostat_state_machine, _Set_current_minute, true, this, "set_no_input", new Object[]{Integer.valueOf(0)});
        _Programmable_thermostat_state_machine.fires("set_clock", _Set_current_minute, _Set_current_hour, true, this, "set_no_input", new Object[]{Integer.valueOf(0)});
        _Programmable_thermostat_state_machine.fires("set_day", _Programmable_thermostat_state_machine, _Set_current_day);
        // Each time a setup-based key is pressed, one resets the '_no_input' variable to zero so that, after 90 sec., one goes back to 'Operate':
        _Programmable_thermostat_state_machine.fires("set_day", _Programmable_thermostat_state_machine, _Current_date_and_time_refreshing, true, this, "set_no_input", new Object[]{Integer.valueOf(0)});
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_greaterThan_target_temperature", _air_conditioner_relay, "run", null);
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_greaterThan_target_temperature", _fan_relay, "run", null);
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_greaterThan_target_temperature", _run_indicator, "on", null);
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_lessThan_target_temperature_minus_delta", _air_conditioner_relay, "stop");
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_lessThan_target_temperature_minus_delta", _run_indicator, "off");
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_lessThan_target_temperature", _furnace_relay, "run");
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_lessThan_target_temperature", _fan_relay, "run");
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_lessThan_target_temperature", _run_indicator, "on");
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_greaterThan_target_temperature_plus_delta", _furnace_relay, "stop");
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_greaterThan_target_temperature_plus_delta", _run_indicator, "off", null);
        // Each time a setup-based key is pressed, one resets the '_no_input' variable to zero so that, after 90 sec., one goes back to 'Operate':
        _Programmable_thermostat_state_machine.fires("temp_down", _Program_target_temperature_refreshing, _Program_target_temperature_refreshing, true, this, "set_no_input", new Object[]{Integer.valueOf(0)});
        _Programmable_thermostat_state_machine.fires("temp_down", _Target_temperature_displaying, _Target_temperature_displaying, this, "target_temperature_greaterThan_Min", null, this, "target_temperature_changed", null, AbstractStatechart.Reentrance);
        // Each time a setup-based key is pressed, one resets the '_no_input' variable to zero so that, after 90 sec., one goes back to 'Operate':
        _Programmable_thermostat_state_machine.fires("temp_up", _Program_target_temperature_refreshing, _Program_target_temperature_refreshing, true, this, "set_no_input", new Object[]{Integer.valueOf(0)});
        _Programmable_thermostat_state_machine.fires("temp_up", _Target_temperature_displaying, _Target_temperature_displaying, this, "target_temperature_lessThan_Max", null, this, "target_temperature_changed", null, AbstractStatechart.Reentrance);
        _Programmable_thermostat_state_machine.fires("time_backward", _Set_current_minute, _Set_current_minute, true, this, "set_time", new Object[]{Integer.valueOf(-60)});
        _Programmable_thermostat_state_machine.fires("time_backward", _Set_current_hour, _Set_current_hour, true, this, "set_time", new Object[]{Integer.valueOf(-3600)});
        _Programmable_thermostat_state_machine.fires("time_backward", _Set_current_day, _Set_current_day, true, this, "set_time", new Object[]{Integer.valueOf(-86400)});
        _Programmable_thermostat_state_machine.fires("time_backward", _Set_program_time, _Set_program_time, true, this, "set_program_time", new Object[]{Integer.valueOf(-15)});
        _Programmable_thermostat_state_machine.fires("time_backward", _Current_date_and_time_refreshing, _Current_date_and_time_refreshing);
        // Each time a setup-based key is pressed, one resets the '_no_input' variable to zero so that, after 90 sec., one goes back to 'Operate':
        _Programmable_thermostat_state_machine.fires("time_backward", _Period_and_program_time_refreshing, _Period_and_program_time_refreshing, true, this, "set_no_input", new Object[]{Integer.valueOf(0)});
        _Programmable_thermostat_state_machine.fires("time_forward", _Set_current_minute, _Set_current_minute, true, this, "set_time", new Object[]{Integer.valueOf(+60)});
        _Programmable_thermostat_state_machine.fires("time_forward", _Set_current_hour, _Set_current_hour, true, this, "set_time", new Object[]{Integer.valueOf(+3600)});
        _Programmable_thermostat_state_machine.fires("time_forward", _Set_current_day, _Set_current_day, true, this, "set_time", new Object[]{Integer.valueOf(+86400)});
        _Programmable_thermostat_state_machine.fires("time_forward", _Set_program_time, _Set_program_time, true, this, "set_program_time", new Object[]{Integer.valueOf(+15)});
        _Programmable_thermostat_state_machine.fires("time_forward", _Current_date_and_time_refreshing, _Current_date_and_time_refreshing);
        // Each time a setup-based key is pressed, one resets the '_no_input' variable to zero so that, after 90 sec., one goes back to 'Operate':
        _Programmable_thermostat_state_machine.fires("time_forward", _Period_and_program_time_refreshing, _Period_and_program_time_refreshing, true, this, "set_no_input", new Object[]{Integer.valueOf(0)});
// Alternative:        _Programmable_thermostat_state_machine.fires("time_out", _Run, _Run, !weekend(), this, "set_target_temperature",  new Object[]{Integer.valueOf(1),Integer.valueOf(4)});
        _Programmable_thermostat_state_machine.fires("time_out", _Run, _Run, this, "not_weekend", this, "set_target_temperature", new Object[]{Integer.valueOf(1), Integer.valueOf(4)});
        _Programmable_thermostat_state_machine.fires("time_out", _Run, _Run, this, "not_weekend", this, "display_target_temperature");
// Alternative:       _Programmable_thermostat_state_machine.fires("time_out", _Run, _Run, weekend(), this, "set_target_temperature",  new Object[]{Integer.valueOf(5),Integer.valueOf(8)});
        _Programmable_thermostat_state_machine.fires("time_out", _Run, _Run, this, "weekend", this, "set_target_temperature", new Object[]{Integer.valueOf(5), Integer.valueOf(8)});
        _Programmable_thermostat_state_machine.fires("time_out", _Run, _Run, this, "weekend", this, "display_target_temperature");
        _Programmable_thermostat_state_machine.fires("time_out", _Ambient_temperature_displaying, _Current_date_and_time_displaying, this, "alternately_equal_to_two", this, "set_alternately", new Object[]{Integer.valueOf(0)});
        _Programmable_thermostat_state_machine.fires("time_out", _Ambient_temperature_displaying, _Current_date_and_time_displaying, this, "alternately_equal_to_two", this, "set_time", new Object[]{Integer.valueOf(+1)});
        _Programmable_thermostat_state_machine.fires("time_out", _Current_date_and_time_displaying, _Ambient_temperature_displaying, this, "alternately_equal_to_two", this, "set_alternately", new Object[]{Integer.valueOf(0)});
        _Programmable_thermostat_state_machine.fires("time_out", _Current_date_and_time_displaying, _Ambient_temperature_displaying, this, "alternately_equal_to_two", this, "set_time", new Object[]{Integer.valueOf(+1)});
        _Programmable_thermostat_state_machine.fires("time_out", _Ambient_temperature_displaying, _Ambient_temperature_displaying, this, "alternately_not_equal_to_two", this, "set_time", new Object[]{Integer.valueOf(+1)});
        _Programmable_thermostat_state_machine.fires("time_out", _Current_date_and_time_displaying, _Current_date_and_time_displaying, this, "alternately_not_equal_to_two", this, "set_time", new Object[]{Integer.valueOf(+1)});
        _Programmable_thermostat_state_machine.fires("view_program", _Programmable_thermostat_state_machine, _Set_program, true, this, "set_period", new Object[]{Integer.valueOf(1)});
        // Each time a setup-based key is pressed, one resets the '_no_input' variable to zero so that, after 90 sec., one goes back to 'Operate':
        _Programmable_thermostat_state_machine.fires("view_program", _Programmable_thermostat_state_machine, _Program_refreshing, true, this, "set_no_input", new Object[]{Integer.valueOf(0)});
        _air_conditioner_relay.start();
        _fan_relay.start();
        _furnace_relay.start();
        _fan_switch.start();
        _season_switch.start();
        _run_indicator.start();
        _Programmable_thermostat_state_machine.start();
    }

    public void stop() throws Statechart_exception {
        _Programmable_thermostat_state_machine.stop();
    }

    public Programmable_thermostat(Temperature temperature) throws Statechart_exception {
        init_structure(temperature);
        init_behavior();
    }

    /**
     * UML events
     */
    synchronized public void ambient_temperature_changed(Temperature temperature) throws Statechart_exception {
        /**
         * Post-condition(s)
         */
        _ambient_temperature = (Temperature) temperature.clone();
        /**
         * End of post-condition(s)
         */
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_lessThan_target_temperature_minus_delta", _fan_relay, "stop", new Object[]{Boolean.valueOf(_fan_switch.in("Is auto"))});
        _Programmable_thermostat_state_machine.fires("ambient_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_greaterThan_target_temperature_plus_delta", _fan_relay, "stop", new Object[]{Boolean.valueOf(_fan_switch.in("Is auto"))});
        _Programmable_thermostat_state_machine.run_to_completion("ambient_temperature_changed");

        //FrançoisThoraval
        //SENDING TO WEBSOCKET SO WE CAN REFRESH HTML PAGE
        try {
            if (_client != null) {
                String[] message = new String[10];
                String temp;
                if (this._temperature_mode == 0) {
                    temp = this._ambient_temperature.asStringCelsius(1);
                } else {
                    temp = this._ambient_temperature.asStringFahrenheit(1);
                }

                //We have to remove the ° character from the temp otherwise it causes a Json input error in JS, thus close the socket connection.
                message[0] = temp.substring(0, temp.length() - 2);
                message[1] = temp.substring(temp.length() - 1, temp.length());
                System.out.println("\n*******TEMP*******\n" + temp);
                System.out.println("clients: " + _NumberOfClients);

                this.sortClients(_client);
                for (int i = 0; i < _NumberOfClients; i++) {
                    if (_client[i] != null) {
                        _WebSocketServer.sendToken(_client[i], Token.buildFromJSON(createMessage("display_ambient_temperature", message).build()));
                    }
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            this.sortClients(_client);
            if (_client != null) {
                String[] message = new String[10];
                String temp;
                if (this._temperature_mode == 0) {
                    temp = this._target_temperature.asStringCelsius(1);
                } else {
                    temp = this._target_temperature.asStringFahrenheit(1);
                }

                //We have to remove the ° character from the temp otherwise it causes a Json input error in JS, thus close the socket connection.
                message[0] = temp.substring(0, temp.length() - 2);
                message[1] = temp.substring(temp.length() - 1, temp.length());
                System.out.println("\n*******TEMP*******\n" + temp);

                for (int i = 0; i < _NumberOfClients; i++) {
                    if (_client[i] != null) {
                        _WebSocketServer.sendToken(_client[i], Token.buildFromJSON(createMessage("display_target_temperature", message).build()));
                    }
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void fan_switch_turned_on() throws Statechart_exception {
        _Programmable_thermostat_state_machine.run_to_completion("fan_switch_turned_on");
    }

    public void f_c() throws Statechart_exception {
        _Programmable_thermostat_state_machine.run_to_completion("f_c");
    }

    public void hold_temp() throws Statechart_exception {
        _Programmable_thermostat_state_machine.run_to_completion("hold_temp");
    }

    public void run_program() throws Statechart_exception {
        _Programmable_thermostat_state_machine.run_to_completion("run_program");
    }

    synchronized public void season_switch_turned_off() throws Statechart_exception {
        _Programmable_thermostat_state_machine.fires("season_switch_turned_off", _Control, _Control, true, _fan_relay, "stop", new Object[]{Boolean.valueOf(_fan_switch.in("Is auto"))});
        _Programmable_thermostat_state_machine.run_to_completion("season_switch_turned_off");
    }

    public void set_clock() throws Statechart_exception {
        _Programmable_thermostat_state_machine.run_to_completion("set_clock");
    }

    public void set_day() throws Statechart_exception {
        _Programmable_thermostat_state_machine.run_to_completion("set_day");
    }

    void target_temperature_changed() throws Statechart_exception { // No use of 'synchronized' because this event is sent and consumed internally
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_cool_and_ambient_temperature_lessThan_target_temperature_minus_delta", _fan_relay, "stop", new Object[]{Boolean.valueOf(_fan_switch.in("Is auto"))});
        _Programmable_thermostat_state_machine.fires("target_temperature_changed", _Control, _Control, this, "season_switch_in_Is_heat_and_ambient_temperature_greaterThan_target_temperature_plus_delta", _fan_relay, "stop", new Object[]{Boolean.valueOf(_fan_switch.in("Is auto"))});
        _Programmable_thermostat_state_machine.run_to_completion("target_temperature_changed");
    }

    synchronized public void temp_down() throws Statechart_exception {
        /* Java ME only */ _Programmable_thermostat_state_machine.fires("temp_down", _Run, _Run, this, "target_temperature_greaterThan_Min", null, _target_temperature, "com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostatActionExecutor.decrement");
        /* Java ME only */ _Programmable_thermostat_state_machine.fires("temp_down", _Hold, _Hold, this, "target_temperature_greaterThan_Min", null, _target_temperature, "com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostatActionExecutor.decrement");
        /* Java ME only */ _Programmable_thermostat_state_machine.fires("temp_down", _Set_program_target_temperature, _Set_program_target_temperature, this, "program_period_target_temperature_greaterThan_Min", this, "program_at_period_target_temperature_decrement", new Object[]{Integer.valueOf(_period - 1)});
        /**
         * Java SE
         */
//        _Programmable_thermostat_state_machine.fires("temp_down", _Set_program_target_temperature, _Set_program_target_temperature, this, "program_period_target_temperature_greaterThan_Min", _program[_period - 1].target_temperature, "decrement");
        /**
         * End of Java SE
         */
        _Programmable_thermostat_state_machine.run_to_completion("temp_down");
    }

    synchronized public void temp_up() throws Statechart_exception {
        /* Java ME only */ _Programmable_thermostat_state_machine.fires("temp_up", _Run, _Run, this, "target_temperature_lessThan_Max", null, _target_temperature, "com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostatActionExecutor.increment");
        /* Java ME only */ _Programmable_thermostat_state_machine.fires("temp_up", _Hold, _Hold, this, "target_temperature_lessThan_Max", null, _target_temperature, "com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostatActionExecutor.increment");
        /* Java ME only */ _Programmable_thermostat_state_machine.fires("temp_up", _Set_program_target_temperature, _Set_program_target_temperature, this, "program_period_target_temperature_lessThan_Max", this, "program_at_period_target_temperature_increment", new Object[]{Integer.valueOf(_period - 1)});
        /**
         * Java SE
         */
//        _Programmable_thermostat_state_machine.fires("temp_up", _Set_program_target_temperature, _Set_program_target_temperature, this, "program_period_target_temperature_lessThan_Max", _program[_period - 1].target_temperature, "increment");
        /**
         * End of Java SE
         */
        _Programmable_thermostat_state_machine.run_to_completion("temp_up");
    }

    public void time_backward() throws Statechart_exception {
        _Programmable_thermostat_state_machine.run_to_completion("time_backward");
    }

    public void time_forward() throws Statechart_exception {
        _Programmable_thermostat_state_machine.run_to_completion("time_forward");
    }

    synchronized public void time_out(long delay, AbstractStatechart context) throws Statechart_exception {
        _Programmable_thermostat_state_machine.fires("time_out", _Ambient_temperature_displaying, _Ambient_temperature_displaying, this, "alternately_not_equal_to_two", this, "set_alternately", new Object[]{Integer.valueOf(_alternately + 1)});
        _Programmable_thermostat_state_machine.fires("time_out", _Current_date_and_time_displaying, _Current_date_and_time_displaying, this, "alternately_not_equal_to_two", this, "set_alternately", new Object[]{Integer.valueOf(_alternately + 1)});
        _Programmable_thermostat_state_machine.fires("time_out", _Setup, _Operate, _no_input >= From_Setup_to_Operate_delay);
        _Setup.allowedEvent("time_out", this, "no_input_less_than_ninety", this, "set_no_input", new Object[]{Integer.valueOf(_no_input + 1)});
        _Programmable_thermostat_state_machine.run_to_completion("time_out");
    }

    public void time_out_error(Statechart_exception se) throws Statechart_exception {
        se.printStackTrace();
        // possible fault recovery here...
    }

    synchronized public void view_program() throws Statechart_exception {
        _Programmable_thermostat_state_machine.fires("view_program", _Set_period, _Set_period, _period == 8, this, "set_period", new Object[]{Integer.valueOf(1)});
        _Programmable_thermostat_state_machine.fires("view_program", _Set_period, _Set_period, _period != 8, this, "set_period", new Object[]{Integer.valueOf(_period + 1)});
        _Programmable_thermostat_state_machine.run_to_completion("view_program");

        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("PROGRAM *****");
        //System.out.println(this);
        System.out.println("difference" + this._program[0].difference);
        System.out.println("difference" + this._program[0].target_temperature.asStringCelsius(1));
        System.out.println("difference" + this._program[0].time.toString());
//        try {
//            if (_client != null) {
//                String[] message = new String[10];
//                String program = this._program[0].target_temperature;
//
//                message[0] = temp.substring(0, temp.length() - 2);
//                message[1] = temp.substring(temp.length() - 1, temp.length());
//                System.out.println("\n*******TEMP*******\n" + temp);
//                if (_client != null) {
//                    _WebSocketServer.sendToken(_client, Token.buildFromJSON(createMessage("display_target_temperature", message).build()));
//
//                }
//
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    /**
     * UML actions
     */
    void display_ambient_temperature() {
        if (_user_interface != null) {
            switch (_temperature_mode) {
                case Temperature.Celsius:
                    _user_interface.display_ambient_temperature(_ambient_temperature.asStringCelsius(1), _ambient_temperature);
                    break;
                case Temperature.Fahrenheit:
                    _user_interface.display_ambient_temperature(_ambient_temperature.asStringFahrenheit(1), _ambient_temperature);
                    break;
                default:
                    _user_interface.display_ambient_temperature(String.valueOf(null), _ambient_temperature);
            }
        }
    }

    void display_current_date_and_time() {
        if (_user_interface != null) {
            _user_interface.display_current_date_and_time(_time.getTime().toString());
        }
    }

    void display_period_and_program_time() {
        /**
         * Java SE
         */
//        if (_user_interface != null) {
//            _user_interface.display_period_and_program_time(String.valueOf(_period) + " - " + _program[_period - 1].time.getTime());
//        }
        /**
         * End of Java SE
         */
        if (_user_interface != null) {
            _user_interface.display_period_and_program_time(String.valueOf(_period) + " - " + _program[_period - 1].time.get(java.util.Calendar.HOUR_OF_DAY) + ":" + _program[_period - 1].time.get(java.util.Calendar.MINUTE) + ":" + _program[_period - 1].time.get(java.util.Calendar.SECOND));
        }
    }

    void display_program_target_temperature() {
        if (_user_interface != null) {
            _user_interface.display_program_target_temperature(_program[_period - 1].target_temperature, _temperature_mode);
        }
    }

    void display_target_temperature() {
        if (_user_interface != null) {
            _user_interface.display_target_temperature(_target_temperature, _temperature_mode);
        }
    }

    void program_at_period_target_temperature_decrement(int index) {
        _program[index].target_temperature.decrement();
    }

    void program_at_period_target_temperature_increment(int index) {
        _program[index].target_temperature.increment();
    }

    void set_alternately(Integer i) {
        _alternately = i.intValue();
    }

    void set_no_input(Integer i) {
        _no_input = i.intValue();
    }

    void set_period(Integer i) {
        _period = i.intValue();
    }

    void set_program_time(Integer step) {
        java.util.Date date = _program[_period - 1].time.getTime();
        date.setTime(date.getTime() + step.intValue() * 1000);
        _program[_period - 1].time.setTime(date);
    }

    private int _index_of_last_target_temperature = -1;

    void set_target_temperature(Integer min, Integer max) {
        long time = _time.get(java.util.Calendar.HOUR_OF_DAY) * 3600L + _time.get(java.util.Calendar.MINUTE) * 60L + _time.get(java.util.Calendar.SECOND);
        for (int i = min.intValue() - 1; i < max.intValue(); i++) {
            _program[i].difference = time - (_program[i].time.get(java.util.Calendar.HOUR_OF_DAY) * 3600L + _program[i].time.get(java.util.Calendar.MINUTE) * 60L + _program[i].time.get(java.util.Calendar.SECOND));
        }
        long difference = _program[min.intValue() - 1].difference;
        int index = min.intValue() - 1;
        for (int i = min.intValue(); i < max.intValue(); i++) {
            if (_program[i].difference >= 0 && _program[i].difference <= difference) {
                difference = _program[i].difference;
                index = i;
            }
        }
        if (index != _index_of_last_target_temperature) {
            try {
                _target_temperature = (Temperature) _program[index].target_temperature.clone();
                _index_of_last_target_temperature = index;
            } /**
             * Java ME problem here...
             */
            // catch(CloneNotSupportedException cnse) {/* Cannot occur whether 'class Temperature implements Cloneable' -> Java ME */}
            catch (Exception e) {
            }
        }
    }

    void set_time(Integer step) {
        java.util.Date date = _time.getTime();
        date.setTime(date.getTime() + step.intValue() * 1000);
        _time.setTime(date);
        try {
            this.sortClients(_client);
            if (_client != null) {
                String[] message = new String[10];
                String value;
                value = date.toString();

                message[0] = value;
                for (int i = 0; i < _NumberOfClients; i++) {
                    if (_client[i] != null) {
                        _WebSocketServer.sendToken(_client[i], Token.buildFromJSON(createMessage("display_current_date_and_time", message).build()));

                    }
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void switch_mode() {
        switch (_temperature_mode) {
            case Temperature.Celsius:
                _temperature_mode = Temperature.Fahrenheit;
                break;
            case Temperature.Fahrenheit:
                _temperature_mode = Temperature.Celsius;
                break;
            default:
                _temperature_mode = Temperature.Fahrenheit;
        }
    }

    /**
     * UML guards
     */
    boolean alternately_equal_to_two() {
        return _alternately == 2;
    }

    boolean alternately_not_equal_to_two() {
        return _alternately != 2;
    }

    boolean no_input_less_than_ninety() {
        return _no_input < From_Setup_to_Operate_delay;
    }

    boolean program_period_target_temperature_greaterThan_Min() {
        return _program[_period - 1].target_temperature.greaterThan(_Min);
    }

    boolean program_period_target_temperature_lessThan_Max() {
        return _program[_period - 1].target_temperature.lessThan(_Max);
    }

    boolean season_switch_in_Is_cool_and_ambient_temperature_greaterThan_target_temperature() {
        return _season_switch.in("Is cool") && _ambient_temperature.greaterThan(_target_temperature);
    }

    boolean season_switch_in_Is_cool_and_ambient_temperature_lessThan_target_temperature_minus_delta() {
        return _season_switch.in("Is cool") && _ambient_temperature.asCelsius() < (_target_temperature.asCelsius() - _delta);
    }

    boolean season_switch_in_Is_heat_and_ambient_temperature_lessThan_target_temperature() {
        return _season_switch.in("Is heat") && _ambient_temperature.lessThan(_target_temperature);
    }

    boolean season_switch_in_Is_heat_and_ambient_temperature_greaterThan_target_temperature_plus_delta() {
        return _season_switch.in("Is heat") && _ambient_temperature.asCelsius() > (_target_temperature.asCelsius() + _delta);
    }

    boolean target_temperature_greaterThan_Min() {
        return _target_temperature.greaterThan(_Min);
    }

    boolean target_temperature_lessThan_Max() {
        return _target_temperature.lessThan(_Max);
    }

    boolean not_weekend() {
        return !weekend();
    }

    boolean weekend() {
        return _time.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SATURDAY || _time.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY;
    }

    /**
     * Implementation
     */
    public void fan_switch_auto() throws Statechart_exception {
        _fan_switch.auto();
        try {
            this.sortClients(_client);
            for (int i = 0; i < _NumberOfClients; i++) {
                if (_client[i] != null) {
                    _WebSocketServer.sendToken(_client[i], Token.buildFromJSON(createMessage("auto").build()));
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fan_switch_on() throws Statechart_exception {
        _fan_switch.on();
        try {
            this.sortClients(_client);
            for (int i = 0; i < _NumberOfClients; i++) {
                if (_client[i] != null) {
                    _WebSocketServer.sendToken(_client[i], Token.buildFromJSON(createMessage("on").build()));
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void season_switch_cool() throws Statechart_exception {
        _season_switch.cool();
        try {
            this.sortClients(_client);
            for (int i = 0; i < _NumberOfClients; i++) {
                if (_client[i] != null) {
                    _WebSocketServer.sendToken(_client[i], Token.buildFromJSON(createMessage("cool").build()));
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void season_switch_heat() throws Statechart_exception {
        _season_switch.heat();
        try {
            this.sortClients(_client);
            for (int i = 0; i < _NumberOfClients; i++) {
                if (_client[i] != null) {
                    _WebSocketServer.sendToken(_client[i], Token.buildFromJSON(createMessage("heat").build()));
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void season_switch_off() throws Statechart_exception {
        _season_switch.off();
        try {
            this.sortClients(_client);
            for (int i = 0; i < _NumberOfClients; i++) {
                if (_client[i] != null) {
                    _WebSocketServer.sendToken(_client[i], Token.buildFromJSON(createMessage("off").build()));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * JavaBeans component model not supported by Java ME:
     */
    public void propertyChange(final /*java.beans.PropertyChangeEvent*/ String event) {
        // if(_user_interface != null && event.getPropertyName().equals(Run_indicator.Status)) _user_interface.update_run_indicator_status((String)event.getNewValue());
        if (_user_interface != null) {
            _user_interface.update_run_indicator_status(event);
        }
    }

    /**
     * Java ME only, Java_MEExecutor implementation
     */
    public Object execute(String action, Object[] args) throws Throwable {
        /**
         * UML actions
         */
        Object result = null;
        if (action != null && action.equals("display_ambient_temperature")) {
            display_ambient_temperature();
        }
        if (action != null && action.equals("display_current_date_and_time")) {
            display_current_date_and_time();
        }
        if (action != null && action.equals("display_period_and_program_time")) {
            display_period_and_program_time();
        }
        if (action != null && action.equals("display_program_target_temperature")) {
            display_program_target_temperature();
        }
        if (action != null && action.equals("display_target_temperature")) {
            display_target_temperature();
        }

        if (action != null && action.equals("program_at_period_target_temperature_decrement")) {
            program_at_period_target_temperature_decrement((Integer) args[0]);
        }

        if (action != null && action.equals("program_at_period_target_temperature_increment")) {
            program_at_period_target_temperature_increment((Integer) args[0]);
        }

        if (action != null && action.equals("set_alternately")) {
            set_alternately((Integer) args[0]);
        }
        if (action != null && action.equals("set_no_input")) {
            set_no_input((Integer) args[0]);
        }
        if (action != null && action.equals("set_period")) {
            set_period((Integer) args[0]);
        }
        if (action != null && action.equals("set_program_time")) {
            set_program_time((Integer) args[0]);
        }
        if (action != null && action.equals("set_target_temperature")) {
            set_target_temperature((Integer) args[0], (Integer) args[1]);
        }
        if (action != null && action.equals("set_time")) {
            set_time((Integer) args[0]);
        }
        if (action != null && action.equals("switch_mode")) {
            switch_mode();
        }
        if (action != null && action.equals("to_be_killed")) {
            to_be_killed();
        }
        if (action != null && action.equals("to_be_set")) {
            to_be_set((Integer) args[0]);
        }
        /**
         * UML events
         */
        if (action != null && action.equals("target_temperature_changed")) {
            target_temperature_changed();
        }
        /**
         * UML guards
         */
        if (action != null && action.equals("alternately_equal_to_two")) {
            result = Boolean.valueOf(alternately_equal_to_two());
        }
        if (action != null && action.equals("alternately_not_equal_to_two")) {
            result = Boolean.valueOf(alternately_not_equal_to_two());
        }
        if (action != null && action.equals("no_input_less_than_ninety")) {
            result = Boolean.valueOf(no_input_less_than_ninety());
        }
        if (action != null && action.equals("program_period_target_temperature_greaterThan_Min")) {
            result = Boolean.valueOf(program_period_target_temperature_greaterThan_Min());
        }
        if (action != null && action.equals("program_period_target_temperature_lessThan_Max")) {
            result = Boolean.valueOf(program_period_target_temperature_lessThan_Max());
        }
        if (action != null && action.equals("season_switch_in_Is_cool_and_ambient_temperature_greaterThan_target_temperature")) {
            result = Boolean.valueOf(season_switch_in_Is_cool_and_ambient_temperature_greaterThan_target_temperature());
        }
        if (action != null && action.equals("season_switch_in_Is_cool_and_ambient_temperature_lessThan_target_temperature_minus_delta")) {
            result = Boolean.valueOf(season_switch_in_Is_cool_and_ambient_temperature_lessThan_target_temperature_minus_delta());
        }
        if (action != null && action.equals("season_switch_in_Is_heat_and_ambient_temperature_lessThan_target_temperature")) {
            result = Boolean.valueOf(season_switch_in_Is_heat_and_ambient_temperature_lessThan_target_temperature());
        }
        if (action != null && action.equals("season_switch_in_Is_heat_and_ambient_temperature_greaterThan_target_temperature_plus_delta")) {
            result = Boolean.valueOf(season_switch_in_Is_heat_and_ambient_temperature_greaterThan_target_temperature_plus_delta());
        }
        if (action != null && action.equals("target_temperature_greaterThan_Min")) {
            result = Boolean.valueOf(target_temperature_greaterThan_Min());
        }
        if (action != null && action.equals("target_temperature_lessThan_Max")) {
            result = Boolean.valueOf(target_temperature_lessThan_Max());
        }
        if (action != null && action.equals("not_weekend")) {
            result = Boolean.valueOf(not_weekend());
        }
        if (action != null && action.equals("weekend")) {
            result = Boolean.valueOf(weekend());
        }
        return result;
    }

    /**
     * GUI utilities
     */
    public boolean furnace_relay_is_on() {
        return _furnace_relay.is_on();
    }

    public boolean air_conditioner_relay_is_on() {
        return _air_conditioner_relay.is_on();
    }

    /**
     * Management
     */
    public String async_current_state() {
        return _Programmable_thermostat_state_machine.async_current_state();
    }

    public String current_state() {
        return _Programmable_thermostat_state_machine.current_state();
    }

    public boolean in_state(String name) {
        return _Programmable_thermostat_state_machine.in_state(name);
    }

    public String name() {
        return _Programmable_thermostat_state_machine.name();
    }

    public String verbose() {
        return _Programmable_thermostat_state_machine.verbose();
    }

    @Override
    public void post_construct(AbstractStatechart_monitor as) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void start(String string) throws Exception {
        System.out.println("\n\n\n\n\n\n\nstart\n\n\n\n\n");
        _WebSocketServer.addListener(this);
        for (Client client : _client) {
            client = null;
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run_to_completion(String string, Hashtable hshtbl) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop(String string) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pre_destroy() throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processEvent(Event event, Token token) {
        this.currentID = Integer.parseInt(token.getString("id"));
        System.out.println("\n\n\n\n\n\n\n\nprocessEvent\n\n\n\n\n\n\n\n\n\n\n\n\n" + currentID);
        if (currentID == -1) {
            currentID = _NumberOfClients;
            _client[currentID] = event.getConnector();
            _client[currentID].id = _NumberOfClients;
            String[] message = {String.valueOf(currentID)};
            try {
                if (_client[currentID] != null) {
                    _WebSocketServer.sendToken(_client[currentID], Token.buildFromJSON(createMessage("set_id", message).build()));
                }
            } catch (IOException ex) {
                Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
            }
            _NumberOfClients++;
        }
        //Action from Client
        switch (token.getString("action")) {
            case "hold_temp": {
                try {
                    hold_temp();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "f_c": {
                try {
                    f_c();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "heat": {
                try {
                    season_switch_heat();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "cool": {
                try {
                    season_switch_cool();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "off": {
                try {
                    season_switch_off();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            case "target_temp_change": {
                if (_temperature_mode == 0) { //Celcius
                    try {
                        this._target_temperature = new Temperature(Float.parseFloat(token.getString("temp")), Temperature.Celsius);
                    } catch (Invalid_temperature_exception ex) {
                        Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        this._target_temperature = new Temperature(Float.parseFloat(token.getString("temp")), Temperature.Fahrenheit);
                    } catch (Invalid_temperature_exception ex) {
                        Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            case "time_forward": {
                try {
                    time_forward();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "time_backward": {
                try {
                    time_backward();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "fan_switch_auto": {
                try {
                    fan_switch_auto();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "fan_switch_on": {
                try {
                    fan_switch_on();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "set_day": {
                try {
                    set_day();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "set_clock": {
                try {
                    set_clock();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "view_program": {
                try {
                    view_program();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "run_program": {
                try {
                    run_program();
                } catch (Statechart_exception ex) {
                    Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }

    }

    @Override
    public void processClosed(Event event) {
        System.err.println("\n\n\n process closed ! Lost Connection with Client !\n\n\n");
        System.out.println("currentID: " + currentID);
        try {
            _client[currentID]= null;
            sortClients(_client);
        } catch (IOException ex) {
            Logger.getLogger(Programmable_thermostat.class.getName()).log(Level.SEVERE, null, ex);
        }
        //_WebSocketServer.stop();
        System.out.println("is server alive" + _WebSocketServer.isAlive());
    }

    //DEBUG purpose
    private void printClients(Client[] client) {
        int i = 0;
        System.out.println("*********************** DEBUG *****************************\n\n\n");
        for (Client c : client) {
            i++;
            System.out.println("client[" + i + "]: " + c);
            if(c!=null){
                System.out.println("id: " + c.id);
                System.out.println("currentID: " + currentID);
            }
        }
        System.out.println("***********************************************************\n\n\n");
    }

    
}
