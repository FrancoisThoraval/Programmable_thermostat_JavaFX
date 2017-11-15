"use strict";

let Window_loaded = null;
Object.defineProperty(window, "Window_loaded", {value: new Promise(g => {
    Window_loaded = main;
}), enumerable: false, configurable: false, writable: false});
window.addEventListener('load', Window_loaded);

let socket = new WebSocket("ws://localhost:8080/", "PauWare_view");
socket.onmessage = onMessage;

socket.onopen = (event)=>{
    console.log("coucou");
    let message = {
        action: "CONNECTION ESTABLISHED",
        description: "xx"
    };
    socket.send(JSON.stringify(message));
};

socket.onclose = (event)=>{
    console.error("CLIENT SAYS:");
    console.error("LOST CONNECTION TO SERVER");
    console.error("Please reboot java app and refresh this page !");
};


function onMessage(event){
    console.log("message received !");
    let thermostat = JSON.parse(event.data);
    switch(thermostat.action){

        //Tout les inputs de Programmable_thermostat_input.java
        case "fan_switch_auto": fan_switch_auto(thermostat.description);
            break;
        case "fan_switch_on" : fan_switch_on();
            break;
        case "f_c" : f_c(thermostat.unit,thermostat.temp,thermostat.description);
            break;
        case "hold_temp" : hold_temp();
            break;
        case "run_program": run_program(thermostat.description);
            break;
        case "season_switch_cool" : season_switch_cool();
            break;
        case "season_switch_heat" : season_switch_heat();
            break;
        case "season_switch_off" : season_switch_off();
            break;
        case "set_clock": set_clock();
            break;
        case "set_day": set_day();
            break;
        case "temp_down": temp_down();
            break;
        case "temp_up": temp_up();
            break;
        case "time_backward": time_backward();
            break;
        case "time_forward": time_forward();
            break;
        case "view_program": view_program();
            break;
        //------------------------------------------------------
        //Tout les output de Programmable_thermostat_output.java
        case "display_ambient_temperature":display_ambient_temperature(thermostat);
            break;
        case "display_current_date_and_time": display_current_date_and_time();
            break;
        case "display_period_and_program_time": display_period_and_program_time();
            break;
        case "display_program_target_temperature": display_program_target_temperature();
            break;
        case "display_target_temperature":display_target_temperature(thermostat);
            break;
        case "update_run_indicator_status":update_run_indicator_status();
            break;
    }
}

function display_target_temperature(jsonValues){
    let temp = jsonValues.value0;
    let unit = jsonValues.value1;

    $('#targetTemp').html("Target Temp: " + temp + "°" + unit);
}

function display_ambient_temperature(jsonValues){
    let temp = jsonValues.value0;
    let unit = jsonValues.value1;
    $('#actualTemp').html("Actual temp: " + temp + "°" + unit);
}

function run_program(description){
    console.log("Description: " + description);
}

function fan_switch_auto(description){
    console.log("Description: " + description);
}

function f_c(unit,temp,description){
    $('#actualTemp').html("Actual temp: " + temp + "°" + unit);
    console.log("Temp: " + temp + "°" + unit);
    console.log("Description: " + description);
}

function main(){
    console.log("app loaded");

    //Clic sur le bouton "run program"
    $('#run_program').on('click',function(event){
        let message = {
            action : "run_program",
            description: "program should run"
        };
        socket.send(JSON.stringify(message));
    });

    $('#fan_switch_auto').on('change',function(event){
        console.log("clicked on fan switch");
        let message = {
            action : "fan_switch_auto",
            description : "Switch fan on automatic mode"
        };
        socket.send(JSON.stringify(message));
    });

    $('#cool').on('change', function(event){
        console.log("Cooling");
        let message = {
            action: "cool",
            description: "asking to cool temperature in house"
        };
        socket.send(JSON.stringify(message));
    });


    $('#heat').on('change', function(event){
        console.log("Heating");
        let message = {
            action: "heat",
            description: "asking to heat temperature in house"
        };
        socket.send(JSON.stringify(message));
    });

    //Bootstraps fucks this part ...
    $('#tempOptions').on('click',function(event){
        let message;
        if($('#optionC').is(':checked')){
            $('#optionF').prop('checked', false);
            console.log("checked temp in C");
            message = {
                action : "f_c",
                description : "Change unit in Celsius"
            };
            console.log(JSON.stringify(message));
        }else if ($('#optionF').is(':checked')){
            $('#optionC').prop("checked", false);
            console.log("checked temp in F");
            message = {
                action : "f_c",
                description : "Change unit in Fahrenheit"
            };
            console.log(JSON.stringify(message));
        }else if ($('#optionHoldTemp').is(':checked')){
            message = {
                action: "hold_temp",
                description: "Asking to hold temperature"
            };
            console.log(JSON.stringify(message));
        }
        //alert(JSON.stringify(message));
        socket.send(JSON.stringify(message));
    });
}
