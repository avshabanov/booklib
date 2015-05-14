var React = require('React');
var InlineNamedItem = require('../generic/inline-named-item.js');
var FavStar = require('../generic/fav-star.js');

function createPersonInlineItem(person) {
  return (<InlineNamedItem key={person.id} itemNavUrl={"#/person/" + person.id} item={person} />);
}

function createGenreInlineItem(genre) {
  return (<InlineNamedItem key={genre.id} itemNavUrl={"#/genre/" + genre.id} item={genre} />);
}

module.exports = React.createClass({
  render: function() {
    var isFavorite = true; // TODO: fetch...
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
              <FavStar id={this.props.book.id} type='BOOK' isFavorite={isFavorite}/>
            </div>
            <div className="col-md-5">{this.props.book.persons.map(createPersonInlineItem)}</div>
            <div className="col-md-5">{this.props.book.genres.map(createGenreInlineItem)}</div>
          </div>
        </div>
      </li>
    );
  }
});

