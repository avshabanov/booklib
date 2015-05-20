var React = require('React');
var CodedNamedItem = require('../generic/coded-named-item.js');

module.exports = React.createClass({
  render: function() {
    var genreNodes = this.props.genres.map(function (genre) {
      var url = "#/genre/" + genre.id;
      return (<CodedNamedItem key={genre.id} url={url} item={genre} />);
    });

    return (<p>{genreNodes}</p>);
  }
});
