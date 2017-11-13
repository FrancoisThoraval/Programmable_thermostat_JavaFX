package com.FranckBarbier.Java._Programmable_thermostat_MODEL._Simulation;

import java.util.Calendar;

public final class Season {

    private static Calendar _Month = Calendar.getInstance();

    public static boolean Winter() {
        return _Month.get(Calendar.MONTH) == Calendar.DECEMBER || _Month.get(Calendar.MONTH) == Calendar.JANUARY || _Month.get(Calendar.MONTH) == Calendar.FEBRUARY;
    }

    public static boolean Summer() {
        return _Month.get(Calendar.MONTH) == Calendar.JUNE || _Month.get(Calendar.MONTH) == Calendar.JULY || _Month.get(Calendar.MONTH) == Calendar.AUGUST;
    }

    public static boolean Autumn() {
        return _Month.get(Calendar.MONTH) == Calendar.SEPTEMBER || _Month.get(Calendar.MONTH) == Calendar.OCTOBER || _Month.get(Calendar.MONTH) == Calendar.NOVEMBER;
    }

    public static boolean Spring() {
        return _Month.get(Calendar.MONTH) == Calendar.MARCH || _Month.get(Calendar.MONTH) == Calendar.APRIL || _Month.get(Calendar.MONTH) == Calendar.MAY;
    }
}
