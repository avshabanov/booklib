var React = require('React');

module.exports = React.createClass({
  render: function () {
    return (
      <span className="named-value-elem">
        <a href={this.props.url}>
          <small>{this.props.item.id}.</small>&nbsp;<strong>{this.props.item.name}</strong>
        </a>
      </span>
    );
  }
});
