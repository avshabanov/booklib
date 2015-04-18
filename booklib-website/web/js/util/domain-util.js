/*
 * This module defines utility functions for working with the domain model.
 */


function selectById(list, id) {
  var i;

  for (i = 0; i < list.length; ++i) {
    if (list[i].id == id) {
      return list[i];
    }
  }

  throw new Error('No element in the list=' + list + ' with id=' + id);
}

function selectByIds(itemList, idList) {
  var result = [];
  for (i = 0; i < idList.length; ++i) {
    result.push(selectById(itemList, idList[i]));
  }
  return result;
}


//
// Exports
//

module.exports.selectById = selectById;
module.exports.selectByIds = selectByIds;
