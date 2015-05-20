var React = require('React');
var FavStar = require('../generic/fav-star.js');
var BookList = require('../book/book-list.js');

function loadBookPage() {
  var promise =  this.props.services.libService.queryBooks({
    personId: this.props.personId,
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

function fetchBooks(personId) {
  var promise = this.props.services.libService.getBooks({personIds: [personId]});
  promise.then(function (pageModel) {
    if (!this.isMounted()) { return; }

    var person = pageModel.persons[0];
    document.title = "Booklib \u00BB " + person.name;
    this.setState({loading: false, person: person, books: null});

    // start another query - request books
    loadBookPage.call(this);
  }.bind(this));
}

//
// Class definition
//

module.exports = React.createClass({
  getInitialState: function() {
    return {loading: true};
  },

  componentDidMount: function() {
    fetchBooks.call(this, this.props.personId);
  },

  componentWillReceiveProps: function(nextProps) {
    //if (nextProps.personId == this.props.personId) { return; }
    fetchBooks.call(this, nextProps.personId);
  },

  render: function() {
    if (this.state.loading) {
      return (
        <div className="container">
          <p>Loading Person Data...</p>
        </div>
      );
    }

    var getBibliography = function () {
      var books = this.state.books;
      if (books == null) {
        // books not yet loaded
        return <div>Loading bibliography...</div>;
      }

      if (books.length == 0) {
        // no books for this author
        return (<div className='alert alert-warning' role='alert'>
          <strong>No books!</strong>&nbsp;This author does not have any books in his bibliography
          </div>);
      }

      return (<div>
        <p>Bibliography:</p>
        <BookList books={books} />
        </div>);
    }.bind(this);

    return (
      <div className="container">
        <h2>{this.state.person.name}</h2>
        <FavStar id={this.props.personId} type='PERSON' isFavorite={false}/>
        <hr/>
        {getBibliography()}
      </div>
    );
  }
});

