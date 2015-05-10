var React = require('React');

module.exports = React.createClass({
  render: function() {
    return (
      <div className="container">
        <h3>Genre Detail Page</h3>
        <p>GenreId = {this.props.genreId}</p>
      </div>
    );
  }
});
