

$( document ).ready(function() {

    createBluetoothButton();    
 
});



function createBluetoothButton(){
    var $bluetooth = $('<button id="bluetooth">Test Bluetooth</button>');
    $bluetooth.appendTo($("body"));


    $('#bluetooth').click(function() {
      navigator.bluetooth.requestDevice({
        acceptAllDevices: true,
        optionalServices: ['battery_service']
      })
      .then(device => { 
        alert("Bluetooth working!")
      })
      .catch(error => { 
        console.log(error); 
      });
    });



}
