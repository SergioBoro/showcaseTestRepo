dojo.provide("course.geo.control.Base");

dojo.require("course.geo.util");

dojo.declare("course.geo.control.Base", null, {
	
	// array of features, featureContainers or the whole map
	targets: null,
	
	handle: null,
	
	enabled: true,

	constructor: function(map, kwArgs){
		
		this.map = map;

		if(!kwArgs) kwArgs = {};

		this.handle = course.geo.util.getUniqueNumber();
		
		// process targets
		// if targets kwArg is not specified all map features are considered as targets
		var targets = kwArgs.targets ? kwArgs.targets : [map];
		if (!dojo.isArray(targets)) targets = [targets];
		this.targets = [];
		dojo.forEach(targets, function(target){
			if (dojo.isString(target)) target = this.map.getFeatureById(target);
			this.targets.push(target);
		}, this);
		// default events to listen to
		this.events = ["onmouseover", "onmouseout"];
	},
	
	attachFactory: function() {
		var factory = this.map.engine.getFactory(this.factoryType);
		if (factory) {
			dojo.mixin(this, factory);
			this.init();
			if (this.enabled) this.enable();
		}
	},

	enable: function(){
		dojo.forEach(this.targets, function(target){
			target.connectWithHandle(this.handle, this.events, this, "process");
		}, this);
		this.enabled = true;
	},

	disable: function(){
		dojo.forEach(this.targets, function(target){
			target.disconnect(this.handle);
		}, this);
		this.enabled = false;
	},

	reset: function(){
		//	summary:
		//		Reset the action.
	},

	destroy: function(){
		this.disable();
	}
});

