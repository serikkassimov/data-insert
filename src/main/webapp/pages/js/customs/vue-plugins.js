WorldClassPlugins = {
	plugins: [
		/*
		function(Vue) {
			console.log('As function. Vue:', Vue);
		},
		{
			parameters: ['store', 'router'],
			install: function(Vue, store, router) {
				console.log(
					'As plugin info. Vue:',
					Vue,
					', store:',
					store,
					', router:',
					router
				);
			}
		}
		*/
	],
	install: function(Vue, store, router) {
		if (!isArray(this.plugins)) throw new Error('plugins is not array: ' + $.type(this.plugins));
		if (!isFunction(Vue)) throw new Error('parameter "Vue" is not object: ' + $.type(Vue));
		if (!isObject(store)) throw new Error('parameter "store" is not object: ' + $.type(store));
		if (!isObject(router)) throw new Error('parameter "router" is not object: ' + $.type(router));
	
		for (var index in this.plugins) {
			if (isObject(this.plugins[index])) {
				var install = this.plugins[index].install;
				if (!isFunction(install)) throw new Error(
					'plugins[' + index + '].install is not function: ' + $.type(this.plugins[index].install)
				);
	
				var parameters = [Vue];
				if (isDefined(this.plugins[index].parameters)) {
					if (isArray(this.plugins[index].parameters)) {
						for (var paramIndex in this.plugins[index].parameters) {
							var parameter = this.plugins[index].parameters[paramIndex];
							if (isNonEmptyString(parameter)) {
								var validParameters = ['Vue', 'store', 'router'];
								switch (validParameters.indexOf(parameter)) {
									case 0: // Vue
										parameters.push(Vue);
										break;
									case 1: // store
										parameters.push(store);
										break;
									case 2: // router
										parameters.push(router);
										break;
									default:
										throw new Error(
											'Invalid parameter "' + parameter + '" (plugins[' + index + '].parameters[' + paramIndex + ']), ' + 'valid parameters are ' + validParameters
										);
								}
							} else throw new Error(
								'plugins[' + index + '].parameters[' + paramIndex + '] is not non-empty string: "' + parameter + '" (type :' + $.type(parameter) + ')'
							);
						}
					} else throw new Error(
						'plugins[' + index + '].parameters is not array: ' + $.type(this.plugins[index].parameters)
					);
				}
	
				install.apply(window, parameters);
			} else if (isFunction(this.plugins[index])) {
				this.plugins[index](Vue);
			} else throw new Error(
				'plugins[' + index + '] is not object or function: ' + $.type(this.plugins[index])
			);
		}
	}
};