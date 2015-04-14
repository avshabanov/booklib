var React = require('React');
var BookList = require('../book/book-list.js');

module.exports = React.createClass({
  getInitialState: function() {
    return {loading: true};
  },

  componentDidMount: function() {
    var booksPromise = this.props.services.libService.getStorefrontPage();
    booksPromise.then(function (storefrontModel) {
      console.log("[StorefrontPage] Fetched Books... storefrontModel=" + JSON.stringify(storefrontModel));
      var favs = storefrontModel.favorites;
      this.setState({loading: false, books: favs.books, persons: favs.persons});
    }.bind(this));
  },

  render: function() {
    if (this.state.loading) {
      return (
        <div>
          <p>Loading Storefront Data...</p>
        </div>
      );
    }

    return (
      <div>
        <h2>Storefront Page</h2>
        <hr/>
        <BookList books={this.state.books} />
      </div>
    );
  }
});
