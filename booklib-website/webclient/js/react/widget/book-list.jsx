var BookList = React.createClass({
  render: function() {
    var bookNodes = this.props.data.map(function (book) {
      return (<BookItem key={book.id} data={book} />);
    });
    return (
      <ul className="book-list">
        {bookNodes}
      </ul>
    );
  }
});
