var React = require('React');

module.exports = React.createClass({
  render: function() {
    return (
      <div className="container">
        <h3>Language Detail Page</h3>
        <p>LanguageId = {this.props.languageId}</p>
      </div>
    );
  }
});
