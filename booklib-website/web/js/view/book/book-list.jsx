var React = require('React');
var BookItem = require('./book-item.js');

module.exports = React.createClass({
  render: function() {
    var bookNodes = this.props.books.map(function (book) {
      return (<BookItem key={book.id} book={book} />);
    });
    return (
      <ul className="book-list">
        {bookNodes}
      </ul>
    );
  }
});

