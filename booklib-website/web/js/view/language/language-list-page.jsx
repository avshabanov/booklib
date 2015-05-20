var React = require('React');
var LanguageList = require('./language-list.js');

module.exports = React.createClass({
  getInitialState: function() {
    return {loading: true};
  },

  componentDidMount: function() {
    var promise = this.props.services.libService.getLanguages();
    promise.then(function (languages) {
      this.setState({loading: false, languages: languages});
    }.bind(this));
  },

  render: function() {
    if (this.state.loading) {
      return (
        <div className="container">
          <p>Loading Languages List...</p>
        </div>
      );
    }
    return (<LanguageList languages={this.state.languages.values} />);
  }
});
