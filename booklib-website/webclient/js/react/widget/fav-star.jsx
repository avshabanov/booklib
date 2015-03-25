var FavStar = React.createClass({
  render: function() {
    // -- > #if isFavorite fav /#if
    return (
      <a className="j-fav-link" href="#" item-id="{this.props.data.id}" item-type="{this.props.data.type}">
        <span className="star"><span className="glyphicon glyphicon glyphicon-star" aria-hidden="true"></span>&nbsp;Unstar</span>
        <span className="unstar"><span className="glyphicon glyphicon glyphicon-star-empty" aria-hidden="true"></span>&nbsp;Star</span>
      </a>
    );
  }
});
