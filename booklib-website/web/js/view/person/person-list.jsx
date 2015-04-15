var React = require('React');
var PersonItem = require('./person-item.js');

module.exports = React.createClass({
  render: function() {
    var personNodes = this.props.persons.map(function (person) {
      return (<PersonItem key={person.id} person={person} />);
    });
    return (
      <ul className="person-list">
        {personNodes}
      </ul>
    );
  }
});

