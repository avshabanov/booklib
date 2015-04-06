var React = require('React');
var BookItem = require('./book-item.js');

module.exports = React.createClass({
  render: function() {
    return (
      <div className="container">
        <h2>Book List</h2>
        <BookItem />
        <BookItem />
      </div>
    );
  }
});

