
const customServiceUUID = "0000a000-0000-1000-8000-00805f9b34fb";
const characteristicUsernameUUID = "0000a001-0000-1000-8000-00805f9b34fb";
const characteristicPasswordUUID = "0000a002-0000-1000-8000-00805f9b34fb";


$( document ).ready(function() {
    createBluetoothButton();
});



function createBluetoothButton(){
    // image not working, check again (fontawesome)
    // let $bluetooth = $('<button id="bluetooth"> <img src="../img/icon16.png"> </button>');

    let $bluetooth = $('<button id="bluetooth">BT</button>');
    $('input:password').before($bluetooth);

    

    $('#bluetooth').click(function() {
        navigator.bluetooth.requestDevice({
                filters: [{
                  services: [customServiceUUID]
                }]
            })
            .then(device => {
                console.log("Device name: " + device.name);
                return device.gatt.connect();
            })
            .then(server => {
                console.log("getting primary service");
                return server.getPrimaryService(customServiceUUID);
            })
            .then(service => {
                console.log("getting username characteristic");
                
                return service.getCharacteristics();
            })
            .then(characteristics => {
                console.log("reading value of characteristic");
                console.log('> Characteristics: ' + characteristics.map(c => c.uuid).join('\n' + ' '.repeat(19)));
                
                console.log('> Characteristics: ' + characteristics.map(c => c.readValue().then(function(dataView){
                  var enc = new TextDecoder("utf-8");
                  var help = enc.decode(dataView.buffer);
                  if(c.uuid == characteristicUsernameUUID){
                      document.getElementById("username").value = help;
                    }
                    if(c.uuid == characteristicPasswordUUID) {
                      document.getElementById("password").value = help;
                    }
                  
                })));
                
                console.log("Now done.");
            })
            .catch(error => { console.log("Error Message: " + error); });

    });

}
