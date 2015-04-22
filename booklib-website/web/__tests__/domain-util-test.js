
jest.dontMock('../js/util/domain-util.js');

describe('selectById', function () {
  it('selects one element', function () {
    var s = require('../js/util/domain-util.js');
    var elements = [{id: 1, name: 'one'}, {id: 2, name: 'two'}];
    expect(s.selectById(elements, 2)).toBe(elements[1]);
  });

  it('selects one element', function () {
    var s = require('../js/util/domain-util.js');
    var elements = [{id: 1, name: 'one'}, {id: 2, name: 'two'}];
    expect(s.selectById(elements, 2)).toBe(elements[1]);
  });
});

