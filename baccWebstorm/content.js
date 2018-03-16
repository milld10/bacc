

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
        navigator.bluetooth.requestDevice(options)
        .then(device => {
            alert("BLUETOOTH!");
            log('connected!');
        })
        .catch(error => {
            log("EEEEEEERRRRRRRRRRRRORRRRRRR: " + error);
        });

    });

    $('#testbtn').click(function() {
        alert("HEEEEEEEERE!!!!!!!!!!!!!!!!!!!!")
    });


}
