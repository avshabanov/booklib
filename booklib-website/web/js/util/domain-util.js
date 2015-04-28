/*
 * This module defines utility functions for working with the domain model.
 */

function selectFirst(list, fieldName, fieldValue, defaultValueProviderFn) {
  var i;

  if (defaultValueProviderFn == null) {
    defaultValueProviderFn = function () {
      throw new Error('No element in the list=' + list + ' with ' +
        fieldName + '=' + fieldValue);
    }
  }

  for (i = 0; i < list.length; ++i) {
    if (list[i][fieldName] == fieldValue) {
      return list[i];
    }
  }

  return defaultValueProviderFn();
}

function selectById(list, id) {
  return selectFirst(list, "id", id);
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

module.exports.selectFirst = selectFirst;
module.exports.selectById = selectById;
module.exports.selectByIds = selectByIds;
