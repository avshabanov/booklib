var app = app || {};

var Nav = {
  MAIN_PAGE: "main",
  BOOK_PAGE: "book"
};

var MainPage = React.createClass({
  getInitialState: function () {
    return {
      nowShowing: Nav.MAIN_PAGE,
      bookId: null
    };
  },

  componentDidMount: function () {
    var gotoMainPage = this.setState.bind(this, {nowShowing: Nav.MAIN_PAGE})
    var gotoBookPage = function (bookId) {
      this.setState({nowShowing: Nav.BOOK_PAGE, bookId: bookId});
    }.bind(this);

    var router = Router({
      '/': gotoMainPage,
      '/book/:bookId': gotoBookPage
    });

    router.init('/');
  },

  render: function() {
    switch (this.state.nowShowing) {
      case Nav.MAIN_PAGE:
        return (<FavListsPage />);

      case Nav.BOOK_PAGE:
        return (<SingleBookPage bookId={this.state.bookId} />);

      default:
        return (<FavListsPage />);
    }
  }
});
