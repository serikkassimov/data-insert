var typeUndefined = 'undefined';
var typeNull = 'null';
var typeBoolean = 'boolean';
var typeNumber = 'number';
var typeString = 'string';
var typeDate = 'date';
var typeFunction = 'function';
var typeArray = 'array';
var typeError = 'error';
var typeSymbol = 'symbol';
var typeRegexp = 'regexp';
var typeObject = 'object';

isUndefined = function(object) {
	return jQuery.type(object) === typeUndefined;
};

isDefined = function(object) {
	return !isUndefined(object);
};

isNull = function(object) {
	return jQuery.type(object) === typeNull;
};

isNullOrUndefined = function(object) {
	return isUndefined(object) || isNull(object);
};

isDefinedNonNull = function(object) {
	return (!isUndefined(object)) && (!isNull(object));
};

isBoolean = function(object) {
	return jQuery.type(object) === typeBoolean;
};

isNumber = function(object) {
	return jQuery.type(object) === typeNumber;
};

isNanNumber = function(object) {
	return isNumber(object) && Number.isNaN(object);
};

isFiniteNumber = function(object) {
	return isNumber(object) && (!isNanNumber(object)) && Number.isFinite(object);
};

isComparableNumber = function(object) {
	return isNumber(object) && isFiniteNumber(object);
};

isString = function(object) {
	return jQuery.type(object) === typeString;
};

isNonEmptyString = function(object) {
	return isString(object) && (object.length > 0);
};

isDate = function(object) {
	return jQuery.type(object) === typeDate;
};

isFunction = function(object) {
	return jQuery.type(object) === typeFunction;
};

isArray = function(object) {
	return jQuery.type(object) === typeArray;
};

isEmptyArray = function(object) {
	return isArray(object) && (object.length === 0);
};

isNonEmptyArray = function(object) {
	return isArray(object) && (object.length > 0);
};

isError = function(object) {
	return jQuery.type(object) === typeError;
};

isSymbol = function(object) {
	return jQuery.type(object) === typeSymbol;
};

isRegexp = function(object) {
	return jQuery.type(object) === typeRegexp;
};

isObject = function(object) {
	return jQuery.type(object) === typeObject;
};

equals = function(object1, object2) {
	if (jQuery.type(object1) !== jQuery.type(object2)) return false;
	else if (isEmptyArray(object1)) return isEmptyArray(object2);
	else if (isNonEmptyArray(object1) || isObject(object2)) {
		var checkedIndices = [];
		var index;
		for (index in object1) {
			if (!equals(object1[index], object2[index])) return false;
			checkedIndices.push(index);
		}
		for (index in object2) {
			if (checkedIndices.indexOf(index) === -1) return false;
		}
		return true;
	} else return object1 === object2;
};

sort = function(comparator, items) {
	if (!isFunction(comparator)) throw new Error('comparator is not function (comparator expected)');
	if (!isArray(items)) throw new Error('items are not array');

	if (items.length > 1) {
		for (var i = 0; i < items.length - 1; i++) {
			for (var j = i + 1; j < items.length; j++) {
				var compareResult = comparator(items[i], items[j]);
				if (!isNumber(compareResult)) throw new Error('compare result must be number, but object of type ' + $.type(compareResult) + ' returned');

				if (compareResult > 0) {
					var temp = items[i];
					items[i] = items[j];
					items[j] = temp;
				}
			}
		}
	}
	return items;
};

stringComparator = function(string1, string2) {
	if (isString(string1)) {
		if (isString(string2)) {
			if (string1 < string2) return -1;
			else if (string1 > string2) return 1;
			else return 0;
		} else return 1;
	} else {
		if (isString(string2)) return -1;
		else return 0;
	}
};

numberComparator = function(number1, number2) {
	if (isNumber(number1)) {
		if (isNumber(number2)) return number1 - number2;
		else return 1;
	} else {
		if (isNumber(number2)) return - 1;
		else return 0;
	}
};

dateComparator = function(date1, date2) {
	if (isDate(date1)) {
		if (isDate(date2)) return numberComparator(date1.getTime(), date2.getTime());
		else return 1;
	} else {
		if (isDate(date2)) return -1;
		else return 0;
	}
};

booleanComparator = function(boolean1, boolean2) {
	if (isBoolean(boolean1)) {
		if (isBoolean(boolean2)) return numberComparator((boolean1 ? 1 : 0), (boolean2 ? 1 : 0));
		else return 1;
	} else {
		if (isBoolean(boolean2)) return -1;
		else return 0;
	}
};

var walkElementTreeContinue = 0;
var walkElementTreeTerminate = walkElementTreeContinue + 1;
var walkElementTreeSkipSubtree = walkElementTreeTerminate + 1;
var walkElementTreeSkipSiblings = walkElementTreeSkipSubtree + 1;
walkElementTree = function(parent, consumer, parentData) {
	if (!isFunction(consumer)) throw new Error('consumer is not function');

	/*
	data = {
		parent: {},
		index: 0,
		root: {},
		level: 0,
		objectPath: [],
		indexPath: []
	}
	*/

	if (!isObject(parentData)) parentData = {};

	if (((!isObject(parentData.root)) && (!isArray(parentData.root))) || ((!isObject(parentData.parent)) && (!isArray(parentData.parent)))) {
		parentData.parent = undefined;
		parentData.index = undefined;
		parentData.root = parent;
		parentData.level = 0;
		parentData.objectPath = [parent];
		parentData.indexPath = [];
	} else {
		if ((!isComparableNumber(parentData.index)) && (!isNonEmptyString(parentData.index))) parentData.index = undefined;
		if ((!isComparableNumber(parentData.level)) || (parentData.level < 0)) parentData.level = 0;
	
		if (!isArray(parentData.objectPath)) parentData.objectPath = [parent];
		if (parentData.objectPath[parentData.objectPath.length - 1] !== parent) parentData.objectPath.push(parent);
	
		if (!isArray(parentData.indexPath)) parentData.indexPath = [];
	}

	parentData.item = parent;

	var consumerResult = consumer(parentData);
	if (consumerResult === walkElementTreeContinue) ;
	else if (consumerResult === walkElementTreeTerminate) return walkElementTreeTerminate;
	else if (consumerResult === walkElementTreeSkipSubtree) return walkElementTreeContinue;
	else if (consumerResult === walkElementTreeSkipSiblings) return walkElementTreeSkipSiblings;
	else throw new Error('unknown consumer result ' + consumerResult);

	if (isArray(parent) || isObject(parent)) {
		for (var index in parent) {
			var child = parent[index];
			var childData = $.extend({}, parentData);
			childData.parent = parent;
			childData.index = index;
			childData.level += 1;
			childData.objectPath = $.extend([], childData.objectPath);
			childData.objectPath.push(child);
			childData.indexPath = $.extend([], childData.indexPath);
			childData.indexPath.push(index);

			var childResult = walkElementTree(child, consumer, childData);
			if (childResult === walkElementTreeContinue) ;
			else if (childResult === walkElementTreeTerminate) return walkElementTreeTerminate;
			else if (childResult === walkElementTreeSkipSiblings) return walkElementTreeContinue;
			else throw new Error('unknown child walk result ' + childResult);
		}
	}

	return walkElementTreeContinue;
};
walkElementTree.CONTINUE = walkElementTreeContinue;
walkElementTree.TERMINATE = walkElementTreeTerminate;
walkElementTree.SKIP_SUBTREE = walkElementTreeSkipSubtree;
walkElementTree.SKIP_SIBLINGS = walkElementTreeSkipSiblings;

isForbidden = function(route, account) {
	var result = false;

	if (account.restrictRoutes) {
		var meta = route.meta;
		if (isObject(meta) && meta.requiresAuthorization) {
			if (account.anonymousUser || (!account.accountNonExpired) || (!account.accountNonLocked) || (!account.credentialsNonExpired) || (!account.enabled)) result = true;
			else if (isNonEmptyArray(meta.requiredRoles)) {
				if (isNonEmptyArray(account.authorities)) {
					result = true;
					for (var index in account.authorities) {
						var authority = account.authorities[index];
						if (isObject(authority)) {
							var indexInRequired = meta.requiredRoles.indexOf(authority.name);
							if (indexInRequired !== -1) {
								result = false;
								break;
							}
						}
					}
				} else result = true;
			}
		}
	}

	return result;
};