WorldClassPlugins = {
	debug: false,
	plugins: [
		/*
		function(Vue) {
			console.log('As function. Vue:', Vue);
		},
		{
			parameters: ['store', 'router'],
			name: 'my plugin',
			dependencies: ['other plugin 1', 'other plugin 2'],
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

		var pluginInfos = {};
		var pluginQueue = [];
		var pluginInfo;
		var pluginIndex;
		var dependencyIndex;
		var dependencyName;
		var dependency;
	
		for (pluginIndex in this.plugins) {
			if (isObject(this.plugins[pluginIndex])) {
				var name = this.plugins[pluginIndex].name;
				if (!isNonEmptyString(name)) throw new Error(
					'this.plugins[' + pluginIndex + '].name is not non-empty string: "' + name + '" (type "' + $.type(name) + '")'
				);
				
				if (isObject(pluginInfos[name])) throw new Error(
					'plugin "' + name + '" already registered'
				);

				var install = this.plugins[pluginIndex].install;
				if (!isFunction(install)) throw new Error(
					'plugins[' + pluginIndex + '].install is not function: type "' + $.type(this.plugins[pluginIndex].install) + '"'
				);
	
				var parameters = [Vue];
				if (isDefined(this.plugins[pluginIndex].parameters)) {
					if (isArray(this.plugins[pluginIndex].parameters)) {
						for (var paramIndex in this.plugins[pluginIndex].parameters) {
							var parameter = this.plugins[pluginIndex].parameters[paramIndex];
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
											'Invalid parameter "' + parameter + '" (plugins[' + pluginIndex + '].parameters[' + paramIndex + ']), ' + 'valid parameters are ' + validParameters
										);
								}
							} else throw new Error(
								'plugins[' + pluginIndex + '].parameters[' + paramIndex + '] is not non-empty string: "' + parameter + '" (type "' + $.type(parameter) + '")'
							);
						}
					} else throw new Error(
						'plugins[' + pluginIndex + '].parameters is not array: type "' + $.type(this.plugins[pluginIndex].parameters) + '"'
					);
				}
				
				var dependencies = this.plugins[pluginIndex].dependencies;
				if (isDefined(dependencies)) {
					if (!isArray(dependencies)) throw new Error(
						'this.plugins[' + pluginIndex + '].dependencies is not array: type "' + $.type(dependencies) + '"'
					);
					for (dependencyIndex in dependencies) {
						dependencyName = dependencies[dependencyIndex];
						if (!isNonEmptyString(dependencyName)) throw new Error(
							'this.plugins[' + pluginIndex + '].dependencies[' + dependencyIndex + '] is not non-empty string: "' + dependencyName + '" (type "' + $.type + '")'
						);
					}
				}
	
				pluginInfo = {
					context: this.plugins[pluginIndex],
					name: name,
					install: install,
					parameters: parameters,
					dependencies: dependencies
				};

				pluginInfos[name] = pluginInfo;
			} else if (isFunction(this.plugins[pluginIndex])) {
				this.plugins[pluginIndex](Vue);
			} else throw new Error(
				'plugins[' + pluginIndex + '] is not object or function: type "' + $.type(this.plugins[pluginIndex]) + '"'
			);
		}

		var addPluginToQueue;

		addPluginToQueue = function(pluginInfo) {
			var pluginName = pluginInfo.name;
			if (pluginQueue.indexOf(pluginName) === -1) {
				if (pluginInfo.dependencies && (pluginInfo.dependencies.length > 0)) {
					for (var dependencyIndex in pluginInfo.dependencies) {
						var dependencyName = pluginInfo.dependencies[dependencyIndex];
						if (pluginQueue.indexOf(dependencyName) === -1) {
							var dependency = pluginInfos[dependencyName];
							if (isObject(dependency)) {
								addPluginToQueue.call(this, dependency);
								if (this.debug) console.log('plugin "' + pluginName + '": dependency "' + dependencyName + '" added');
							}
							else throw new Error(
								'cannot find plugin "' + dependencyName + '" (dependency of plugin "' + pluginName + '")'
							);
						} else if (this.debug) console.log('plugin "' + pluginName + '": dependency "' + dependencyName + '" skipped (already added)');
					}
				}
				pluginQueue.push(pluginInfo.name);
				if (this.debug) console.log('plugin "' + pluginName + '": added to queue');
			}
		};

		for (pluginIndex in pluginInfos) addPluginToQueue.call(this, pluginInfos[pluginIndex]);

		for (pluginIndex in pluginQueue) {
			var pluginName = pluginQueue[pluginIndex];
			pluginInfo = pluginInfos[pluginName];
			pluginInfo.install.apply(pluginInfo.context, pluginInfo.parameters);
			if (this.debug) console.log('plugin "' + pluginName + '": installed');
		}
	}
};