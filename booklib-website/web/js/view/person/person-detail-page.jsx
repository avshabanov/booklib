var React = require('React');

module.exports = React.createClass({
  render: function() {
    return (
      <div className="container">
        <h3>Person Detail Page</h3>
        <p>PersonId = {this.props.personId}</p>
      </div>
    );
  }
});

