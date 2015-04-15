var React = require('React');

module.exports = React.createClass({
  getInitialState: function() {
    return {
      isFavorite: this.props.isFavorite
    };
  },

  handleClick: function () {
    var newFavStatus = true;
    if (this.state.isFavorite === true) {
      newFavStatus = false;
    }

    // TODO: service call
    var isFavorite = newFavStatus;
    this.setState({isFavorite: isFavorite});

//    var promise = app.BookService.setFavorite(this.props.id, this.props.type, newFavStatus);
//    promise.done(function (isFavorite) {
//      this.setState({isFavorite: isFavorite});
//    }.bind(this));
  },

  render: function () {
    var favLinkClass = "j-fav-link";
    if (this.state.isFavorite) {
      favLinkClass += " fav";
    }

    return (
      <a className={favLinkClass} href="#" onClick={this.handleClick}>
        <span className="star"><span className="glyphicon glyphicon glyphicon-star" aria-hidden="true"></span>&nbsp;Unstar</span>
        <span className="unstar"><span className="glyphicon glyphicon glyphicon-star-empty" aria-hidden="true"></span>&nbsp;Star</span>
      </a>
    );
  }
});
