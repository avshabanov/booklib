var React = require('React');
var PersonList = require('./person-list.js');

function updatePrefix(prefix) {
  var promise;

  if (prefix != null && prefix.length > 2) {
    // enough letters, we can request the whole names
    promise = this.props.services.libService.getPersons(prefix); // TODO: offsetToken
    promise.then(function (namedValueList) {
      if (!this.isMounted()) { return; }
      this.setState({loading: false, personList: namedValueList});
    }.bind(this));
    return;
  }

  promise = this.props.services.libService.getPersonHints(prefix);
  promise.then(function (parts) {
    if (!this.isMounted()) { return; }
    this.setState({loading: false, nameParts: parts});
  }.bind(this));
}

module.exports = React.createClass({
  getInitialState: function() {
    return {loading: true, nameParts: null, personList: null};
  },

  componentDidMount: function() {
    updatePrefix.call(this, this.props.prefix);
  },

  componentWillReceiveProps: function(nextProps) {
    // the state needs to somehow indicate that we no longer have personList
    this.setState({personList: null, nameParts: []});
    updatePrefix.call(this, nextProps.prefix);
  },

  render: function() {
    //console.log("[render] state", this.state);
    if (this.state.loading) {
      return (<p>Loading person name hints...</p>);
    }

    if (this.state.personList != null) {
      // ok, we have person list - it takes precedence over name parts
      return (<PersonList persons={this.state.personList.values} />)
    }

    var nameParts = this.state.nameParts.map(function (part) {
      var personUrl = "#/persons/prefix/" + encodeURIComponent(part);
      return (<span key={part} className="named-value-elem"><a href={personUrl}><strong>{part}</strong>&nbsp;<small>&hellip;</small></a></span>);
    });

    return (<p>{nameParts}</p>);
  }
});

