package com.FranckBarbier.Java._Programmable_thermostat_GUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;

public class User_interface_JavaFX extends javafx.application.Application implements com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat_output {

    /**
     * MODEL
     */
    com.FranckBarbier.Java._Temperature.Temperature _ambient_temperature;
    com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat _programmable_thermostat;
    com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Temperature_sensor _temperature_sensor;
    /**
     * UI
     */
    private final static double _WindowMinWidth = 1000;
    private final static double _WindowMinHeight = 600;
    /**
     * Styles
     */
    private final static String _StyleBackgroundRedLight = "-fx-background-color:#ffdad1;";
    private final static String _StyleBackgroundBlueLight = "-fx-background-color:#d1eaff;";
    private final static String _StyleBackgroundGreenLight = "-fx-background-color:#c8f9c3;";
    private final static String _StyleBackgroundGrayLight = "-fx-background-color:#eee;";
    private final static String _StyleBorderButton = "-fx-border-width:2px;-fx-padding:4px;";
    private final static String _StyleBorderBlue = "-fx-border-color:" + "#0000FF" + ";";
    private final static String _StyleBorderRed = "-fx-border-color:" + "#FF0000" + ";";
    private final static String _StyleBorderGreen = "-fx-border-color:" + "#57dc49" + ";";
    private final static String _StyleBorderGray = "-fx-border-color:" + "#AAAAAA" + ";";
    private final static String _StyleSlider = "";
    private final static String _StyleButtonGreen = "-fx-background-radius:20;-fx-background-color:rgba(0,0,0,0.05),linear-gradient(#306108, #5cb811),linear-gradient(#5cb811, #77d42a);-fx-background-insets:0,1,3;-fx-text-fill:#ffffff;";
    private final static String _StyleButtonRed = "-fx-background-radius:20;-fx-background-color:rgba(0,0,0,0.05),linear-gradient(#8c2811, #d0451b),linear-gradient(#d0451b, #bc3315);-fx-background-insets:0,1,3;-fx-text-fill:#ffffff;";
    private final static String _StyleButtonBlue = "-fx-background-radius:20;-fx-background-color:rgba(0,0,0,0.05),linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);-fx-background-insets: 0,1,2,3;-fx-text-fill: white;";

    private javafx.scene.control.Label _current_state = new javafx.scene.control.Label();
    private javafx.scene.layout.BorderPane masterPane = new javafx.scene.layout.BorderPane();
    private javafx.scene.layout.GridPane mainGrid = new javafx.scene.layout.GridPane();
    private javafx.scene.layout.GridPane leftGrid = new javafx.scene.layout.GridPane();
    private javafx.scene.layout.GridPane rightGrid = new javafx.scene.layout.GridPane();
    private javafx.scene.control.Label _ambient_temperature_versus_current_date_and_time = new javafx.scene.control.Label();
    private javafx.scene.control.Button _time_backward = new javafx.scene.control.Button();
    private javafx.scene.control.Button _time_forward = new javafx.scene.control.Button();
    private javafx.scene.control.Button _set_clock = new javafx.scene.control.Button();
    private javafx.scene.control.Button _set_day = new javafx.scene.control.Button();
    private javafx.scene.control.Button _view_program = new javafx.scene.control.Button();
    private javafx.scene.control.CheckBox _fan_switch_auto = new javafx.scene.control.CheckBox();
    private javafx.scene.control.CheckBox _fan_switch_on = new javafx.scene.control.CheckBox();
    private javafx.scene.control.RadioButton _season_switch_cool = new javafx.scene.control.RadioButton();
    private javafx.scene.control.RadioButton _season_switch_off = new javafx.scene.control.RadioButton();
    private javafx.scene.control.RadioButton _season_switch_heat = new javafx.scene.control.RadioButton();
    private javafx.scene.control.Label _target_temperature = new javafx.scene.control.Label();

    private float _temp_down_temp_up_value = (com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Min(com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Default_temperature_mode) + com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Max(com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Default_temperature_mode)) / 2.F;
    private javafx.scene.control.Slider _temp_down_temp_up = new javafx.scene.control.Slider(com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Min(com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Default_temperature_mode), com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Max(com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Default_temperature_mode), _temp_down_temp_up_value);
    private javafx.scene.control.Button _f_c = new javafx.scene.control.Button();
    private javafx.scene.control.Button _hold_temp = new javafx.scene.control.Button();
    private javafx.scene.control.Button _run_program = new javafx.scene.control.Button();
    private javafx.scene.control.Button _run_indicator = new javafx.scene.control.Button();

    /**
     * Slider management
     */
    private boolean _display_target_temperature = false;
    private boolean _display_program_target_temperature = false;
    private boolean _run_program_click = false;
    private boolean _view_program_click = false;

    @Override
    public void start(javafx.stage.Stage primaryStage) {
        try {
            _ambient_temperature = new com.FranckBarbier.Java._Temperature.Temperature(18.F, com.FranckBarbier.Java._Temperature.Temperature.Celsius, (float) 1.E-2);
            _programmable_thermostat = new com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat(_ambient_temperature);
            _temperature_sensor = new com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Temperature_sensor(_programmable_thermostat, _ambient_temperature);
        } catch (Throwable t) {
            System.err.println("User interface <programmable thermostat initialization> failure: " + t.getMessage());
            System.exit(1);
        }
        /* Current state */
        _current_state.setAlignment(javafx.geometry.Pos.CENTER);
        _current_state.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _current_state.setFont(javafx.scene.text.Font.font(10));
        /* F-C */
        _f_c.setText("F-C");
        _f_c.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _f_c.setTooltip(new javafx.scene.control.Tooltip("convert Fahrenheit to Celsius or convert Celsius to Fahrenheit"));
        /* Hold temp. */
        _hold_temp.setText("Hold temp");
        _hold_temp.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _hold_temp.setTooltip(new javafx.scene.control.Tooltip("Do not perform control program"));
        /* Run program */
        _run_program.setText("Run program");
        _run_program.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _run_program.setTooltip(new javafx.scene.control.Tooltip("Perform control program"));
        /* Target temp. */
        _target_temperature.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _target_temperature.setAlignment(javafx.geometry.Pos.CENTER);
        /* Season switch */
        _season_switch_cool.setText("cool");
        _season_switch_cool.setStyle(_StyleBorderButton + _StyleBorderBlue);
        _season_switch_cool.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _season_switch_off.setText("off");
        _season_switch_off.setStyle(_StyleBackgroundGrayLight + _StyleBorderButton + _StyleBorderGray);
        _season_switch_off.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _season_switch_heat.setText("heat");
        _season_switch_heat.setStyle(_StyleBorderButton + _StyleBorderRed);
        _season_switch_heat.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        leftGrid.add(_season_switch_cool, 0, 0);
        leftGrid.add(_season_switch_off, 0, 2);
        leftGrid.add(_season_switch_heat, 0, 1);
        /* Setup */
        _set_clock.setText("Set clock");
        _set_clock.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _set_day.setText("Set day");
        _set_day.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _view_program.setText("View program");
        _view_program.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        leftGrid.add(_set_clock, 1, 0);
        leftGrid.add(_set_day, 1, 1);
        leftGrid.add(_view_program, 1, 2);
        /* Fan switch */
        _fan_switch_auto.setText("auto");
        _fan_switch_auto.setStyle(_StyleBackgroundGreenLight + _StyleBorderButton + _StyleBorderGreen);
        _fan_switch_auto.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _fan_switch_on.setText("on");
        _fan_switch_on.setStyle(_StyleBorderButton + _StyleBorderGreen);
        _fan_switch_on.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        rightGrid.add(_fan_switch_auto, 1, 0);
        rightGrid.add(_fan_switch_on, 1, 1);
        /* Time backward/forward */
        _time_backward.setText("Time backward");
        _time_backward.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _time_forward.setText("Time forward");
        _time_forward.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        rightGrid.add(_time_backward, 0, 0);
        rightGrid.add(_time_forward, 0, 1);
        /* Ambient temp. vs current date and time */
        _ambient_temperature_versus_current_date_and_time.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _ambient_temperature_versus_current_date_and_time.setAlignment(javafx.geometry.Pos.CENTER);
        _ambient_temperature_versus_current_date_and_time.setTooltip(new javafx.scene.control.Tooltip("Ambient temp. decreases in Winter and increases in Summer"));
        /* Temp. up/down */
        _temp_down_temp_up.setStyle(_StyleSlider);
        _temp_down_temp_up.setMajorTickUnit(com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Target_temperature_precision);
        _temp_down_temp_up.setMinorTickCount(0);
        _temp_down_temp_up.setBlockIncrement(com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Target_temperature_precision);
        _temp_down_temp_up.setShowTickMarks(true);
        /**
         * Initializing states
         */
        _fan_switch_auto.setSelected(true);
        _run_indicator.setText("\uD83D\uDCA4");
        _run_indicator.setStyle(_StyleButtonGreen);
        _season_switch_off.setSelected(true);
        /**
         * Handling events
         */
        _fan_switch_auto.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                if (!_fan_switch_auto.selectedProperty().getValue()) {
                    _fan_switch_auto.setStyle(_StyleBorderButton + _StyleBorderGreen);
                    _fan_switch_on.fire();
                } else {
                    _fan_switch_auto.setStyle(_StyleBackgroundGreenLight + _StyleBorderButton + _StyleBorderGreen);
                    _fan_switch_on.setSelected(false);
                    _fan_switch_on.setStyle(_StyleBorderButton + _StyleBorderGreen);
                    try {
                        _programmable_thermostat.fan_switch_auto();
                    } catch (Exception e) {
                        System.err.println("User interface <fan_switch_auto> failure: " + e.getMessage());
                        System.exit(1);
                    }
                }
            }
        });
        _fan_switch_on.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                if (!_fan_switch_on.selectedProperty().getValue()) {
                    _fan_switch_on.setStyle(_StyleBorderButton + _StyleBorderGreen);
                    _fan_switch_auto.fire();
                } else {
                    _fan_switch_on.setStyle(_StyleBackgroundGreenLight + _StyleBorderButton + _StyleBorderGreen);
                    _fan_switch_auto.setSelected(false);
                    _fan_switch_auto.setStyle(_StyleBorderButton + _StyleBorderGreen);
                    try {
                        _programmable_thermostat.fan_switch_on();
                    } catch (Exception e) {
                        System.err.println("User interface <fan_switch_on> failure: " + e.getMessage());
                        System.exit(1);
                    }
                }
            }
        });
        _f_c.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                try {
                    _programmable_thermostat.f_c();
                } catch (Exception e) {
                    System.err.println("User interface <f_c> failure: " + e.getMessage());
                    System.exit(1);
                }
            }
        });
        _hold_temp.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                try {
                    _programmable_thermostat.hold_temp();
                } catch (Exception e) {
                    System.err.println("User interface <hold_temp> failure: " + e.getMessage());
                    System.exit(1);
                }
            }
        });
        _run_program.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                try {
                    _run_program_click = true;
                    _programmable_thermostat.run_program();
                } catch (Exception e) {
                    System.err.println("User interface <run_program> failure: " + e.getMessage());
                    System.exit(1);
                }
            }
        });
        _season_switch_cool.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                if (!_season_switch_cool.selectedProperty().getValue()) {
                    _season_switch_cool.setStyle(_StyleBorderButton + _StyleBorderBlue);
                    _season_switch_heat.fire();
                } else {
                    _season_switch_cool.setStyle(_StyleBackgroundBlueLight + _StyleBorderButton + _StyleBorderBlue);
                    _season_switch_heat.setSelected(false);
                    _season_switch_heat.setStyle(_StyleBorderButton + _StyleBorderRed);
                    _season_switch_off.setSelected(false);
                    _season_switch_off.setStyle(_StyleBorderButton + _StyleBorderGray);

                    try {
                        _programmable_thermostat.season_switch_cool();
                    } catch (Exception e) {
                        System.err.println("User interface <season_switch_cool> failure: " + e.getMessage());
                        System.exit(1);
                    }
                }
            }
        }
        );
        _season_switch_heat.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                if (!_season_switch_heat.selectedProperty().getValue()) {
                    _season_switch_heat.setStyle(_StyleBorderButton + _StyleBorderRed);
                    _season_switch_cool.fire();
                } else {
                    _season_switch_heat.setStyle(_StyleBackgroundRedLight + _StyleBorderButton + _StyleBorderRed);
                    _season_switch_cool.setSelected(false);
                    _season_switch_cool.setStyle(_StyleBorderButton + _StyleBorderBlue);
                    _season_switch_off.setSelected(false);
                    _season_switch_off.setStyle(_StyleBorderButton + _StyleBorderGray);
                    try {
                        _programmable_thermostat.season_switch_heat();
                    } catch (Exception e) {
                        System.err.println("User interface <season_switch_heat> failure: " + e.getMessage());
                        System.exit(1);
                    }
                }
            }
        }
        );
        _season_switch_off.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                if (!_season_switch_off.selectedProperty().getValue()) {
                    _season_switch_off.setSelected(true);
                } else {
                    _season_switch_off.setStyle(_StyleBackgroundGrayLight + _StyleBorderButton + _StyleBorderGray);
                    _season_switch_cool.setSelected(false);
                    _season_switch_cool.setStyle(_StyleBorderButton + _StyleBorderBlue);
                    _season_switch_heat.setSelected(false);
                    _season_switch_heat.setStyle(_StyleBorderButton + _StyleBorderRed);
                    try {
                        _programmable_thermostat.season_switch_off();
                    } catch (Exception e) {
                        System.err.println("User interface <season_switch_off> failure: " + e.getMessage());
                        System.exit(1);
                    }
                }
            }
        }
        );
        _set_clock.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                try {
                    _programmable_thermostat.set_clock();
                } catch (Exception e) {
                    System.err.println("User interface <set_clock> failure: " + e.getMessage());
                    System.exit(1);
                }
            }
        }
        );
        _set_day.setOnAction(
                new javafx.event.EventHandler<javafx.event.ActionEvent>() {
                    @Override
                    public void handle(javafx.event.ActionEvent event) {
                        try {
                            _programmable_thermostat.set_day();
                        } catch (Exception e) {
                            System.err.println("User interface <set_day> failure: " + e.getMessage());
                            System.exit(1);
                        }
                    }
                }
        );
        _temp_down_temp_up.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number old_value, Number new_value) {
                try {
                    if (_temp_down_temp_up_value > new_value.floatValue()) {
                        for (int i = 0; i < Math.round((_temp_down_temp_up_value - new_value.floatValue()) / com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Target_temperature_precision); i++) {
                            _run_program_click = false;
                            _view_program_click = false;
                            _programmable_thermostat.temp_down();
                        }
                    }
                    if (_temp_down_temp_up_value < new_value.floatValue()) {
                        for (int i = 0; i < Math.round((new_value.floatValue() - _temp_down_temp_up_value) / com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Target_temperature_precision); i++) {
                            _run_program_click = false;
                            _view_program_click = false;
                            _programmable_thermostat.temp_up();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("User interface <temp_down/temp_up> failure: " + e.getMessage());
                    System.exit(1);
                }
            }
        }
        );
        _time_backward.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                try {
                    _programmable_thermostat.time_backward();
                } catch (Exception e) {
                    System.err.println("User interface <time_backward> failure: " + e.getMessage());
                    System.exit(1);
                }
            }
        }
        );
        _time_forward.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                try {
                    _programmable_thermostat.time_forward();
                } catch (Exception e) {
                    System.err.println("User interface <time_forward> failure: " + e.getMessage());
                    System.exit(1);
                }
            }
        }
        );
        _view_program.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                try {
                    _view_program_click = true;
                    _programmable_thermostat.view_program();
                } catch (Exception e) {
                    System.err.println("User interface <view_program> failure: " + e.getMessage());
                    System.exit(1);
                }
            }
        }
        );
        primaryStage.setOnCloseRequest(new javafx.event.EventHandler<javafx.stage.WindowEvent>() {
            public void handle(javafx.stage.WindowEvent event) {
                try {
                    _temperature_sensor.stop();
                    _programmable_thermostat.stop();
                    javafx.application.Platform.exit();
                    System.exit(0);
                } catch (Throwable t) {
                    System.err.println("User interface <programmable thermostat finalization> failure: " + t.getMessage());
                    System.exit(1);
                }
            }
        });

        /**
         * Grid constraints
         */
        javafx.scene.layout.ColumnConstraints LGcolumn1 = new javafx.scene.layout.ColumnConstraints();
        LGcolumn1.setPercentWidth(50);
        javafx.scene.layout.ColumnConstraints LGcolumn2 = new javafx.scene.layout.ColumnConstraints();
        LGcolumn2.setPercentWidth(50);
        leftGrid.getColumnConstraints().addAll(LGcolumn1, LGcolumn2);
        javafx.scene.layout.RowConstraints LGrow1 = new javafx.scene.layout.RowConstraints();
        LGrow1.setPercentHeight(33);
        javafx.scene.layout.RowConstraints LGrow2 = new javafx.scene.layout.RowConstraints();
        LGrow2.setPercentHeight(33);
        javafx.scene.layout.RowConstraints LGrow3 = new javafx.scene.layout.RowConstraints();
        LGrow3.setPercentHeight(34);
        leftGrid.getRowConstraints().addAll(LGrow1, LGrow2, LGrow3);
        leftGrid.setHgap(5);
        leftGrid.setVgap(5);

        javafx.scene.layout.ColumnConstraints RGcolumn1 = new javafx.scene.layout.ColumnConstraints();
        RGcolumn1.setPercentWidth(50);
        javafx.scene.layout.ColumnConstraints RGcolumn2 = new javafx.scene.layout.ColumnConstraints();
        RGcolumn2.setPercentWidth(50);
        rightGrid.getColumnConstraints().addAll(RGcolumn1, RGcolumn2);
        javafx.scene.layout.RowConstraints RGrow1 = new javafx.scene.layout.RowConstraints();
        RGrow1.setPercentHeight(50);
        javafx.scene.layout.RowConstraints RGrow2 = new javafx.scene.layout.RowConstraints();
        RGrow2.setPercentHeight(50);
        rightGrid.getRowConstraints().addAll(RGrow1, RGrow2);
        rightGrid.setHgap(5);
        rightGrid.setVgap(5);

        javafx.scene.layout.ColumnConstraints column1 = new javafx.scene.layout.ColumnConstraints();
        column1.setPercentWidth(17);
        javafx.scene.layout.ColumnConstraints column2 = new javafx.scene.layout.ColumnConstraints();
        column2.setPercentWidth(18);
        javafx.scene.layout.ColumnConstraints column3 = new javafx.scene.layout.ColumnConstraints();
        column3.setPercentWidth(30);
        column3.setHalignment(HPos.CENTER);
        javafx.scene.layout.ColumnConstraints column4 = new javafx.scene.layout.ColumnConstraints();
        column4.setPercentWidth(18);
        javafx.scene.layout.ColumnConstraints column5 = new javafx.scene.layout.ColumnConstraints();
        column5.setPercentWidth(17);
        mainGrid.getColumnConstraints().addAll(column1, column2, column3, column4, column5);

        javafx.scene.layout.RowConstraints row1 = new javafx.scene.layout.RowConstraints();
        row1.setPercentHeight(15);
        javafx.scene.layout.RowConstraints row2 = new javafx.scene.layout.RowConstraints();
        row2.setPercentHeight(15);
        javafx.scene.layout.RowConstraints row3 = new javafx.scene.layout.RowConstraints();
        row3.setPercentHeight(15);
        javafx.scene.layout.RowConstraints row4 = new javafx.scene.layout.RowConstraints();
        row4.setPercentHeight(15);
        javafx.scene.layout.RowConstraints row5 = new javafx.scene.layout.RowConstraints();
        row5.setPercentHeight(15);
        javafx.scene.layout.RowConstraints row6 = new javafx.scene.layout.RowConstraints();
        row6.setPercentHeight(15);
        javafx.scene.layout.RowConstraints row7 = new javafx.scene.layout.RowConstraints();
        row7.setPercentHeight(5);
        javafx.scene.layout.RowConstraints row8 = new javafx.scene.layout.RowConstraints();
        row8.setPercentHeight(5);
        mainGrid.getRowConstraints().addAll(row1, row2, row3, row4, row5, row6, row7, row8);

        mainGrid.setHgap(5);
        mainGrid.setVgap(5);
        mainGrid.setPadding(new javafx.geometry.Insets(5, 5, 5, 5));

        /**
         * Build main grid
         */
        mainGrid.add(_run_program, 0, 0, 5, 1);
        mainGrid.add(_f_c, 0, 1);
        mainGrid.add(_hold_temp, 4, 1);
        mainGrid.add(_target_temperature, 2, 1);
        mainGrid.add(_ambient_temperature_versus_current_date_and_time, 2, 4);
        mainGrid.add(_temp_down_temp_up, 0, 2, 5, 1);
        mainGrid.add(leftGrid, 0, 3, 2, 3);
        mainGrid.add(rightGrid, 3, 3, 2, 3);
        mainGrid.add(_run_indicator, 2, 6);
        mainGrid.add(_current_state, 0, 7, 5, 1);

        masterPane.setCenter(mainGrid);
        masterPane.setPrefSize(_WindowMinWidth, _WindowMinHeight);

        javafx.scene.Scene scene = new javafx.scene.Scene(masterPane);
        primaryStage.setTitle("Programmable thermostat (Northern hemisphere only!)");
        primaryStage.setMinWidth(_WindowMinWidth);
        primaryStage.setMinHeight(_WindowMinHeight);
        primaryStage.setScene(scene);
        primaryStage.show();

        _programmable_thermostat.add_user_interface(this);
        try {
            _programmable_thermostat.start();
            _temperature_sensor.start();
        } catch (Throwable t) {
            System.err.println("User interface <programmable thermostat startup> failure: " + t.getMessage());
            System.exit(1);
        }
    }

    public void display_ambient_temperature(final String ambient_temperature, final com.FranckBarbier.Java._Temperature.Temperature at) {
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _ambient_temperature_versus_current_date_and_time.setText(ambient_temperature);
                _current_state.setText(_programmable_thermostat.async_current_state());
            }
        });
    }

    public void display_current_date_and_time(final String current_date_and_time) {
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _ambient_temperature_versus_current_date_and_time.setText(current_date_and_time);
                _current_state.setText(_programmable_thermostat.async_current_state());
            }
        });
    }

    public void display_period_and_program_time(final String period_and_program_time) {
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _ambient_temperature_versus_current_date_and_time.setText(period_and_program_time);
                _current_state.setText(_programmable_thermostat.async_current_state());
            }
        });
    }

    public void display_program_target_temperature(final com.FranckBarbier.Java._Temperature.Temperature target_temperature, final byte temperature_mode) {
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (temperature_mode == com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Default_temperature_mode) {
                    _temp_down_temp_up.setShowTickLabels(true);
                } else {
                    _temp_down_temp_up.setShowTickLabels(false);
                }
                switch (temperature_mode) {
                    case com.FranckBarbier.Java._Temperature.Temperature.Celsius:
                        _target_temperature.setText(target_temperature.asStringCelsius(1));
                        break;
                    case com.FranckBarbier.Java._Temperature.Temperature.Fahrenheit:
                        _target_temperature.setText(target_temperature.asStringFahrenheit(1));
                        break;
                    default:
                        System.err.println("User interface <display_program_target_temperature> failure: non existing temperature mode");
                        System.exit(1);
                }
                switch (com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Default_temperature_mode) {
                    case com.FranckBarbier.Java._Temperature.Temperature.Celsius:
                        _temp_down_temp_up_value = target_temperature.asCelsius();
                        if (_display_target_temperature || _view_program_click) {
                            _temp_down_temp_up.setValue(_temp_down_temp_up_value);
                            _display_target_temperature = false;
                        }
                        break;
                    case com.FranckBarbier.Java._Temperature.Temperature.Fahrenheit:
                        _temp_down_temp_up_value = target_temperature.asFahrenheit();
                        if (_display_target_temperature || _view_program_click) {
                            _temp_down_temp_up.setValue(_temp_down_temp_up_value);
                            _display_target_temperature = false;
                        }
                        break;
                    default:
                        System.err.println("User interface <display_program_target_temperature> failure: non existing default temperature mode");
                        System.exit(1);
                }
                _current_state.setText(_programmable_thermostat.async_current_state());
            }
        }
        );
        _display_program_target_temperature = true;
    }

    public void display_target_temperature(final com.FranckBarbier.Java._Temperature.Temperature target_temperature, final byte temperature_mode) {
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (temperature_mode == com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Default_temperature_mode) {
                    _temp_down_temp_up.setShowTickLabels(true);
                } else {
                    _temp_down_temp_up.setShowTickLabels(false);
                }
                switch (temperature_mode) {
                    case com.FranckBarbier.Java._Temperature.Temperature.Celsius:
                        _target_temperature.setText(target_temperature.asStringCelsius(1));
                        break;
                    case com.FranckBarbier.Java._Temperature.Temperature.Fahrenheit:
                        _target_temperature.setText(target_temperature.asStringFahrenheit(1));
                        break;
                    default:
                        System.err.println("User interface <display_target_temperature> failure: non existing temperature mode");
                        System.exit(1);
                }
                switch (com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Programmable_thermostat.Default_temperature_mode) {
                    case com.FranckBarbier.Java._Temperature.Temperature.Celsius:
                        _temp_down_temp_up_value = target_temperature.asCelsius();
                        if (_display_program_target_temperature || _run_program_click) {
                            _temp_down_temp_up.setValue(_temp_down_temp_up_value);
                            _display_program_target_temperature = false;
                        }
                        break;
                    case com.FranckBarbier.Java._Temperature.Temperature.Fahrenheit:
                        _temp_down_temp_up_value = target_temperature.asFahrenheit();
                        if (_display_program_target_temperature || _run_program_click) {
                            _temp_down_temp_up.setValue(_temp_down_temp_up_value);
                            _display_program_target_temperature = false;
                        }
                        break;
                    default:
                        System.err.println("User interface <display_target_temperature> failure: non existing default temperature mode");
                        System.exit(1);
                }
                _current_state.setText(_programmable_thermostat.async_current_state());
            }
        }
        );
        _display_target_temperature = true;
    }

    public void update_run_indicator_status(final String run_indicator_status) {
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (run_indicator_status.contains(com.FranckBarbier.Java._Programmable_thermostat_MODEL._Programmable_thermostat.Run_indicator.Everything_off)) {
                    _run_indicator.setText("\uD83D\uDCA4");
                    _run_indicator.setStyle(_StyleButtonGreen);
                } else {
                    _run_indicator.setText(String.valueOf('\u26A1'));
                    if (_programmable_thermostat.furnace_relay_is_on()) {
                        _run_indicator.setStyle(_StyleButtonRed);
                    }
                    if (_programmable_thermostat.air_conditioner_relay_is_on()) {
                        _run_indicator.setStyle(_StyleButtonBlue);
                    }
                }
                _current_state.setText(_programmable_thermostat.async_current_state());
            }
        });
    }

    public static void main(String args[]) {
        launch(args);
    }
}
