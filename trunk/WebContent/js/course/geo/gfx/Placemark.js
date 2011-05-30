dojo.provide("course.geo.gfx.Placemark");

dojo.require("course.geo.common.Placemark");

(function() {

var g = course.geo,
	cp = g.common.Placemark,
	s = course.geo.styling;

dojo.declare("course.geo.gfx.Placemark", course.geo.common.Placemark, {
	
	constructor: function(kwArgs) {
		dojo.mixin(this, kwArgs);
		this.polygons = this.group.createGroup();
		this.lines = this.group.createGroup();
		this.points = this.group.createGroup();
	},
	
	prerender: function() {
		this.lengthDenominator = (this.group._getRealMatrix()||{xx:1}).xx;
	},
	
	moveTo: function(path, point) {
		path.moveTo(this.getX(point[0]), this.getY(point[1]));
	},
	
	lineTo: function(path, point) {
		path.lineTo(this.getX(point[0]), this.getY(point[1]));
	},
	
	getX: function(x) {
		return x-this.map.extent[0];
	},
	
	getY: function(y) {
		return this.map.extent[3]-y;
	},
	
	makePoint: function(feature, coordinates) {
		// do nothing
		// point shape are created in this.applyPointStyle
		return null;
	},
	
	makeLineString: function(feature, coordinates, path) {
		if (!path) path = this.lines.createPath();
		this.moveTo(path, coordinates[0]);
		for(var i=1; i<coordinates.length; i++) {
			this.lineTo(path, coordinates[i]);
		}
		return path;
	},

	makePolygon: function(feature, coordinates, path) {
		if (!path) path = this.polygons.createPath();
		dojo.forEach(coordinates, function(lineStringCoords){
			this.moveTo(path, lineStringCoords[0]);
			for(var i=1; i<lineStringCoords.length; i++) {
				this.lineTo(path, lineStringCoords[i]);
			}
		}, this);
		return path;
	},
	
	makeMultiLineString: function(feature, coordinates) {
		var path = this.lines.createPath();
		dojo.forEach(coordinates, function(lineStringCoords){
			this.makeLineString(feature, lineStringCoords, path);
		}, this);
		return path;
	},
	
	makeMultiPolygon: function(feature, coordinates) {
		var path = this.polygons.createPath();
		dojo.forEach(coordinates, function(polygonCoords){
			this.makePolygon(feature, polygonCoords, path);
		}, this);
		return path;
	},
	
	applyPointStyle: function(feature, geometry, calculatedStyle) {
		var coordinates = geometry.coordinates,
			specificStyles = calculatedStyle["point"];
		
		// disconnect connected events if any
		// store their handles for reconnection
		var handles = {};
		for (var handle in feature.handles) {
			handles[handle] = feature.handles[handle];
			feature.disconnect(handle);
		}
		
		// remove existing shapes
		if (feature.baseShapes.length) {
			for (var i=feature.baseShapes.length-1; i>=0; i--) {
				// remove shape
				feature.baseShapes.pop().removeShape();
			}
		}
		
		if (specificStyles) {
			dojo.forEach(specificStyles, function(specificStyle){
				var shape = this._createPointShape(coordinates, calculatedStyle, specificStyle);
				if (shape) feature.baseShapes.push(shape);
			}, this);
		}
		else {
			var shape = this._createPointShape(coordinates, calculatedStyle, null);
			if (shape) feature.baseShapes.push(shape);
		}
		
		// reconnect events
		for (var handle in handles) {
			var handleObj = handles[handle],
				events = handleObj[0],
				context = handleObj[1],
				method = handleObj[2];
			feature.connectWithHandle(handle, events, context, method);
		}
	},
	
	_createPointShape: function(coordinates, calculatedStyle, specificStyle) {
		var type,
			shapeType = cp.get("shape", calculatedStyle, specificStyle),
			src = cp.get("src", calculatedStyle, specificStyle),
			width,
			height,
			scale = cp.get("scale", calculatedStyle, specificStyle),
			transform = [dojox.gfx.matrix.translate(this.getX(coordinates[0]), this.getY(coordinates[1]))];
		shape = null;

		// find width and height
		if (specificStyle) {
			width = specificStyle.width ? specificStyle.width : specificStyle.size;
			height = specificStyle.height ? specificStyle.height : specificStyle.size;
			// specific style for the following code always overrides calculatedStyle
			if (specificStyle.type) type = specificStyle.type;
			else {
				if (specificStyle.shape) type = "shape";
				else if (specificStyle.src) type = "image";
			}
		}
		else {
			if (calculatedStyle.type) type = calculatedStyle.type;
			else {
				if (calculatedStyle.shape) type = "shape";
				else if (calculatedStyle.src) type = "image";
			}
		}
		if (!width) width = calculatedStyle.width ? calculatedStyle.width : calculatedStyle.size;
		if (!height) height = calculatedStyle.height ? calculatedStyle.height : calculatedStyle.size;
		
		if (!scale) scale = 1;

		if ( type == "shape" && shapes[shapeType]) {
			var shapeDef = shapes[shapeType],
				size = shapeType=="circle" ? 2 : shapeDef.size,
				_scale = scale/this.lengthDenominator/size;

			transform.push(dojox.gfx.matrix.scale(_scale*width, _scale*height));
			
			if (shapeType=="circle") {
				shape = this.points.createCircle({cx:0, cy:0, r:1});
			}
			else {
				shape = this.points.createPolyline(shapeDef.points);
			}
			applyFill(shape, calculatedStyle, specificStyle);
			applyStroke(shape, calculatedStyle, specificStyle, size/height/scale);
		}
		else if (type == "image" && src) {
			var imageDef = {
				type: "image",
				src: src,
				width: width,
				height: height,
				x: (specificStyle && specificStyle.x !== undefined) ? specificStyle.x : -width/2,
				y: (specificStyle && specificStyle.y !== undefined) ? specificStyle.y : -height/2
			}
			shape = this.points.createImage(imageDef);
			transform.push(dojox.gfx.matrix.scale(1/this.lengthDenominator*scale));
		}
		
		if (shape) shape.setTransform(transform);
		
		return shape;
	},
	
	applyLineStyle: function(feature, geometry, calculatedStyle) {
		var specificStyles = calculatedStyle["line"],
			baseShapes = feature.baseShapes;
			
		this._updateShapes(feature, geometry, calculatedStyle, specificStyles);

		if (specificStyles) {
			dojo.forEach(specificStyles, function(specificStyle, i){
				// index of specificStyles corresponds to the index of feature.baseShapes
				this._applyLineStyle(baseShapes[i], calculatedStyle, specificStyle);
			}, this);
		}
		else {
			this._applyLineStyle(baseShapes[0], calculatedStyle, null);
		}
	},
	
	_applyLineStyle: function(shape, calculatedStyle, specificStyle) {
		applyStroke(shape, calculatedStyle, specificStyle, 1/this.lengthDenominator);
	},
	
	applyPolygonStyle: function(feature, geometry, calculatedStyle) {
		var specificStyles = calculatedStyle["polygon"],
			baseShapes = feature.baseShapes;
			
		this._updateShapes(feature, geometry, calculatedStyle, specificStyles);

		if (specificStyles) {
			dojo.forEach(specificStyles, function(specificStyle, i){
				// index of specificStyles corresponds to the index of feature.baseShapes
				this._applyPolygonStyle(baseShapes[i], calculatedStyle, specificStyle);
			}, this);
		}
		else {
			this._applyPolygonStyle(baseShapes[0], calculatedStyle, null);
		}
	},
	
	_applyPolygonStyle: function(shape, calculatedStyle, specificStyle) {
		applyFill(shape, calculatedStyle, specificStyle);
		applyStroke(shape, calculatedStyle, specificStyle, 1/this.lengthDenominator);
	},
	
	_updateShapes: function(feature, geometry, calculatedStyle, specificStyles) {
		var baseShapes = feature.baseShapes,
			handles = feature.handles,
			numSpecificStyles = specificStyles ? specificStyles.length : 1,
			numBaseShapes = baseShapes.length;

		if (numSpecificStyles > numBaseShapes) {
			// add missing shapes
			var coordinates = geometry.coordinates;
			for (var i=numBaseShapes; i<numSpecificStyles; i++) {
				var shape = this.createShape(feature, geometry);
				// connect events to the shape
				for (var handle in handles) {
					var events = handles[handle][0],
						context = handles[handle][1],
						method = handles[handle][2],
						eventConnections = handles[handle][3];
					dojo.forEach(events, function(event, eventIndex){
						eventConnections[eventIndex].push( shape.connect(event, g.util.normalizeCallback(feature, event, context, method)) );
					});
				}
				baseShapes.push(shape);
			}
		}
		else if (numSpecificStyles < numBaseShapes) {
			// remove excessive shapes
			for (var i=numBaseShapes-1; i>=numSpecificStyles; i--) {
				var shape = baseShapes.pop();
				shape.removeShape();
				// disconnect events from the shape
				for (var handle in handles) {
					var events = handles[handle][0],
						eventConnections = handles[handle][3];
					dojo.forEach(events, function(event, eventIndex){
						shape.disconnect( eventConnections[eventIndex].pop() );
					});
				}
			}
			
		}
	}
});

var applyFill = function(shape, calculatedStyle, specificStyle) {
	var fill = cp.get("fill", calculatedStyle, specificStyle);
	if (fill) shape.setFill(fill);
};

var applyStroke = function(shape, calculatedStyle, specificStyle, widthMultiplier) {
	if (dojox.gfx.renderer == "vml") widthMultiplier=1;
	var stroke = cp.get("stroke", calculatedStyle, specificStyle);
	var strokeWidth = cp.get("strokeWidth", calculatedStyle, specificStyle);
	if (strokeWidth!==undefined || stroke) {
		var strokeDef = strokeWidth===0 ? null : {};
		if (strokeDef) {
			if (strokeWidth) strokeDef.width = strokeWidth*widthMultiplier;
			if (stroke) strokeDef.color = stroke;
		}
		shape.setStroke(strokeDef);
	}
};

// center of each shape must be 0,0
var shapes = {
	circle: 1, // a dummy value
	star: {
		size: 1000,
		points: [0,-476, 118,-112, 500,-112, 191,112, 309,476, 0,251, -309,476, -191,112, -500,-112, -118,-112, 0,-476]
	},
	cross: {
		size: 10,
		points: [-1,-5, 1,-5, 1,-1, 5,-1, 5,1, 1,1, 1,5, -1,5, -1,1, -5,1, -5,-1, -1,-1, -1,-5]
	},
	x: {
		size: 100,
		points: [-50,-50, -25,-50, 0,-15, 25,-50, 50,-50, 15,0, 50,50, 25,50, 0,15, -25,50, -50,50, -15,0, -50,-50]
	},
	square: {
		size: 2,
		points: [-1,-1, -1,1, 1,1, 1,-1, -1,-1]
	},
	triangle: {
		size: 10,
		points: [-5,5, 5,5, 0,-5, -5,5]
	}
};

}());
