var React = require('React');
var Router = require('director').Router;
var StorefrontPage = require('./storefront/storefront-page.js');
var BookDetailPage = require('./book/book-detail-page.js');

var Nav = {
  UNDEFINED: "undefined",
  STOREFRONT: "storefront",
  BOOK_DETAILS: "book"
};

module.exports = React.createClass({
  getInitialState: function () {
    return {
      nowShowing: Nav.UNDEFINED,
      bookId: null
    };
  },

  componentDidMount: function () {
    var gotoStorefrontPage = this.setState.bind(this, {nowShowing: Nav.STOREFRONT})
    var gotoBookPage = function (bookId) {
      this.setState({nowShowing: Nav.BOOK_DETAILS, bookId: bookId});
    }.bind(this);

    var router = Router({
      '/storefront': gotoStorefrontPage,
      '/book/:bookId': gotoBookPage
    });

    router.init('/storefront');
  },

  render: function() {
    switch (this.state.nowShowing) {
      case Nav.UNDEFINED: // happens once on loading
        return (<div/>);

      case Nav.STOREFRONT:
        return (<StorefrontPage services={this.props.services} />);

      case Nav.BOOK_DETAILS:
        return (<BookDetailPage services={this.props.services} bookId={this.state.bookId} />);

      default:
        return (<StorefrontPage services={this.props.services} />);
    }
  }
});

