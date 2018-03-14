

$( document ).ready(function() {
    createBluetoothButton();
});



function createBluetoothButton(){
    //var $bluetooth = $('<button id="bluetooth">Test</button>');
    //image not working!! why??
    var $bluetooth = $('<button id="bluetooth"><img src="../img/icon16.png"></button>');
    //$bluetooth.append('<img src="../img/icon16.png">');

    $('input:PASSWORD').after($bluetooth);


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
