var React = require('React');

var StorefrontPage = require('./view/storefront/storefront-page.js');
var LibService = require('./service/lib-service.js').LibService;

window.onload = function () {
  console.log("main (2)");
  React.render(React.createElement(StorefrontPage), document.getElementById('main-content'));
}
