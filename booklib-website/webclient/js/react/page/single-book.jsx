var app = app || {};

var SingleBookPage = React.createClass({
  getInitialState: function() {
    return {book: null};
  },

  componentDidMount: function() {
    var promise = app.BookService.getBookById(this.props.bookId);
    promise.done(function (book) {
      this.setState({book: book});
    }.bind(this));
  },

  render: function() {
    var book = this.state.book;

    if (book == null) {
      return (
        <div>
          <p>Loading...</p>
        </div>
      );
    }

    return (
      <div>
        <h2>{book.title}</h2>
        <hr/>
        <table className="book-info">
          <tbody>
            <tr>
              <td>ID:</td>
              <td>{book.id}</td>
            </tr>
            <tr>
              <td>Authors:</td>
              <td>{book.authors.map(createAuthorInlineItem)}</td>
            </tr>
            <tr>
              <td>Genres:</td>
              <td>{book.genres.map(createGenreInlineItem)}</td>
            </tr>
            <tr>
              <td>File Size:</td>
              <td>{book.fileSize} byte(s)</td>
            </tr>
            <tr>
              <td>Add Date:</td>
              <td>{book.addDate}</td>
            </tr>
            <tr>
              <td>Language:</td>
              <td><a href="#/language/${book.lang.id}">{book.lang.name}</a></td>
            </tr>
            <tr>
              <td>Origin:</td>
              <td>{book.origin.name}</td>
            </tr>
          </tbody>
        </table>

        <hr/>

        <h3><a href="#">Download&nbsp;<span className="glyphicon glyphicon-download" aria-hidden="true"></span></a></h3>
      </div>
    );
  }
});
