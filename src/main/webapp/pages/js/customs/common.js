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