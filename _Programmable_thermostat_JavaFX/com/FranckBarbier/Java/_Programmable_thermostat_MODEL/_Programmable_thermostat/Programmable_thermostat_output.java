package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat;

public interface Programmable_thermostat_output {

    void display_ambient_temperature(String ambient_temperature, final com.FranckBarbier.Java._Temperature.Temperature at);

    void display_current_date_and_time(String current_date_and_time);

    void display_period_and_program_time(String period_and_program_time);

    void display_program_target_temperature(com.FranckBarbier.Java._Temperature.Temperature target_temperature, byte temperature_mode);

    void display_target_temperature(com.FranckBarbier.Java._Temperature.Temperature target_temperature, byte temperature_mode);

    public void update_run_indicator_status(String run_indicator_status);
}
