var React = require('React');
var Router = require('director').Router;
var StorefrontPage = require('./storefront/storefront-page.js');
var BookDetailPage = require('./book/book-detail-page.js');
var PersonDetailPage = require('./person/person-detail-page.js');
var AboutPage = require('./about/about-page.js');

var Nav = {
  UNDEFINED: "undefined",
  STOREFRONT: "storefront",
  PERSON_DETAILS: "person",
  BOOK_DETAILS: "book",

  ABOUT: "about"
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
    var gotoAboutPage = this.setState.bind(this, {nowShowing: Nav.ABOUT})
    var gotoBookPage = function (bookId) {
      this.setState({nowShowing: Nav.BOOK_DETAILS, bookId: bookId});
    }.bind(this);
    var gotoPersonPage = function (personId) {
      this.setState({nowShowing: Nav.PERSON_DETAILS, personId: personId});
    }.bind(this);

    var router = Router({
      '/storefront': gotoStorefrontPage,

      '/person/:personId': gotoPersonPage,
      '/book/:bookId': gotoBookPage,

      '/about': gotoAboutPage
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

      case Nav.PERSON_DETAILS:
        return (<PersonDetailPage services={this.props.services} personId={this.state.personId} />);

      case Nav.ABOUT:
        return (<AboutPage />);

      default:
        return (<StorefrontPage services={this.props.services} />);
    }
  }
});

