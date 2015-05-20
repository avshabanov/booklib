var React = require('React');
var CodedNamedItem = require('../generic/coded-named-item.js');

module.exports = React.createClass({
  render: function() {
    var languageNodes = this.props.languages.map(function (language) {
      var url = "#/language/" + language.id;
      return (<CodedNamedItem key={language.id} url={url} item={language} />);
    });

    return (<p>{languageNodes}</p>);
  }
});
