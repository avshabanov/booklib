//
// TODO: this file is very similar to person-detail-page.jsx, refactor common pieces
//

var React = require('React');
var BookList = require('../book/book-list.js');

function loadBookPage() {
  console.log("about to load langs...");
  var promise =  this.props.services.libService.queryBooks({
    languageId: this.props.languageId,
    offsetToken: null
  });

  promise = promise.then(function (queryBooksResult) {
    console.log("queryBooksResult", queryBooksResult);
    return this.props.services.libService.getBooks({bookIds: queryBooksResult.bookIds});
  }.bind(this));

  promise = promise.then(function (bookPage) {
    var books = this.state.books || [];
    books = books.concat(bookPage.books);
    this.setState({books: books});
  }.bind(this));
}

function fetchBooks(languageId) {
  var promise = this.props.services.libService.getBooks({languageIds: [languageId]});
  promise.then(function (pageModel) {
    if (!this.isMounted()) { return; }
    var language = pageModel.languages[0];
    document.title = "Booklib \u00BB " + language.name;
    this.setState({loading: false, language: language, books: null});

    // start another query - request books
    loadBookPage.call(this);
  }.bind(this));
}

module.exports = React.createClass({
  getInitialState: function() {
    return {loading: true};
  },

  componentDidMount: function() {
    fetchBooks.call(this, this.props.languageId);
  },

  componentWillReceiveProps: function(nextProps) {
    //if (nextProps.languageId == this.props.languageId) { return; }
    fetchBooks.call(this, nextProps.languageId);
  },

  render: function() {
    if (this.state.loading) {
      return (
        <div className="container">
          <p>Loading Language Data...</p>
        </div>
      );
    }

    var getBibliography = function () {
      var books = this.state.books;
      if (books == null) {
        // books not yet loaded
        return <div>Loading books written in this language...</div>;
      }

      if (books.length == 0) {
        // no books for this genre
        return (<div className='alert alert-warning' role='alert'>
          <strong>No books!</strong>&nbsp;There is no books written in this language
          </div>);
      }

      return (<div>
        <p>Books:</p>
        <BookList books={books} />
        </div>);
    }.bind(this);

    return (
      <div className="container">
        <h2>{this.state.language.name}</h2>
        <hr/>
        {getBibliography()}
      </div>
    );
  }
});
