var React = require('React');
var Router = require('director').Router;
var StorefrontPage = require('./storefront/storefront-page.js');
var BookDetailPage = require('./book/book-detail-page.js');

var PersonDetailPage = require('./person/person-detail-page.js');
var PersonHintsOrListPage = require('./person/person-hints-or-list-page.js');

var LanguageDetailPage = require('./language/language-detail-page.js');
var LanguageListPage = require('./language/language-list-page.js');

var GenreDetailPage = require('./genre/genre-detail-page.js');
var GenreListPage = require('./genre/genre-list-page.js');

var AboutPage = require('./about/about-page.js');

var Nav = {
  UNDEFINED: "undefined",
  STOREFRONT: "storefront",
  BOOK_DETAILS: "book",

  PERSON_DETAILS: "person",
  PERSON_LIST: "persons",

  LANGUAGE_DETAILS: "language",
  LANGUAGE_LIST: "languages",

  GENRE_DETAILS: "genre",
  GENRE_LIST: "genres",

  ABOUT: "about"
};

function setStartTitle(pageNamePart) {
  if (pageNamePart != null) {
    document.title = "Booklib \u00BB " + pageNamePart;
    return;
  }

  document.title = "Booklib \u00BB Loading...";
}

module.exports = React.createClass({
  getInitialState: function () {
    return {
      nowShowing: Nav.UNDEFINED,
      bookId: null,
      prefix: null
    };
  },

  componentDidMount: function () {
    var gotoStorefrontPage = this.setState.bind(this, {nowShowing: Nav.STOREFRONT});

    var gotoBookPage = function (bookId) {
      this.setState({nowShowing: Nav.BOOK_DETAILS, bookId: bookId});
    }.bind(this);

    var gotoPersonPage = function (personId) {
      this.setState({nowShowing: Nav.PERSON_DETAILS, personId: personId});
    }.bind(this);
    var gotoPersonsPage = this.setState.bind(this, {nowShowing: Nav.PERSON_LIST, prefix: null});
    var gotoPersonsPageWithHint = function (namePart) {
      this.setState({nowShowing: Nav.PERSON_LIST, prefix: namePart});
    }.bind(this);

    var gotoLanguagePage = function (languageId) {
      this.setState({nowShowing: Nav.LANGUAGE_DETAILS, languageId: languageId});
    }.bind(this);
    var gotoLanguagesPage = this.setState.bind(this, {nowShowing: Nav.LANGUAGE_LIST});
    
    var gotoGenrePage = function (genreId) {
      this.setState({nowShowing: Nav.GENRE_DETAILS, genreId: genreId});
    }.bind(this);
    var gotoGenresPage = this.setState.bind(this, {nowShowing: Nav.GENRE_LIST});

    var gotoAboutPage = this.setState.bind(this, {nowShowing: Nav.ABOUT});

    var router = Router({
      '/storefront': gotoStorefrontPage,

      '/book/:bookId': gotoBookPage,

      '/person/:personId': gotoPersonPage,
      '/persons': gotoPersonsPage,
      '/persons/prefix/:namePart': gotoPersonsPageWithHint,

      '/language/:languageId': gotoLanguagePage,
      '/languages': gotoLanguagesPage,

      '/genre/:genreId': gotoGenrePage,
      '/genres': gotoGenresPage,

      '/about': gotoAboutPage
    });

    router.init('/storefront');
  },

  render: function() {
    //console.log("dispatcher state", this.state);
    switch (this.state.nowShowing) {
      case Nav.UNDEFINED: // happens once on loading
        setStartTitle("Main");
        return (<div/>);

      case Nav.STOREFRONT:
        setStartTitle();
        return (<StorefrontPage services={this.props.services} />);

      case Nav.BOOK_DETAILS:
        setStartTitle();
        return (<BookDetailPage services={this.props.services} bookId={this.state.bookId} />);

      case Nav.PERSON_DETAILS:
        setStartTitle();
        return (<PersonDetailPage services={this.props.services} personId={this.state.personId} />);

      case Nav.PERSON_LIST:
        setStartTitle("Persons");
        return (<PersonHintsOrListPage services={this.props.services} prefix={this.state.prefix} />);

      case Nav.GENRE_DETAILS:
        setStartTitle("Genre");
        return (<GenreDetailPage services={this.props.services} genreId={this.state.genreId} />);

      case Nav.GENRE_LIST:
        setStartTitle("Genres");
        return (<GenreListPage services={this.props.services} />);

      case Nav.LANGUAGE_DETAILS:
        setStartTitle("Language");
        return (<LanguageDetailPage services={this.props.services} languageId={this.state.languageId} />);

      case Nav.LANGUAGE_LIST:
        setStartTitle("Languages");
        return (<LanguageListPage services={this.props.services} />);

      case Nav.ABOUT:
        setStartTitle("About");
        return (<AboutPage />);

      default:
        setStartTitle();
        return (<StorefrontPage services={this.props.services} />);
    }
  }
});

