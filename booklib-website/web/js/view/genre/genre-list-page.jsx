var React = require('React');
var GenreList = require('./genre-list.js');

module.exports = React.createClass({
  getInitialState: function() {
    return {loading: true};
  },

  componentDidMount: function() {
    var promise = this.props.services.libService.getGenres();
    promise.then(function (genres) {
      this.setState({loading: false, genres: genres});
    }.bind(this));
  },

  render: function() {
    if (this.state.loading) {
      return (
        <div className="container">
          <p>Loading Genres List...</p>
        </div>
      );
    }
    return (<GenreList genres={this.state.genres.values} />);
  }
});
