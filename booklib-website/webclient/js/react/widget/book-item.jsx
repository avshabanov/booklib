var BookItem = React.createClass({
  render: function() {
    return (
      <li>
        <div className="container">
          <div className="row">
            <div className="col-md-12">
              <h3><small>{this.props.book.id}</small>&nbsp;<a href={'#/book/' + this.props.book.id}
                title={this.props.book.title}>{this.props.book.title}</a></h3>
            </div>
          </div>
          <div className="row">
            <div className="col-md-2">
              <FavStar id={this.props.book.id} type='BOOK' isFavorite={this.props.book.isFavorite}/>
            </div>
            <div className="col-md-5">{this.props.book.authors.map(createAuthorInlineItem)}</div>
            <div className="col-md-5">{this.props.book.genres.map(createGenreInlineItem)}</div>
          </div>
        </div>
      </li>
    );
  }
});
