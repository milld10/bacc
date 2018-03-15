

$( document ).ready(function() {
    createBluetoothButton();
});



function createBluetoothButton(){
    //image not working!! why??
    //var $bluetooth = $('<button id="bluetooth"> </button>');
    //$("bluetooth").append('<img src="../img/icon16.png">');

    var $bluetooth = $('<button id="bluetooth">BT</button>');

    $('input:password').before($bluetooth);

    var $test = $('<button id="testbtn">Test</button>');

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

    $('#bluetooth').click(function() {
        navigator.bluetooth.requestDevice([acceptAllDevices[true]])
            .then(function(bluetoothDevice){
                console.log("Bluetooth working!");
            })
    });

    $('#testbtn').click(function() {
        alert("HEEEEEEEERE!!!!!!!!!!!!!!!!!!!!")
    });

}
