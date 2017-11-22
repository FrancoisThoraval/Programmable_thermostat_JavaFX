"use strict";

let id;
//let socket = new WebSocket("ws://10.8.23.64:8080/", "PauWare_view");
let socket = new WebSocket("ws://localhost:8080/", "PauWare_view");

let Window_loaded = null;
Object.defineProperty(window, "Window_loaded", {value: new Promise(g => {
        Window_loaded = main;
    }), enumerable: false, configurable: false, writable: false});
window.addEventListener('load', Window_loaded);

socket.onmessage = onMessage;

socket.onopen = (event) => {
    console.log("connection established");
    let message = {
        action: "CONNECTION ESTABLISHED",
        description: "xx",
        id: "-1"
    };
    socket.send(JSON.stringify(message));
    swal({
        position: 'top-right',
        width: '300px',
        type: 'success',
        title: 'CONNECTION ESTABLISHED',
        showConfirmButton: false,
        timer: 1500
    });
};

socket.onclose = (event) => {
    console.error("CLIENT SAYS:");
    console.error("LOST CONNECTION TO SERVER");
    console.error("Please reboot java app and refresh this page !");

    swal({
        title: 'Oops ! Something went wrong!',
        text: 'Trying to reconnect !',
        type: 'error',
        timer: 4000,
        onOpen: function () {
            swal.showLoading();
        }
    }).then(
            function () {},
            // handling the promise rejection
                    function (dismiss) {
                        if (dismiss === 'timer') {
                            location.reload();
                        }
                    }
            );

        };


function onMessage(event) {
    console.log("message received !");
    let thermostat = JSON.parse(event.data);
    switch (thermostat.action) {
        case "set_id":
            set_id(thermostat);
            break;

            //Tout les inputs de Programmable_thermostat_input.java
        case "f_c" :
            f_c(thermostat.unit, thermostat.temp, thermostat.description);
            break;
        case "view_program":
            view_program(thermostat);
            break;

        case "heat":
            heat();
            break;
        case "cool":
            cool();
            break;
        case "off":
            off();
            break;
        case "auto":
            auto();
            break;
        case "on":
            on();
            break;
            //------------------------------------------------------
            //Tout les output de Programmable_thermostat_output.java
        case "display_target_temperature":
            display_target_temperature(thermostat);
            break;

        case "display_ambient_temperature":
            display_ambient_temperature(thermostat);
            break;
        case "display_current_date_and_time":
            display_current_date_and_time(thermostat);
            break;
        case "update_run_indicator_status":
            update_run_indicator_status();
            break;
    }
}

function set_id(jsonValues) {
    id = jsonValues.value0;
    console.log(id);
}

function auto() {
    $('#fan_switch_auto').click(); //not optimal but it's a workaround for the bootstrap radios ...
}

function on() {
    $('#fan_switch_on').click(); //not optimal but it's a workaround for the bootstrap radios ...
}

function off() {
    $('#off').click(); //not optimal but it's a workaround for the bootstrap radios ...
}

function cool() {
    $('#cool').click(); //not optimal but it's a workaround for the bootstrap radios ...
}

function heat() {
    $('#heat').click(); //not optimal but it's a workaround for the bootstrap radios ...
}

function view_program(jsonValues) {
    program = jsonValues.value0;
    $('#date').html(program);
}

function display_current_date_and_time(jsonValues) {
    let date = jsonValues.value0;
    $('#date').html(date);
}

function display_target_temperature(jsonValues) {
    let temp = jsonValues.value0;
    let unit = jsonValues.value1;
    $('#targetTemp').html("Target Temp: " + temp + "°" + unit);
    $('#futureTempOut').val(temp);
    $('#futureTempIn').val(temp);
}

function display_ambient_temperature(jsonValues) {
    let temp = jsonValues.value0;
    let unit = jsonValues.value1;
    $('#actualTemp').html("Actual temp: " + temp + "°" + unit);
}

function run_program(description) {
    console.log("Description: " + description);
}

function fan_switch_auto(description) {
    console.log("Description: " + description);
}

function f_c(unit, temp, description) {
    $('#actualTemp').html("Actual temp: " + temp + "°" + unit);
    console.log("Temp: " + temp + "°" + unit);
    console.log("Description: " + description);
}

function sendMessage(jsonValues) {
    let messageToSend = {
        action: jsonValues[0],
        description: jsonValues[1],
        id: id
    };
    if (jsonValues.length > 2) {
        for (let i = 2; i < jsonValues.length; i += 2)
            messageToSend[jsonValues[i]] = jsonValues[i + 1];
    }
    socket.send(JSON.stringify(messageToSend));
}

function main() {
    console.log("app loaded");
    //Clic sur le bouton "run program"
    $('#run_program').on('click', (event) => {
        sendMessage(["run_program", "program should run"]);
    });

    $('#fan_switch_auto').on('change', (event) => {
        sendMessage(["fan_switch_auto", "Switch fan on automatic mode"]);
    });

    $('#fan_switch_on').on('change', (event) => {
        sendMessage(["fan_switch_on", "Switch fan on 'on' mode"]);
    });

    $('#cool').on('change', (event) => {
        sendMessage(["cool", "asking to cool temperature in house"]);
    });

    $('#heat').on('change', (event) => {
        sendMessage(["heat", "asking to heat temperature in house"]);
    });

    $('#off').on('change', (event) => {
        sendMessage(["off", "asking to off temperature in house"]);
    });

    $('#f_c').on('click', (event) => {
        sendMessage(["f_c", "Toggle unit"]);
    });

    $('#futureTempIn').on('input', (event) => {
        $('#futureTempOut').val($('#futureTempIn').val());
        sendMessage(["target_temp_change", "changing the target temperature mode", "temp", $('#futureTempOut').val()]);
    });

    $('#timeForward').on('click', (event) => {
        sendMessage(["time_forward", "forwarding time"]);
    });

    $('#timeBackward').on('click', (event) => {
        sendMessage(["time_backward", "backwarding time"]);
    });

    $('#set_day').on('click', (event) => {
        sendMessage(["set_day", "setting the day"]);
    });

    $('#set_clock').on('click', (event) => {
        sendMessage(["set_clock", "setting the clock"]);
    });

    $('#view_program').on('click', (event) => {
        sendMessage(["view_program", "viewing programs"]);
    });
}