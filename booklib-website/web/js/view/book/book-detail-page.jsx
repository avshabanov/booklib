var React = require('React');
var InlineNamedItem = require('../generic/inline-named-item.js');

function createPersonInlineItem(person) {
  return (<InlineNamedItem key={person.id} itemNavUrl={"#/person/" + person.id} item={person} />);
}

function createGenreInlineItem(genre) {
  return (<InlineNamedItem key={genre.id} itemNavUrl={"#/genre/" + genre.id} item={genre} />);
}

function renderBook(book) {
  var langUrl = "#/language/" + book.lang.id;
  return (
    <div className="container">
      <h2>{book.title}</h2>
      <hr/>
      <table className="book-info">
        <tbody>
          <tr>
            <td>ID:</td>
            <td>{book.id}</td>
          </tr>
          <tr>
            <td>Persons:</td>
            <td>{book.persons.map(createPersonInlineItem)}</td>
          </tr>
          <tr>
            <td>Genres:</td>
            <td>{book.genres.map(createGenreInlineItem)}</td>
          </tr>
          <tr>
            <td>Add Date:</td>
            <td>{book.addDate}</td>
          </tr>
          <tr>
            <td>Language:</td>
            <td><a href={langUrl}>{book.lang.name}</a></td>
          </tr>
        </tbody>
      </table>
      <hr/>
    </div>
  );
}

module.exports = React.createClass({
  getInitialState: function() {
    return {loading: true};
  },

  componentDidMount: function() {
    var promise = this.props.services.libService.getBooks({bookIds: [this.props.bookId]});
    promise.then(function (pageModel) {
      if (!this.isMounted()) { return; }
      var book = pageModel.books[0];
      document.title = "Booklib \u00BB " + book.title;
      this.setState({loading: false, book: book});
    }.bind(this));
  },

  render: function() {
    if (this.state.loading) {
      return (
        <div className="container">
          <p>Loading Book Data...</p>
        </div>
      );
    }

    return renderBook(this.state.book);
  }
});
