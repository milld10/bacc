

$( document ).ready(function() {
    createBluetoothButton();
});



function createBluetoothButton(){
    //image not working!! why??
    //var $bluetooth = $('<button id="bluetooth"> </button>');
    //$("bluetooth").append('<img src="../img/icon16.png">');

    let $bluetooth = $('<button id="bluetooth">BT</button>');

    $('input:password').before($bluetooth);

    let $test = $('<button id="testbtn">Test</button>');

    $('input:password').after($test);


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

    let options = {};
    options.acceptAllDevices = true;

    $('#bluetooth').click(function() {
        // navigator.bluetooth.requestDevice(options)
        // .then(device => {
        //     alert("BLUETOOTH!");
        //     console.log('connected!');
        // })
        // .catch(error => {
        //     console.log("EEEEEEERRRRRRRRRRRRORRRRRRR: " + error);
        // });





        navigator.bluetooth.requestDevice({ filters: [{ services: ['battery_service'] }] })
            .then(device => {
                // Human-readable name of the device.
                console.log(device.name);

                // Attempts to connect to remote GATT Server.
                return device.gatt.connect();
            })
            .catch(error => { console.log("HELP ERROR: " + error);; });

    });

    $('#testbtn').click(function() {
        alert("HEEEEEEEERE!!!!!!!!!!!!!!!!!!!!")
    });


}
