var React = require('React');

module.exports = React.createClass({
  getInitialState: function() {
    return {loading: true, books: []};
  },

  componentDidMount: function() {
    console.log("this.props.personId", this.props.personId);
    var promise = this.props.services.libService.getBooks({personIds: [this.props.personId]});
    promise.then(function (pageModel) {
      console.log("pageModel", pageModel);
      if (!this.isMounted()) { return; }
      var person = pageModel.persons[0];
      document.title = "Booklib \u00BB " + person.name;
      this.setState({loading: false, person: person});
    }.bind(this));
  },

  render: function() {
    if (this.state.loading) {
      return (
        <div className="container">
          <p>Loading Person Data...</p>
        </div>
      );
    }

    return (
      <div className="container">
        <h3>Person Detail Page</h3>
        <p>PersonId = {this.state.person.id}, PersonName = {this.state.person.name}</p>
      </div>
    );
  }
});

