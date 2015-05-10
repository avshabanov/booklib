var React = require('React');

module.exports = React.createClass({
  render: function() {
    return (
      <div className="container">
        <h3>Author Detail Page</h3>
        <p>AuthorId = {this.props.authorId}</p>
      </div>
    );
  }
});
