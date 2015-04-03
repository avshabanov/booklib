var React = require('React');

var StorefrontPage = require('./view/storefront/storefront-page.js');

$(document).ready(function () {
  console.log("main (1)");
  React.render(React.createElement(StorefrontPage), document.getElementById('main-content'));
});
