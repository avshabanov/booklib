var MainPage = React.createClass({
  getInitialState: function() {
    return {
      data: {
        books: []
      }
    };
  },

  componentDidMount: function() {
    var booksPromise = BookService.getFavoriteBooks();
    booksPromise.done(function (books) {
      this.setState({data: {books: books}});
    }.bind(this));
  },

  render: function() {
    return (
      <div>
        <h2>Book Library <small>demo</small></h2>

        <h3>Favorite Books</h3>
        <BookList data={this.state.data.books} />

        <h3>Favorite Authors</h3>
        <p>You don't have favorite authors yet.</p>
      </div>
    );
  }
});
