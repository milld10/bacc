

$( document ).ready(function() {
    createBluetoothButton();
});



function createBluetoothButton(){
    // image not working, check again
    // let $bluetooth = $('<button id="bluetooth"> <img src="../img/icon16.png"> </button>');

    let $bluetooth = $('<button id="bluetooth">BT</button>');
    $('input:password').before($bluetooth);


    /*
    $('#bluetooth').click(function() {
      navigator.bluetooth.requestDevice({
        acceptAllDevices: true
      })
      .then(device => {
        alert("Bluetooth working!")
      })
      .catch(error => {
        console.log(error);
      });
    });
    */

/*    $('#bluetooth').click(function() {
    navigator.bluetooth.requestDevice([acceptAllDevices[true]])
        .then(function(bluetoothDevice){
            console.log("Bluetooth working!");
        })
});*/

    // let options = {};
    // options.acceptAllDevices = true;

    $('#bluetooth').click(function() {
        navigator.bluetooth.requestDevice({
            filters: [{ services: ['battery_service'] }]
            // acceptAllDevices: true
            // optionalServices: ['battery_service']
        })
            .then(device => {
                // Human-readable name of the device.
                console.log(device.name);

                // Attempts to connect to remote GATT Server.
                return device.gatt.connect();
            })
            .then(server => {
                return server.getPrimaryService('battery_service');
            })
            .then(service => {
                return service.getCharacteristic('battery_level');
            })
            .then(characteristic => {
                return characteristic.readValue();
            })
            .then(value => {
                console.log('Battery percentage is ' + value.getUint8(0));
            })
            .catch(error => { console.log("HELP ERROR: " + error); });

    });

}
