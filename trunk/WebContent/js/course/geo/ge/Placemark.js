dojo.provide("course.geo.ge.Placemark");

(function() {
	
var shapeSize = 50.;

course.geo.ge.methods.Placemark = {
	render: function(stylingOnly, mode) {
		google.earth.executeBatch(this.engine.ge, dojo.hitch(this, function(){
			this._render(stylingOnly, mode);
		}));
	}
};

var g = course.geo,
	cp = g.common.Placemark;

dojo.declare("course.geo.ge.Placemark", null, {
	
	constructor: function(kwArgs) {
		dojo.mixin(this, kwArgs);
	},
	
	createPlacemark: function() {
		var placemark = this.ge.createPlacemark('');
		placemark.setStyleSelector(this.ge.createStyle(''));
		return placemark;
	},
	
	makePoint: function(feature, coordinates) {
		var placemark = this.createPlacemark();
		var point = this.ge.createPoint('');
		point.setLatLngAlt (coordinates[1], coordinates[0], 200);
		placemark.setGeometry(point);
		return placemark;
	},
	
	makeLineString: function(feature, coordinates) {
		var placemark = this.createPlacemark();
		var lineString = this.ge.createLineString('');
		this._makeLineString(lineString, coordinates);
		placemark.setGeometry(lineString);
		return placemark;
	},
	
	_makeLineString: function(lineString, coordinates) {
		dojo.forEach(coordinates, function(point, i){
			lineString.getCoordinates().pushLatLngAlt(point[1], point[0], 150/*TODO: 3D*/);
		}, this);
	},

	makePolygon: function(feature, coordinates) {
		var placemark = this.createPlacemark();
		var polygon = this.ge.createPolygon('');
		this._makePolygon(polygon, coordinates);
		placemark.setGeometry(polygon);
		return placemark;
	},
	
	_makePolygon: function(polygon, coordinates) {
		dojo.forEach(coordinates, function(lineStringCoords, i){
			var linearRing = this.ge.createLinearRing('');
			dojo.forEach(lineStringCoords, function(point){
				linearRing.getCoordinates().pushLatLngAlt(point[1], point[0], 100/*TODO: 3D*/);
			});
			if (!i) polygon.setOuterBoundary(linearRing);
			else polygon.getInnerBoundaries().appendChild(linearRing);
		}, this);
	},
	
	makeMultiLineString: function(feature, coordinates) {
		var placemark = this.createPlacemark();
		var multiLineString = this.ge.createMultiGeometry('');
		placemark.setGeometry(multiLineString);
		dojo.forEach(coordinates, function(lineStringCoords){
			var lineString = this.ge.createLineString('');
			this._makeLineString(lineString, lineStringCoords);
			multiLineString.getGeometries().appendChild(lineString);
		}, this);
		return placemark;
	},
	
	makeMultiPolygon: function(feature, coordinates) {
		var placemark = this.createPlacemark();
		var multiPolygon = this.ge.createMultiGeometry('');
		placemark.setGeometry(multiPolygon);
		
		dojo.forEach(coordinates, function(polygonCoords){
			var polygon = this.ge.createPolygon('');
			this._makePolygon(polygon, polygonCoords);
			multiPolygon.getGeometries().appendChild(polygon);
		}, this);
		return placemark;
	},
	
	applyPointStyle: function(placemark, feature, calculatedStyle, specificStyle, factory) {
		var type = specificStyle.type,
			scale = cp.get("scale", calculatedStyle, specificStyle),
			normalStyle = getNormalStyle(placemark),
			icon = this.ge.createIcon(''),
			setIcon = true,
			href;
			
		// find width and height
		if (specificStyle) {
			width = specificStyle.width ? specificStyle.width : specificStyle.size;
			height = specificStyle.height ? specificStyle.height : specificStyle.size;
		}
		if (!width) width = calculatedStyle.width ? calculatedStyle.width : calculatedStyle.size;
		if (!height) height = calculatedStyle.height ? calculatedStyle.height : calculatedStyle.size;
		
		if (!scale) scale = 1;
		
		var iconActualSize = scale*Math.min(width,height);
		var kmlIconScale = iconActualSize/shapeSize;

		if (type == "shape" && shapes[specificStyle.shape]) {
			href = shapeIconsUrl + shapes[specificStyle.shape];
		}
		else if (type == "image") {
			href = isRelativeUrl(specificStyle.src) ? baseUrl+specificStyle.src : specificStyle.src;
		}
		else setIcon = false;

		if (setIcon) {
			icon.setHref(href);
			var iconStyle = normalStyle.getIconStyle();
			iconStyle.setIcon(icon);
			iconStyle.setScale(kmlIconScale);
			if (type == "shape") {
				var fill = cp.get("fill", calculatedStyle, specificStyle);
				iconStyle.getColor().set(convertColor(fill));
			}
		}
		return placemark;
	},
	
	applyLineStyle: function(placemark, feature, calculatedStyle, specificStyle, factory) {
		var normalStyle = getNormalStyle(placemark);
		applyStroke(normalStyle, calculatedStyle, specificStyle);
		return placemark;
	},
	
	applyPolygonStyle: function(placemark, feature, calculatedStyle, specificStyle, factory) {
		//var normalStyle = this.ge.createStyle('');
		var normalStyle = getNormalStyle(placemark);
		var fill = cp.get("fill", calculatedStyle, specificStyle);
		normalStyle.getPolyStyle().getColor().set(convertColor(fill));
		applyStroke(normalStyle, calculatedStyle, specificStyle);
		return placemark;
	}
});

// baseUrl is needed to set href for KmlIcon
var baseUrl = window.location.href.substring(0, window.location.href.lastIndexOf('/')+1);
// relative path to the shape icons (square, star, etc), something like ../../../somepath/
// we'll calculate the absolute path in the next step with an anonymous function
var shapeIconsUrl = dojo.moduleUrl("course", "geo/ge/resources/icons/").uri;
shapeIconsUrl = (function(relativePath) {
	// find the number of occurances of ../
	var depth = relativePath.match(/\.\.\//g);
	depth = depth ? depth.length : 0;
	var _depth = depth;
	var position = baseUrl.length-1; // omit trailing slash
	while(depth) {
		position--;
		if (baseUrl.charAt(position)=="/") depth--;
	}
	return baseUrl.substring(0,position) + "/" +relativePath.substring(3*_depth);
})(shapeIconsUrl);

function convertColor(c) {
	c = new dojo.Color(c).toHex();
	return "ff"+c.charAt(5)+c.charAt(6)+c.charAt(3)+c.charAt(4)+c.charAt(1)+c.charAt(2);
}

var getNormalStyle = function(placemark) {
	return placemark.getStyleSelector();
	var styleSelector = placemark.getStyleSelector();
	var normalStyle = styleSelector.getNormalStyle();
	return normalStyle;
}

var applyStroke = function(kmlStyle, calculatedStyle, specificStyle) {
	var lineStyle = kmlStyle.getLineStyle();
	var stroke = cp.get("stroke", calculatedStyle, specificStyle);
	var strokeWidth = cp.get("strokeWidth", calculatedStyle, specificStyle);
	lineStyle.getColor().set(convertColor(stroke));
	lineStyle.setWidth(strokeWidth);
}

var isRelativeUrl = function(url) {
	return url.substr(0,4)=="http" ? false : true;
}

var shapes = {
	circle: "circle.png",
	star: "star.png",
	cross: "cross.png",
	x: "x.png",
	square: "square.png",
	triangle: "triangle.png"
};

}())
