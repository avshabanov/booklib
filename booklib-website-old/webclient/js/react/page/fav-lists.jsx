var app = app || {};

var FavListsPage = React.createClass({
  getInitialState: function() {
    return {books: [], authors: []};
  },

  componentDidMount: function() {
    var booksPromise = app.BookService.getFavoriteBooks();
    booksPromise.done(function (books) {
      this.setState({books: books});
    }.bind(this));
  },

  render: function() {
    return (
      <div>
        <h2>Book Library <small>demo</small></h2>

        <h3>Favorite Books</h3>
        <BookList data={this.state.books} />

        <h3>Favorite Authors</h3>
        <p>You don't have favorite authors yet.</p>
      </div>
    );
  }
});
