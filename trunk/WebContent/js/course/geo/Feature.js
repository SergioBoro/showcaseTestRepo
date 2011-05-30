dojo.provide("course.geo.Feature");

dojo.require("course.geo.util");

course.geo.events = {onmouseover: true, onmouseout: true, onclick: true};

dojo.declare("course.geo.Feature", null, {
	
	id: null,
	bbox: null,
	style: null,
	parent: null,
	map: null,

	constructor: function(featureDef, kwArgs) {
		if (kwArgs) dojo.mixin(this, kwArgs);
		if (featureDef) dojo.mixin(this, featureDef);
		if (!this.id) this.id = "_geo_"+course.geo.util.getUniqueNumber();
		if (this.styleClass && !dojo.isArray(this.styleClass)) this.styleClass = [this.styleClass];
		if (featureDef && featureDef.style) {
			this.style = null;
			this.addStyle(featureDef.style);
		}
	},
	
	setMap: function(map) {
		this.map = map;
		if (this.styleClass) dojo.forEach(this.styleClass, function(_class){
			var featuresByClass = map._featuresByClass;
			if (!featuresByClass[_class]) featuresByClass[_class] = [];
			featuresByClass[_class].push(this);
		}, this);
	},
	
	setParent: function(parent) {
		this.parent = parent;
	},
	
	get: function(attr) {
		return this.map.attributesInFeature ? this[attr] : this.properties[attr];
	},
	
	addStyle: function(/* Array|Object */style) {
		if (!dojo.isArray(style)) style = [style];
		dojo.forEach(style, function(_style){
			var s = new course.geo.Style(_style, this.map);
			if (!s.styleClass && !s.fid) {
				s._features[this.id] = this;
				if (!this.style) this.style = [];
				this.style.push(s);
			}
		}, this);
	},
	
	render: function() {
		
	},
	
	setGroup: function(group) {
		this.group = group;
	},
	
	getBbox: function() {
		return this.bbox;
	},

	connect: function(/* String|Array? */events, /*Object|null*/ context, /*String|Function*/ method) {
		return this.connectWithHandle(null, events, context, method);
	},
	
	connectWithHandle: function(handle, /* String|Array? */events, /*Object|null*/ context, /*String|Function*/ method) {
		
	},
	
	disconnect: function(handle, keepHandlesEntry) {
		
	}
});


// registry of feature constructors
course.geo.featureTypes = {};
