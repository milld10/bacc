
const customServiceUUID = "0000a000-0000-1000-8000-00805f9b34fb";
const characteristicUsernameUUID = "0000a001-0000-1000-8000-00805f9b34fb";
const characteristicPasswordUUID = "0000a002-0000-1000-8000-00805f9b34fb";


$( document ).ready(function() {
  createBluetoothButton();
});



function createBluetoothButton(){
  let $bluetooth = $('<button id="bluetooth">CONNECT BLE</button>');
  $('input:password').after($bluetooth);



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
      console.log("getting all characteristics");

      return service.getCharacteristics();
    })
    .then(characteristics => {

      let queue = Promise.resolve();
      let decoder = new TextDecoder('utf-8');
      characteristics.forEach(characteristic => {
        switch (characteristic.uuid) {

          case BluetoothUUID.getCharacteristic(characteristicPasswordUUID):
          queue = queue.then(_ => characteristic.readValue()).then(value => {
            console.log('> Password: ' + decoder.decode(value));
            //document.getElementById("user_password").value = decoder.decode(value);
          });
          break;
          
          case BluetoothUUID.getCharacteristic(characteristicUsernameUUID):
          queue = queue.then(_ => characteristic.readValue()).then(value => {
            console.log('> Username: ' + decoder.decode(value));
            //document.getElementById("user_login").value = decoder.decode(value);
          });
          break;




          /*
          var enc = new TextDecoder("utf-8");
          var help = enc.decode(dataView.buffer);
          if(c.uuid == characteristicUsernameUUID){
            console.log("Username: " + document.getElementById("username").value);
            document.getElementById("username").value = help;
          }
          if(c.uuid == characteristicPasswordUUID) {
            console.log("Password: " + document.getElementById("password").value);
            document.getElementById("password").value = help;
          }
          */

          default: console.log('> Unknown Characteristic: ' + characteristic.uuid);
        }
      });

      return queue;

    })
    .catch(error => { console.log("Error Message: " + error); });

  });

}
