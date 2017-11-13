package com.FranckBarbier.Java._Temperature;

public final class Temperature /* implements Cloneable,java.io.Serializable */ {

    public static final byte Celsius = 0;
    public static final byte Fahrenheit = 1;
    public static final byte Kelvin = 2;
    public static final String[] Literals = new String[3];

    static {
        Literals[Celsius] = new String("°C");
        Literals[Fahrenheit] = new String("°F");
        Literals[Kelvin] = new String("°K");
    }
    public final static float Min = -273.15F; // in Celsius
    private float _value; // in Celsius
    private float _step;

    public Temperature() {
        _value = 0.F;
        _step = 0.00000000001F;
    }

    public Temperature(float value, byte unit) throws Invalid_temperature_exception {
        this();
        switch (unit) {
            case Celsius:
                _value = value;
                break;
            case Fahrenheit:
                _value = (value - 32.F) * 5.F / 9.F;
                break;
            case Kelvin:
                _value = value + Min;
                break;
            default:
                throw new Invalid_temperature_exception("Illegal unit");
        }
        if (_value < Min) {
            throw new Invalid_temperature_exception("Illegal temperature");
        }
    }

    public Temperature(float value, byte unit, float step) throws Invalid_temperature_exception {
        this(value, unit);
        _step = step;
    }

    public float asCelsius() {
        return _value;
    }

    public float asFahrenheit() {
        return 9.F / 5.F * _value + 32.F;
    }

    public float asKelvin() {
        return _value - Min;
    }

    public String asStringCelsius(int precision) {
        if (precision < 0) {
            return String.valueOf(asCelsius()) + Literals[Celsius];
        }
        return String.valueOf(asCelsius()).substring(0, String.valueOf(asCelsius()).indexOf('.') + precision + 1) + Literals[Celsius];
    }

    public String asStringFahrenheit(int precision) {
        if (precision < 0) {
            return String.valueOf(asFahrenheit()) + Literals[Fahrenheit];
        }
        return String.valueOf(asFahrenheit()).substring(0, String.valueOf(asFahrenheit()).indexOf('.') + precision + 1) + Literals[Fahrenheit];
    }

    public String asStringKelvin(int precision) {
        if (precision < 0) {
            return String.valueOf(asKelvin()) + Literals[Kelvin];
        }
        return String.valueOf(asKelvin()).substring(0, String.valueOf(asKelvin()).indexOf('.') + precision + 1) + Literals[Kelvin];
    }

    public Object clone() /**
     * **throws CloneNotSupportedException***
     */
    {
        // 'implements Cloneable' avoids the raising of an instance of CloneNotSupportedException
        // cloning amounts then to field-by-field copy
        /**
         * **return super.clone();***
         */
        try {
            return new Temperature(_value, Celsius, _step);
        } catch (Invalid_temperature_exception ite) {
            return new Temperature();
        }
    }

    public void decrement() {
        _value -= _step;
        if (_value < Min) {
            _value = Min;
        }
    }

    public void increment() {
        _value += _step;
    }

    public boolean equals(Temperature t) {
        return _value == t._value;
    }

    public boolean greaterThan(Temperature t) {
        return _value > t._value;
    }

    public boolean greaterOrEqualThan(Temperature t) {
        return _value >= t._value;
    }

    public boolean lessThan(Temperature t) {
        return _value < t._value;
    }

    public boolean lessOrEqualThan(Temperature t) {
        return _value <= t._value;
    }
}
