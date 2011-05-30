dojo.provide("course.geo.FeatureContainer");

dojo.require("course.geo.Feature");

dojo.declare("course.geo.FeatureContainer", course.geo.Feature, {
	
	type: "FeatureContainer",
	
	features: null,
	
	constructor: function(featureDef, kwArgs) {
		if (this.features) {
			var features = this.features;
			this.features = [];
			this.addFeatures(features);
		}
		else this.features = [];
	},
	
	addFeatures: function(/* Array */features) {
		dojo.forEach(features, function(feature){
			if (feature.declaredClass) { // derived from course.geo.Feature
				feature.setMap(this.map);
				feature.setParent(this);
				this.features.push(feature);
			}
			else {
				if (!feature.type) feature.type = feature.features ? "FeatureContainer" : "Placemark";
				if (feature.type=="Feature") feature.type="Placemark";
				var ctor = course.geo.featureTypes[feature.type];
				if (ctor) {
					feature = new ctor(feature, {map: this.map, parent: this});
					this.features.push(feature);
				}
			}
			if (feature.declaredClass) {
				feature.setMap(this.map);
				feature.setParent(this);
				this.map.registerFeature(feature);
			}
		}, this);
	},
	
	getBbox: function() {
		var bbox = [Infinity,Infinity,-Infinity,-Infinity];
		dojo.forEach(this.features, function(feature){
			course.geo.util.bbox.extend(bbox, feature.getBbox());
		}, this);
		return bbox;
	},
	
	_render: function(stylingOnly, mode) {
		dojo.forEach(this.features, function(feature){
			feature._render(stylingOnly, mode);
		}, this);
	},
	
	getContainer: function() {
		if (!this.container) {
			this.container = this.map.engine.createContainer(this);
		}
		return this.container;
	},
	
	connectWithHandle: function(handle, /* String|Array? */events, /*Object|null*/ context, /*String|Function*/ method) {
		if (!this.features.length) return handle;
		events = dojo.isString(events) ? [events] : events;
		handle = handle || course.geo.util.getUniqueNumber();
		method = dojo.hitch(context, method);
		dojo.forEach(this.features, function(feature) {
			feature.connectWithHandle(handle, events, method);
		});
		return handle;
	},
	
	disconnect: function(handle) {
		if (!this.features.length) return;
		dojo.forEach(this.features, function(feature) {
			feature.disconnect(handle);
		});
	}
});

// register the constructor
course.geo.featureTypes["FeatureContainer"] = course.geo.FeatureContainer;
course.geo.featureTypes["FeatureCollection"] = course.geo.FeatureContainer;
