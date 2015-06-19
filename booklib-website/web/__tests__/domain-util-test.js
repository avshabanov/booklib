
jest.dontMock('../js/util/domain-util.js');
var s = require('../js/util/domain-util.js');

var ELEMENTS = [{id: 1, name: 'one'}, {id: 2, name: 'two'}];

describe('select elements', function () {
  it('selects one element', function () {
    expect(s.selectById(ELEMENTS, 1)).toBe(ELEMENTS[0]);
  });

  it('selects one element', function () {
    expect(s.selectById(ELEMENTS, 2)).toBe(ELEMENTS[1]);
  });

  it('selects first elements', function () {
    expect(s.selectFirst(ELEMENTS, 1)).toBe(ELEMENTS[0]);
  });

  it('selects no elements', function () {
    expect(s.selectByIds(ELEMENTS, [])).toEqual([]);
  });

  it('selects two elements', function () {
    expect(s.selectByIds(ELEMENTS, [1, 2])).toEqual([ELEMENTS[0], ELEMENTS[1]]);
  });
});
