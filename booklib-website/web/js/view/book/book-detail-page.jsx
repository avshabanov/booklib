var React = require('React');
var InlineNamedItem = require('../generic/inline-named-item.js');

function createPersonInlineItem(person) {
  return (<InlineNamedItem key={person.id} itemNavUrl={"#/person/" + person.id} item={person} />);
}

function createGenreInlineItem(genre) {
  return (<InlineNamedItem key={genre.id} itemNavUrl={"#/genre/" + genre.id} item={genre} />);
}

function renderBook(book) {
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
            <td><a href="#/g/language/{book.lang.id}">{book.lang.name}</a></td>
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
    var bookPromise = this.props.services.libService.getBookPage(this.props.bookId);
    bookPromise.then(function (pageModel) {
      if (!this.isMounted()) { return; } // ignore

      this.setState({loading: false, book: pageModel.book});
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

/*
    <h2>${book.meta.title}</h2>

    <hr/>

    <table className="book-info">
      <tbody>
        <tr>
          <td>ID:</td>
          <td>${book.meta.id?c}</td>
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
          <td><a href="/g/language/{book.landId}">${book.meta.lang.name?html}</a></td>
        </tr>
        <tr>
          <td>Origin:</td>
          <td>${book.meta.origin?html}</td>
        </tr>
      </tbody>
    </table>

    <hr/>

    <h3><a href="/g/book/{book.id}/download">Download&nbsp;<span class="glyphicon glyphicon-download" aria-hidden="true"></span></a></h3>
*/