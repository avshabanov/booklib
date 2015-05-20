//
// TODO: this file is very similar to person-detail-page.jsx, refactor common pieces
//

var React = require('React');
var BookList = require('../book/book-list.js');

function loadBookPage() {
  var promise =  this.props.services.libService.queryBooks({
    genreId: this.props.genreId,
    offsetToken: null
  });

  promise = promise.then(function (queryBooksResult) {
    return this.props.services.libService.getBooks({bookIds: queryBooksResult.bookIds});
  }.bind(this));

  promise = promise.then(function (bookPage) {
    var books = this.state.books || [];
    books = books.concat(bookPage.books);
    this.setState({books: books});
  }.bind(this));
}

function fetchBooks(genreId) {
  var promise = this.props.services.libService.getBooks({genreIds: [genreId]});
  promise.then(function (pageModel) {
    if (!this.isMounted()) { return; }

    var genre = pageModel.genres[0];
    document.title = "Booklib \u00BB " + genre.name;
    this.setState({loading: false, genre: genre, books: null});

    // start another query - request books
    loadBookPage.call(this);
  }.bind(this));
}

module.exports = React.createClass({
  getInitialState: function() {
    return {loading: true};
  },

  componentDidMount: function() {
    fetchBooks.call(this, this.props.genreId);
  },

  componentWillReceiveProps: function(nextProps) {
    //if (nextProps.personId == this.props.personId) { return; }
    fetchBooks.call(this, nextProps.genreId);
  },

  render: function() {
    if (this.state.loading) {
      return (
        <div className="container">
          <p>Loading Genre Data...</p>
        </div>
      );
    }

    var getBibliography = function () {
      var books = this.state.books;
      if (books == null) {
        // books not yet loaded
        return <div>Loading books in this genre...</div>;
      }

      if (books.length == 0) {
        // no books for this genre
        return (<div className='alert alert-warning' role='alert'>
          <strong>No books!</strong>&nbsp;There is no books in this genre
          </div>);
      }

      return (<div>
        <p>Books:</p>
        <BookList books={books} />
        </div>);
    }.bind(this);

    return (
      <div className="container">
        <h2>{this.state.genre.name}</h2>
        <hr/>
        {getBibliography()}
      </div>
    );
  }
});
