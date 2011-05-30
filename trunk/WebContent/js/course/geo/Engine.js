dojo.provide("course.geo.Engine");

dojo.declare("course.geo.Engine", null, {
	
	type: "gfx",
	
	factories: null,
	
	initialized: false,

	constructor: function(kwArgs){
		dojo.mixin(this, kwArgs);
		// find base module (e.g course.geo.gfx)
		this.baseModule = this.declaredClass.substring(0, this.declaredClass.lastIndexOf("."));
		this.factories = {};
	},
	
	initialize: function(/* Function */readyFunction) {
	},
	
	isInitialized: function() {
		return this.initialized;
	},
	
	createContainer: function(parentContainer, featureType) {

	},
	
	appendChild: function(parent, child) {
		
	},

	prerender: function() {

	},
	
	getFactory: function(type) {
		if (!this.factories[type]) {
			var lastDot = type.lastIndexOf(".");
			var module = lastDot>=0 ? type.substring(lastDot+1) : type;
			// type can have one of the following forms: 1)Placemark 2)control.Highlight
			// in the case 1) we try the type as is, in the case of 2) we try Highlight
			module = this.baseModule + "." + module;
			if (dojo.require(module)) this.factories[type] = new (dojo.getObject(module))({engine: this});
			else if (lastDot>0) {
				// in the case 2) we try control.Highlight, i.e. type as is
				module = this.baseModule + "." + type;
				if (dojo.require(module)) this.factories[type] = new (dojo.getObject(module));
			}
		}
		return this.factories[type];
	},
	
	getTopContainer: function() {
		
	},
	
	connect: function(group, event, context, method) {
	},
	
	patchMethods: function() {
		
	}
});