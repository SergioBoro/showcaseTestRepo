dojo.provide("course.geo.ge.Engine");

dojo.require("course.geo.Engine");

if (!course.geo.ge._googleLoaded) {
	google.load("earth", "1");
	course.geo.ge._googleLoaded = true;
}

(function(){
	
// default methods;
var e = course.geo.ge;

var engineEvents = {onmouseover: "mouseover", onmouseout: "mouseout", onclick: "click"}

e.methods = {
	Map: {
		addStyle: function(styles) {
			google.earth.executeBatch(this.engine.ge, dojo.hitch(this, function(){
				this._addStyle(styles);
			}));
		},
		
		render: function(stylingOnly, mode, features) {
			google.earth.executeBatch(this.engine.ge, dojo.hitch(this, function(){
				this._render(stylingOnly, mode, features);
			}));
		}
	}
}

dojo.declare("course.geo.ge.Engine", course.geo.Engine, {
	
	type: "ge",

	// the result of google.earth.createInstance
	ge: null,
	
	constructor: function(kwArgs) {
	},
	
	initialize: function(/* Function */readyFunction) {
		google.earth.createInstance(this.map.container, dojo.hitch(this, function(instance){
			this.map.currentProjection = "EPSG:4326";
			this.ge = instance;
			this.ge.getWindow().setVisibility(true);
			
			this.patchMethods();
			
			// initialize some factories
			this.factories["Placemark"] = new course.geo.ge.Placemark({ge:this.ge, engine:this});
			
			this.initialized = true;
			readyFunction();
		}), function(){/*failure*/});
	},

	createContainer: function(feature) {
		var container = this.ge.createFolder('');
		this.appendChild(container, feature);
		return container;
	},
	
	prerender: function() {
		this._zoomTo( this.map.getBbox() );
	},
	
	appendChild: function(child, feature) {
		var parentContainer = feature.parent.getContainer();
		parentContainer.getFeatures().appendChild(child);
	},
	
	getTopContainer: function() {
		var features = this.ge.getFeatures();
		return this.ge;
	},
	
	patchMethods: function() {
		dojo.mixin(this.map.methods, e.methods);
	},
	
	connect: function(feature, event, method) {
		var connections = [];
		dojo.forEach(feature.baseShapes, function(placemark){
			event = engineEvents[event];
			google.earth.addEventListener(placemark, event, method);
			connections.push([placemark, event, method]);
		});
		return connections;
	},
	
	disconnect: function(connections) {
		dojo.forEach(connections, function(connection){
			google.earth.removeEventListener(/* placemark */connection[0], /* engineEvent */connection[1], /* method */connection[2]);
		});
	},
	
	_zoomTo: function(extent) {
		// The following code is derived from earth-api-utility-library (http://code.google.com/p/earth-api-utility-library/)
		// The earth-api-utility-library is licensed under the Apache License, Version 2.0

		var lookAtRange = 1000, // the default lookat range to use when creating a view for a degenerate, single-point extent
			scaleRange = 1.5,
			aspectRatio = this.map.width/this.map.height;
			centerX = (extent[0]+extent[2])/2,
			centerY = (extent[1]+extent[3])/2,
			extentWidth = extent[2] - extent[0],
			extentHeight = extent[3] - extent[1];
		
		if (extentWidth || extentHeight) {
			var distEW = distance([extent[2], centerY], [extent[0], centerY]);
			var distNS = distance([centerX, extent[3]], [centerX, extent[1]]);
			aspectRatio = Math.min(Math.max(aspectRatio, distEW/distNS), 1.0);
			
			//experimentally derived distance formula
			var alpha = toRadians(45.0 / (aspectRatio + 0.4) - 2.0),
				expandToDistance = Math.max(distNS, distEW),
				beta = Math.min(toRadians(90), alpha + expandToDistance/(2*EARTH_RADIUS));
			lookAtRange = scaleRange * EARTH_RADIUS * (Math.sin(beta) * Math.sqrt(1 + 1 / Math.pow(Math.tan(alpha), 2)) - 1);
		}
		
		// get the current view
		var lookAt = this.ge.getView().copyAsLookAt(this.ge.ALTITUDE_RELATIVE_TO_GROUND);
		// set the position values
		lookAt.setLongitude(centerX);
		lookAt.setLatitude(centerY);
		lookAt.setAltitude(0);
		lookAt.setRange(lookAtRange);

		// update the view in Google Earth
		this.ge.getView().setAbstractView(lookAt);
	}
});

var EARTH_RADIUS = 6378135;

var toRadians = function(value) {
	return value*Math.PI/180;
};

var distance = function(point1, point2) {
	// The following code is derived from geojs library (http://code.google.com/p/geojs/)
	// The geojs library is licensed under the Apache License, Version 2.0
	
	// calculate angular distance between two points using the Haversine formula
	var phi1 = toRadians(point1[1]),
		phi2 = toRadians(point2[1]),
		d_phi = toRadians(point2[1] - point1[1]),
		d_lmd = toRadians(point2[0] - point1[0]),
		A = Math.pow(Math.sin(d_phi/2), 2) + Math.cos(phi1) * Math.cos(phi2) * Math.pow(Math.sin(d_lmd/2), 2),
		angularDistance = 2 * Math.atan2(Math.sqrt(A), Math.sqrt(1 - A));
	return EARTH_RADIUS * angularDistance;
};

}());

dojo.require("course.geo.ge.Placemark");