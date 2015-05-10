var React = require('React');

var Dispatcher = require('./view/dispatcher.js');
var LibService = require('./service/lib-service.js').LibService;

window.onload = function () {
  var services = {
    libService: new LibService()
  };
  React.render(React.createElement(Dispatcher, {services: services}),
    document.getElementById('main-content'));
}
