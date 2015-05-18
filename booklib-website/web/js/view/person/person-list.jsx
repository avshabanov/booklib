var React = require('React');
var CodedNamedItem = require('../generic/coded-named-item.js');

module.exports = React.createClass({
  render: function() {
    var personNodes = this.props.persons.map(function (person) {
      var url = "#/person/" + person.id;
      return (<CodedNamedItem key={person.id} url={url} item={person} />);
    });

    return (<p>{personNodes}</p>);
  }
});

