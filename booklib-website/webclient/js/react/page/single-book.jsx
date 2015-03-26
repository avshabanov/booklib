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
              <td>
                <small>TODO: authors</small>
              </td>
            </tr>
            <tr>
              <td>Genres:</td>
              <td>
                <small>TODO: genres</small>
              </td>
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

  /*
  <div>
          <h2>{book.title}</h2>
          <hr/>
          <table class="book-info">
            <tbody>
              <tr>
                <td>ID:</td>
                <td>{book.id}</td>
              </tr>
              <tr>
                <td>Authors:</td>
                <td>
                  <#list book.authors as author>
                    <a href="/g/author/${author.id?c}" title="${author.name?html}">${author.name?html}</a><#if author_has_next>,&nbsp</#if>
                  </#list>
                </td>
              </tr>
              <tr>
                <td>Genres:</td>
                <td>
                  <#list book.genres as genre>
                    <a href="/g/genre/${genre.id?c}" title="${genre.name?html}">${genre.name}</a><#if genre_has_next>,&nbsp</#if>
                  </#list>
                </td>
              </tr>
              <tr>
                <td>File Size:</td>
                <td>${book.meta.fileSize?c} byte(s)</td>
              </tr>
              <tr>
                <td>Add Date:</td>
                <td>${book.meta.addDate}</td>
              </tr>
              <tr>
                <td>Language:</td>
                <td><a href="/g/language/${book.meta.lang.id?c}">${book.meta.lang.name?html}</a></td>
              </tr>
              <tr>
                <td>Origin:</td>
                <td>${book.meta.origin?html}</td>
              </tr>
            </tbody>
          </table>

          <hr/>

          <h3><a href="/g/book/${book.meta.id?c}/download">Download&nbsp;<span class="glyphicon glyphicon-download" aria-hidden="true"></span></a></h3>
        </div>

  */
});
