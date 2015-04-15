var React = require('React');

module.exports = React.createClass({
  render: function () {
    return (
      <span key={this.props.item.id} className="item-sep">
        <span className="prepended-divider">,&nbsp;</span>
        <a href={this.props.itemNavUrl} title={this.props.item.name}>{this.props.item.name}</a>
      </span>
    );
  }
});
