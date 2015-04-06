var React = require('React');

module.exports = React.createClass({
  render: function() {
    return (
      <li>
        <span>Book: id={this.props.book.id} title={this.props.book.title}</span>
      </li>
    );
  }
});

