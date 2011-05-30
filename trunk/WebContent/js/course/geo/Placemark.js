dojo.provide("course.geo.Placemark");

dojo.require("course.geo.Feature");
dojo.require("course.geo.styling");

(function() {

var g = course.geo,
	u = g.util,
	s = g.styling;
	
var geometryTypes = {Point: 1, LineString: 1, Polygon: 1, MultiLineString: 1, MultiPolygon: 1};

dojo.declare("course.geo.Placemark", course.geo.Feature, {
	
	type: "Placemark",
	
	// keep active handles
	handles: null,
	
	baseShapes: null,

	constructor: function(/* Object? */featureDef, /* Object? */kwArgs) {
		this.handles = {};
		this.baseShapes = [];
	},
	
	getGeometry: function() {
		return this._getGeometry();
	},

	_getGeometry: function() {
		var geometry = this.geometry;
		if (!geometry) {
			var geometryId = this.geometryId || this.id;
			if (geometryId) geometry = this.map.getGeometryById(geometryId);
		}
		return geometry;
	},
	
	render: function(stylingOnly, mode) {
		this.map.methods.Placemark.render.call(this, stylingOnly, mode);
	},

	_render: function(stylingOnly, mode) {
		//TODO: disconnect connections and then reconnect them
		var geometry = this.getGeometry();
		if (!geometry || !geometry.coordinates || !(geometry.type in geometryTypes)) {
			this.invalid = true;
			return;
		}
		
		// check if have factory for the Placemark
		var factory = this.map.engine.getFactory(this.type);
		if (!factory) return;

		// TODO: disconnect
		if (!stylingOnly) {
			// destroy base shapes
			for (var i=this.baseShapes.length-1; i>=0; i--) {
				this.map.engine.destroy(this.baseShapes.pop(), this);
			}
		}
		// destroy extra shapes
		// extra shapes are destroyed in an case (also if stylingOnly == true)
		if (this.extraShapes) for (var i=this.extraShapes.length-1; i>=0; i--) {
			this.map.engine.destroy(this.extraShapes.pop(), this);
		}
		
		var style = s.calculateStyle(this, mode);
		/*
		var pointStyle = s.calculatePointStyle(this),
			textStyle = s.calculateTextStyle(this),
			lineStyle,
			polygonStyle;
		
		var gType = geometry.type,
			isPoint = (gType == "Point" || gType == "MultiPoint");
			isLine = (gType == "LineString" || gType == "MultiLineString");
			isPolygon = (gType == "Polygon" || gType == "MultiPolygon");

		if (isLine || isPolygon) {
			lineStyle = s.calculateLineStyle();
			polygonStyle = s.calculatePolygonStyle();
		}
		*/

		// apply style to the base geometry
		var styleType = "point";
		if (geometry.type == "Polygon" || geometry.type == "MultiPolygon") styleType = "polygon";
		else if (geometry.type == "LineString" || geometry.type == "MultiLineString") styleType = "line";
		if (stylingOnly) {
			applyStyle(styleType, this, geometry, style, factory);
		}
		else {
			// create shape(s)
			if (style[styleType]) { // we have a specific style
				dojo.forEach(style[styleType], function(_style) {
					var shape = factory.createShape(this, geometry);
					if (shape) this.baseShapes.push(shape);
				}, this);
			}
			else {
				var shape = factory.createShape(this, geometry);
				if (shape) this.baseShapes.push(shape);
			}
			
			// apply style to the shape(s)
			applyStyle(styleType, this, geometry, style, factory);
			
			// add shape(s) to the map
			if (style[styleType]) { // we have a specific style
				dojo.forEach(this.baseShapes, function(shape) {
					this.map.engine.appendChild(shape, this);
				}, this);
			}
			else this.map.engine.appendChild(this.baseShapes[0], this);
		}
		
		// render extra geometry if it is defined in a style
		// point geometry can only have base geometry
		/*
		if (!isPoint) {
			if (lineStyle) {
				dojo.forEach(lineStyle, function(_style) {
					var extraGeometry = s.getStyleGeometry(_style, geometry);
					if (extraGeometry) {
						var shape = this._createShape(extraGeometry, factory);
						s.applyLineStyle(shape, this, _style, factory);
						this.map.engine.appendChild(shape, this, _style.zIndex);
						this.extraShapes.push(shape);
					}
				}, this);
			}
			if (polygonStyle) {
				dojo.forEach(polygonStyle, function(_style) {
					var extraGeometry = s.getStyleGeometry(_style, geometry);
					if (extraGeometry) {
						var shape = this._createShape(extraGeometry, factory);
						s.applyPolygonStyle(shape, this, _style, factory);
						this.map.engine.appendChild(shape, this, _style.zIndex);
						this.extraShapes.push(shape);
					}
				}, this);
			}
		}
		
		// apply text styles
		if (textStyle) {
			// take into account all geometries produced above
		}
		*/
	},

	getBbox: function() {
		// summary:
		//		Returns the feature boundings box in the current map projection
		var geometry = this.getGeometry();
		return geometry ? (geometry.bbox ? geometry.bbox : u.bbox.get(geometry)) : null; // TODO: calculate bounding box if it's not provided
	},

	connectWithHandle: function(handle, /* String|Array? */events, /*Object|null*/ context, /*String|Function*/ method) {
		if (this.invalid) return handle;
		events = dojo.isString(events) ? [events] : events;
		
		// disconnect existing events for this handle
		var handleObj = this.handles[handle];
		if (handleObj) {
			var eventConnections = handleObj[3];
			dojo.forEach(eventConnections, function(eventConnection){
				if (eventConnection) this.map.engine.disconnect(eventConnection);
			}, this);
		}
		var eventConnections = [];
		// the format of handleObj is [events,context,method,eventConnections]
		var handleObj = [events, context, method, eventConnections];

		var numEvents = 0; // number of connected events
			method = dojo.hitch(context, method);
		dojo.forEach(events, function(event) {
			if (course.geo.events[event]) {
				var feature = this;
				eventConnections.push(
					this.map.engine.connect(this, event, u.normalizeCallback(feature, event, method))
				);
				numEvents++;
			}
			else eventConnections.push(null);
		}, this);
		if (!numEvents) return handle;
		handle = handle || u.getUniqueNumber();
		if (!this.handles[handle]) this.handles[handle] = handleObj;
		return handle;
	},

	disconnect: function(handle) {
		var handleObj = this.handles[handle];
		if (handleObj) {
			var eventConnections = handleObj[3];
			dojo.forEach(eventConnections, function(eventConnection){
				this.map.engine.disconnect(eventConnection);
			}, this);
			delete this.handles[handle];
		}
	}
});


// default methods;
var p = g.Placemark.prototype;
g.methods.Placemark = {
	render: p._render
}


var applyStyle = function(styleType, feature, geometry, style, factory) {
	switch(styleType) {
		case "point":
			factory.applyPointStyle(feature, geometry, style);
			break;
		case "line":
			factory.applyLineStyle(feature, geometry, style);
			break;
		case "polygon":
			factory.applyPolygonStyle(feature, geometry, style);
			break;
		case "text":
			factory.applyTextStyle(feature, geometry, style);
			break;
	}
};

}())

// register the constructor
course.geo.featureTypes["Feature"] = course.geo.Placemark;
course.geo.featureTypes["Placemark"] = course.geo.Placemark;
