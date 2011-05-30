dojo.provide("course.geo.gfx.Tooltip");

dojo.require("course.geo.gfx.AnimatedControl");

dojo.require("dijit.Tooltip");
dojo.require("dojox.gfx.matrix");

(function(){

	var position = ["above", "below"];

	var m = dojox.gfx.matrix, pi4 = Math.PI / 4, pi2 = Math.PI / 2;
	
	// singletons!
	var tooltip,
		onmouseoutTimeout,
		mouseOverTooltip = false;

	var showTooltip = function(/*String*/ innerHTML, /*DomNode*/ aroundNode, /*String[]?*/ position, /*Boolean*/ rtl){
		if(!tooltip) tooltip = createTooltip();
		dojo.style(tooltip.domNode, "padding", "0");
		return tooltip.show(innerHTML, aroundNode, position, rtl);
	};
	var hideTooltip = function(aroundNode){
		if(!tooltip) tooltip = createTooltip();
		return tooltip.hide(aroundNode);
	};
	var createTooltip = function() {
		// create a tooltip without a dijitTooltipConnector
		var tooltip = new dijit._MasterTooltip();
		dojo.style(tooltip.connectorNode, "display", "none");
		dojo.connect(tooltip.domNode, "onmouseover", function(){
			mouseOverTooltip = true;
			if (onmouseoutTimeout) {
				clearOnmouseoutTimeout();
			}
		});
		tooltip.connect(tooltip.domNode, "onmouseout", function(){
			mouseOverTooltip = false;
		});
		return tooltip;
	};
	var clearOnmouseoutTimeout = function() {
		clearTimeout(onmouseoutTimeout.id);
		onmouseoutTimeout = null;
	}
	
	dojo.declare("course.geo.gfx.Tooltip", course.geo.gfx.AnimatedControl, {

		// current tooltip feature
		feature: null,
		
		onmouseoutDelay: 100, // milliseconds
	
		onmouseoverTimeout: null,
		
		init: function() {
			
		},
		
		process: function(event) {
			var feature = event.feature;
			
			if (onmouseoutTimeout) clearOnmouseoutTimeout();

			if(event.type == "onmouseout") {
				if (!mouseOverTooltip) {
					onmouseoutTimeout = {
						feature: feature,
						id: setTimeout(
							dojo.hitch(this, this._onmouseout),
							this.onmouseoutDelay
						)
					};
				}
				return;
			}
			
			if (this.feature == feature) return;
			
			this.feature = feature;

			var factory = this.map.engine.getFactory(feature.type),
				featureBbox = feature.getBbox(),
				centerX = factory.getX( (featureBbox[0]+featureBbox[2])/2 ),
				centerY = factory.getY( (featureBbox[1]+featureBbox[3])/2 );
			var realMatrix = factory.group._getRealMatrix() || {xx:1,xy:0,yx:0,yy:1,dx:0,dy:0};
			var point = dojox.gfx.matrix.multiplyPoint(realMatrix, centerX, centerY);
			
			// calculate relative coordinates and the position
			var aroundRect = {type: "rect", x: point.x, y:point.y, width:0, height:0};
			
			// adjust relative coordinates to absolute, and remove fractions
			aroundRect.x += this.map.x;
			aroundRect.y += this.map.y;
			aroundRect.x = Math.round(aroundRect.x);
			aroundRect.y = Math.round(aroundRect.y);
			aroundRect.width = Math.ceil(aroundRect.width);
			aroundRect.height = Math.ceil(aroundRect.height);
			this.aroundRect = aroundRect;
			
            showTooltip(this.text(feature), this.aroundRect, position);
		},
		
		_onmouseout: function() {
			onmouseoutTimeout = null;
			this.feature = null;
            hideTooltip(this.aroundRect);
			this.aroundRect = null;
		}
	});

})();
