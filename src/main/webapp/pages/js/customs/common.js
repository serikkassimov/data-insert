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

isBoolean = function(object) {
	return jQuery.type(object) === typeBoolean;
};

isNumber = function(object) {
	return jQuery.type(object) === typeNumber;
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