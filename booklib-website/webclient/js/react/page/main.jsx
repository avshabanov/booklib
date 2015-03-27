var app = app || {};

var Nav = {
  UNDEFINED: "undefined",
  FAVS_PAGE: "favs",
  BOOK_PAGE: "book"
};

var MainPage = React.createClass({
  getInitialState: function () {
    return {
      nowShowing: Nav.UNDEFINED,
      bookId: null
    };
  },

  componentDidMount: function () {
    var gotoFavsPage = this.setState.bind(this, {nowShowing: Nav.FAVS_PAGE})
    var gotoBookPage = function (bookId) {
      this.setState({nowShowing: Nav.BOOK_PAGE, bookId: bookId});
    }.bind(this);

    var router = Router({
      '/favs': gotoFavsPage,
      '/book/:bookId': gotoBookPage
    });

    router.init('/favs');
  },

  render: function() {
    switch (this.state.nowShowing) {
      case Nav.UNDEFINED: // happens once on loading
        return (<div/>);

      case Nav.FAVS_PAGE:
        return (<FavListsPage />);

      case Nav.BOOK_PAGE:
        return (<SingleBookPage bookId={this.state.bookId} />);

      default:
        return (<FavListsPage />);
    }
  }
});

