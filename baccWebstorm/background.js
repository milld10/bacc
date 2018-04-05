//
//
//
// $( document ).ready(function() {
//     createBluetoothButton();
// });
//
//
//
// function createBluetoothButton(){
//     //image not working!! why??
//     //var $bluetooth = $('<button id="bluetooth"> </button>');
//     //$("bluetooth").append('<img src="../img/icon16.png">');
//
//     let $bluetooth = $('<button id="bluetooth">BT</button>');
//
//     $('input:password').before($bluetooth);
//
//     let $test = $('<button id="testbtn">Test</button>');
//
//     $('input:password').after($test);
//
//
//     /*
//     $('#bluetooth').click(function() {
//       navigator.bluetooth.requestDevice({
//         acceptAllDevices: true
//       })
//       .then(device => {
//         alert("Bluetooth working!")
//       })
//       .catch(error => {
//         console.log(error);
//       });
//     });
//     */
//
//     /*    $('#bluetooth').click(function() {
//         navigator.bluetooth.requestDevice([acceptAllDevices[true]])
//             .then(function(bluetoothDevice){
//                 console.log("Bluetooth working!");
//             })
//     });*/
//
//     let options = {};
//     options.acceptAllDevices = true;
//
//     $('#bluetooth').click(function() {
//         // navigator.bluetooth.requestDevice(options)
//         // .then(device => {
//         //     alert("BLUETOOTH!");
//         //     console.log('connected!');
//         // })
//         // .catch(error => {
//         //     console.log("EEEEEEERRRRRRRRRRRRORRRRRRR: " + error);
//         // });
//
//
//
//
//
//         navigator.bluetooth.requestDevice({ filters: [{ services: ['battery_service'] }] })
//             .then(device => device.gatt.connect())
//             .then(server => {
//                 return server.getPrimaryService('battery_service');
//             })
//             .then(service => {
//                 return service.getCharacteristic('battery_level');
//             })
//             .then(characteristic => {
//                 return characteristic.readValue();
//             })
//             .then(value => {
//                 console.log('Battery percentage is ' + value.getUint8(0));
//             })
//             .catch(error => { console.log("HELP ERROR: " + error); });
//
//     });
//
//     $('#testbtn').click(function() {
//         alert("HEEEEEEEERE!!!!!!!!!!!!!!!!!!!!")
//     });
//
//
// }
