var React = require('React');

module.exports = React.createClass({
  render: function() {
    return (
      <div className="container">
        <h3>Book Detail Page</h3>
        <p>BookId = ${this.props.bookId}</p>
      </div>
    );
  }
});

