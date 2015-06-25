var React = require('React');

var FavStatus = {
  UNDECIDED,    // state fetching is in progress
  FAVORITE,     // this element is a favorite
  NOT_FAVORITE  // this element is not a favorite
};

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

    // TODO: service call (bulk?)
    var isFavorite = newFavStatus;
    this.setState({isFavorite: isFavorite});
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
