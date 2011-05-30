dojo.provide("course.geo.gfx.Navigation");

dojo.require("dojox.gfx.matrix");

dojo.declare("course.geo.gfx.Navigation",null, {

	moveable: null,
	wheelConnection: null,

	enable: function() {
		this.moveable = new dojox.gfx.Moveable(this.map.engine.group);
		this.enableZoom(true);
	},

	enableZoom: function(enable) {
		if (enable && !this._mouseWheelListener) {
			var wheelEventName = !dojo.isMozilla ? "onmousewheel" : "DOMMouseScroll";
			this.wheelConnection = this.map.engine.surface.connect(wheelEventName, this, this._onWheel);
		}
	},

	_onWheel: function(mouseEvent) {
		// prevent browser interaction
		dojo.stopEvent(mouseEvent);
		
		// position relative to map container
		var x = mouseEvent.pageX - this.map.x,
			y = mouseEvent.pageY - this.map.y;

		var matrix = this.map.engine.group._getRealMatrix() || {xx:1,xy:0,yx:0,yy:1,dx:0,dy:0};
		
		// zoom increment power 
		var power  = mouseEvent[ dojo.isMozilla ? "detail" : "wheelDelta" ] / (dojo.isMozilla ? -3 : 120) ;
		var scaleFactor = Math.pow(1.2, power);
		
		this.map.engine.group.setTransform([
			{xx:scaleFactor,yy:scaleFactor, dx: x*(1-scaleFactor), dy: y*(1-scaleFactor)},
			matrix
		]);
	}
});