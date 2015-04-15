var React = require('React');

module.exports = React.createClass({
  render: function() {
    return (
      <li>
        <span>Person: id={this.props.person.id} name={this.props.person.name}</span>
      </li>
    );
  }
});

